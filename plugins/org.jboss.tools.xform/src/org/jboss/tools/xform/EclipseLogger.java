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

import java.text.MessageFormat;

import org.eclipse.core.runtime.IStatus;
import org.modeshape.common.i18n.I18nResource;
import org.modeshape.common.logging.Logger;

/**
 * The <code>EclipseLogger</code> class provides an <code>org.modeshape.common.logging.Logger</code> implementation that uses the
 * Eclipse logger.
 */
public final class EclipseLogger extends Logger {
    
    private final String name;
    
    /**
     * @param name
     */
    public EclipseLogger( final String name ) {
        this.name = name;
    }
    
    /**
     * {@inheritDoc}
     * 
     * @see org.modeshape.common.logging.Logger#debug(java.lang.String, java.lang.Object[])
     */
    @Override
    public void debug( final String message,
                       final Object... params ) {
        debug( null, message, params );
    }
    
    /**
     * {@inheritDoc}
     * 
     * @see org.modeshape.common.logging.Logger#debug(java.lang.Throwable, java.lang.String, java.lang.Object[])
     */
    @Override
    public void debug( final Throwable error,
                       String message,
                       final Object... params ) {
        message = message.replace( '\'', '"' ); // jpav Temporary until MS fixed
        if ( isDebugEnabled() ) Activator.log( IStatus.INFO, MessageFormat.format( message, params ), error );
    }
    
    /**
     * {@inheritDoc}
     * 
     * @see org.modeshape.common.logging.Logger#error(org.modeshape.common.i18n.I18nResource, java.lang.Object[])
     */
    @Override
    public void error( final I18nResource message,
                       final Object... params ) {
        error( null, message, params );
    }
    
    /**
     * {@inheritDoc}
     * 
     * @see org.modeshape.common.logging.Logger#error(java.lang.Throwable, org.modeshape.common.i18n.I18nResource,
     *      java.lang.Object[])
     */
    @Override
    public void error( final Throwable error,
                       final I18nResource message,
                       final Object... params ) {
        if ( isErrorEnabled() ) Activator.log( IStatus.ERROR, message.text( params ), error );
    }
    
    /**
     * {@inheritDoc}
     * 
     * @see org.modeshape.common.logging.Logger#getName()
     */
    @Override
    public String getName() {
        return this.name;
    }
    
    /**
     * {@inheritDoc}
     * 
     * @see org.modeshape.common.logging.Logger#info(org.modeshape.common.i18n.I18nResource, java.lang.Object[])
     */
    @Override
    public void info( final I18nResource message,
                      final Object... params ) {
        info( null, message, params );
    }
    
    /**
     * {@inheritDoc}
     * 
     * @see org.modeshape.common.logging.Logger#info(java.lang.Throwable, org.modeshape.common.i18n.I18nResource,
     *      java.lang.Object[])
     */
    @Override
    public void info( final Throwable error,
                      final I18nResource message,
                      final Object... params ) {
        if ( isInfoEnabled() ) Activator.log( IStatus.INFO, message.text( params ), error );
    }
    
    /**
     * {@inheritDoc}
     * 
     * @see org.modeshape.common.logging.Logger#isDebugEnabled()
     */
    @Override
    public boolean isDebugEnabled() {
        return isInfoEnabled();
    }
    
    /**
     * {@inheritDoc}
     * 
     * @see org.modeshape.common.logging.Logger#isErrorEnabled()
     */
    @Override
    public boolean isErrorEnabled() {
        return true;
    }
    
    /**
     * {@inheritDoc}
     * 
     * @see org.modeshape.common.logging.Logger#isInfoEnabled()
     */
    @Override
    public boolean isInfoEnabled() {
        return Activator.infoLoggingEnabled();
    }
    
    /**
     * {@inheritDoc}
     * 
     * @see org.modeshape.common.logging.Logger#isTraceEnabled()
     */
    @Override
    public boolean isTraceEnabled() {
        return isInfoEnabled();
    }
    
    /**
     * {@inheritDoc}
     * 
     * @see org.modeshape.common.logging.Logger#isWarnEnabled()
     */
    @Override
    public boolean isWarnEnabled() {
        return true;
    }
    
    /**
     * {@inheritDoc}
     * 
     * @see org.modeshape.common.logging.Logger#trace(java.lang.String, java.lang.Object[])
     */
    @Override
    public void trace( final String message,
                       final Object... params ) {
        trace( null, message, params );
    }
    
    /**
     * {@inheritDoc}
     * 
     * @see org.modeshape.common.logging.Logger#trace(java.lang.Throwable, java.lang.String, java.lang.Object[])
     */
    @Override
    public void trace( final Throwable t,
                       final String message,
                       final Object... params ) {
        if ( isTraceEnabled() ) debug( message, t );
    }
    
    /**
     * {@inheritDoc}
     * 
     * @see org.modeshape.common.logging.Logger#warn(org.modeshape.common.i18n.I18nResource, java.lang.Object[])
     */
    @Override
    public void warn( final I18nResource message,
                      final Object... params ) {
        warn( null, message, params );
    }
    
    /**
     * {@inheritDoc}
     * 
     * @see org.modeshape.common.logging.Logger#warn(java.lang.Throwable, org.modeshape.common.i18n.I18nResource,
     *      java.lang.Object[])
     */
    @Override
    public void warn( final Throwable error,
                      final I18nResource message,
                      final Object... params ) {
        if ( isWarnEnabled() ) Activator.log( IStatus.WARNING, message.text( params ), error );
    }
}