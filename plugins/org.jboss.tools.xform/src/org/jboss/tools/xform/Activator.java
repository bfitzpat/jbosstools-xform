package org.jboss.tools.xform;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * Controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {
    
    /**
     * The plug-in ID
     */
    public static final String PLUGIN_ID = "jbosstools-xform"; //$NON-NLS-1$
    
    // The shared instance
    private static Activator plugin;
    
    /**
     * @return the shared instance
     */
    public static Activator plugin() {
        return plugin;
    }
    
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
    }
    
    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop( final BundleContext context ) throws Exception {
        plugin = null;
        super.stop( context );
    }
}
