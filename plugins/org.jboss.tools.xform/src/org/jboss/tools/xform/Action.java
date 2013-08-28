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

import java.util.Iterator;

import javax.jcr.Node;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import com.sun.xml.xsom.XSSchema;
import com.sun.xml.xsom.parser.XSOMParser;

/**
 * 
 */
public class Action implements IObjectActionDelegate {
    
    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    @Override
    public void run( final IAction action ) {}
    
    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
     */
    @Override
    public void selectionChanged( final IAction action,
                                  final ISelection selection ) {
        if ( selection.isEmpty() ) return;
        if ( selection instanceof IStructuredSelection ) for ( final Object obj : ( ( IStructuredSelection ) selection ).toList() ) {
            // jpav Should we import source into MS first, then parse via MS?
            final IFile iFile = ( IFile ) obj;
            if ( "xsd".equalsIgnoreCase( iFile.getFileExtension() ) ) try {
                final XSOMParser parser = new XSOMParser();
                parser.parse( iFile.getContents() );
                final Node root = Activator.plugin().workspaceRepositorySession().getRootNode();
                for ( final Iterator< XSSchema > iter = parser.getResult().iterateSchema(); iter.hasNext(); )
                    root.addNode( "schema", "xform:Object" );
            } catch ( final RuntimeException error ) {
                throw error;
            } catch ( final Exception error ) {
                throw new RuntimeException( error );
            }
            else MessageDialog.openError( null, "Error", "Unsupported file type: " + iFile.getFileExtension() );
        }
    }
    
    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction, org.eclipse.ui.IWorkbenchPart)
     */
    @Override
    public void setActivePart( final IAction action,
                               final IWorkbenchPart targetPart ) {}
}
