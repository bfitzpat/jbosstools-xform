/*
 * See the COPYRIGHT.txt file distributed with this work for information
 * regarding copyright ownership.
 *
 * This software is made available by Red Hat, Inc. under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution and is
 * available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * See the AUTHORS.txt file in the distribution for a full listing of
 * individual contributors.
 */
package org.jboss.tools.xform;

import java.lang.reflect.InvocationTargetException;

import javax.jcr.Session;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.modeshape.common.collection.Problem;
import org.modeshape.common.collection.Problems;
import org.modeshape.jcr.ModeShapeEngine;
import org.modeshape.jcr.NoSuchRepositoryException;
import org.modeshape.jcr.RepositoryConfiguration;
import org.modeshape.jcr.api.Repository;
import org.osgi.framework.BundleContext;

/**
 * Controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {
    
    // The singleton instance of this plug-in
    private static Activator plugin;
    
    /**
     * @return the singleton instance of this plug-in.
     */
    public static Activator plugin() {
        return plugin;
    }
    
    private boolean infoEnabled;
    
    private ModeShapeEngine modeShape;
    private Session session;
    
    /**
     * @return <code>true</code> if information-level logging is enabled
     */
    public boolean infoLoggingEnabled() {
        return infoEnabled;
    }
    
    /**
     * @param severity
     * @param message
     * @param throwable
     */
    public void log( final int severity,
                     final String message,
                     final Throwable throwable ) {
        if ( severity == IStatus.INFO && !infoLoggingEnabled() ) return;
        if ( plugin == null ) {
            if ( severity == IStatus.ERROR ) {
                System.err.println( message );
                if ( throwable != null ) System.err.println( throwable );
            } else {
                System.out.println( message );
                if ( throwable != null ) System.out.println( throwable );
            }
        } else getLog().log( new Status( severity, getBundle().getSymbolicName(), message, throwable ) );
    }
    
    /**
     * @param severity
     * @param throwable
     */
    public void log( final int severity,
                     final Throwable throwable ) {
        log( severity, throwable.getMessage(), throwable );
    }
    
    /**
     * @param throwable
     */
    public void log( final Throwable throwable ) {
        log( IStatus.ERROR, throwable );
    }
    
    /**
     * @param enabled
     *            <code>true</code> if information-level logging is enabled
     */
    public void setInfoEnabled( final boolean enabled ) {
        infoEnabled = enabled;
    }
    
    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start( final BundleContext context ) throws Exception {
        super.start( context );
        plugin = this;
    }
    
    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop( final BundleContext context ) throws Exception {
        // Close the workspace repository session and shutdown ModeShape
        if ( session != null ) session.logout();
        if ( modeShape != null ) modeShape.shutdown().get();
        // Stop plug-in
        plugin = null;
        super.stop( context );
    }
    
    /**
     * @return the session to the ModeShape workspace repository.
     */
    public Session workspaceRepositorySession() {
        if ( modeShape == null ) try {
            final ProgressMonitorDialog dlg = new ProgressMonitorDialog( null );
            dlg.open();
            dlg.getProgressMonitor().setTaskName( "Starting workspace repository..." );
            dlg.run( false, false, new IRunnableWithProgress() {
                
                @SuppressWarnings( "synthetic-access" )
                @Override
                public void run( final IProgressMonitor monitor ) throws InvocationTargetException {
                    try {
                        modeShape = new ModeShapeEngine();
                        modeShape.start();
                        final RepositoryConfiguration config = RepositoryConfiguration.read( "workspaceRepository.json" );
                        final Problems problems = config.validate();
                        if ( problems.hasProblems() ) for ( final Problem problem : problems ) {
                            int severity;
                            switch ( problem.getStatus() ) {
                                case ERROR:
                                    severity = IStatus.ERROR;
                                    break;
                                case WARNING:
                                    severity = IStatus.WARNING;
                                    break;
                                default:
                                    severity = IStatus.INFO;
                            }
                            log( severity, problem.getThrowable() );
                        }
                        if ( problems.hasErrors() ) throw new RuntimeException( problems.iterator().next().getThrowable() );
                        Repository repository;
                        try {
                            repository = modeShape.getRepository( config.getName() );
                        } catch ( final NoSuchRepositoryException err ) {
                            repository = modeShape.deploy( config );
                        }
                        session = repository.login( "default" );
                    } catch ( final Exception error ) {
                        throw new InvocationTargetException( error );
                    }
                }
            } );
        } catch ( InvocationTargetException | InterruptedException error ) {
            throw new RuntimeException( error );
        }
        return session;
    }
}
