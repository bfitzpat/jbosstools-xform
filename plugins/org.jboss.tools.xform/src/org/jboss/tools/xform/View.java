package org.jboss.tools.xform;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

/**
 * 
 */
public class View extends ViewPart {
    
    /**
     * 
     */
    public View() {}
    
    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createPartControl( final Composite parent ) {
        Session session = null;
        try {
            session = Activator.repository().login( "default" );
        } catch ( final RepositoryException error ) {
            Activator.log( error );
        } finally {
            if ( session != null ) session.logout();
        }
    }
    
    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
     */
    @Override
    public void setFocus() {}
    
}
