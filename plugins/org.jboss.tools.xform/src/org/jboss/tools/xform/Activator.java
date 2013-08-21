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

import javax.jcr.Repository;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.modeshape.common.collection.Problem;
import org.modeshape.common.collection.Problems;
import org.modeshape.jcr.ModeShapeEngine;
import org.modeshape.jcr.NoSuchRepositoryException;
import org.modeshape.jcr.RepositoryConfiguration;
import org.osgi.framework.BundleContext;

/**
 * Controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {
    
    private static final String MODESHAPE_CONFIG = "modeshape.json";
    
    // The shared instance
    private static Activator plugin;
    
    private static boolean infoEnabled;
    
    /**
     * @return <code>true</code> if information-level logging is enabled
     */
    public static boolean infoLoggingEnabled() {
        return infoEnabled;
    }
    
    /**
     * @param severity
     * @param message
     * @param throwable
     */
    public static void log( final int severity,
                            final String message,
                            final Throwable throwable ) {
        if ( !infoLoggingEnabled() ) return;
        if ( plugin == null ) {
            if ( severity == IStatus.ERROR ) {
                System.err.println( message );
                if ( throwable != null ) System.err.println( throwable );
            } else {
                System.out.println( message );
                if ( throwable != null ) System.out.println( throwable );
            }
        } else plugin.getLog().log( new Status( severity, plugin.getBundle().getSymbolicName(), message, throwable ) );
    }
    
    /**
     * @param severity
     * @param throwable
     */
    public static void log( final int severity,
                            final Throwable throwable ) {
        log( severity, throwable.getMessage(), throwable );
    }
    
    /**
     * @param throwable
     */
    public static void log( final Throwable throwable ) {
        log( IStatus.ERROR, throwable );
    }
    
    /**
     * @return the ModeShape repository
     */
    public static Repository repository() {
        return plugin.repository;
    }
    
    /**
     * @param enabled
     *            <code>true</code> if information-level logging is enabled
     */
    public static void setInfoEnabled( final boolean enabled ) {
        infoEnabled = enabled;
    }
    
    private ModeShapeEngine modeShape;
    
    private Repository repository;
    
    /**
     * 
     */
    public Activator() {}
    
    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start( final BundleContext context ) throws Exception {
        super.start( context );
        plugin = this;
        // Start ModeShape and deploy internal repository
        modeShape = new ModeShapeEngine();
        modeShape.start();
        final RepositoryConfiguration config = RepositoryConfiguration.read( MODESHAPE_CONFIG );
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
        try {
            repository = modeShape.getRepository( config.getName() );
        } catch ( final NoSuchRepositoryException err ) {
            repository = modeShape.deploy( config );
        }
    }
    
    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop( final BundleContext context ) throws Exception {
        // Shutdown ModeShape and all of its repositories
        modeShape.shutdown().get();
        // Stop plug-in
        plugin = null;
        super.stop( context );
    }
}
