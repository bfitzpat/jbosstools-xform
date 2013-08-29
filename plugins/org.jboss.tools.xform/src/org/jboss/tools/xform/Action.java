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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.jcr.Node;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.sun.xml.xsom.XSSchema;
import com.sun.xml.xsom.XSSchemaSet;
import com.sun.xml.xsom.parser.XSOMParser;

/**
 * 
 */
public class Action implements IObjectActionDelegate {
    
    private final List< IFile > files = new ArrayList<>();
    
    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    @Override
    public void run( final IAction action ) {
        for ( final IFile iFile : files )
            // jpav Should we import source into MS first, then parse via MS?
            if ( "xsd".equalsIgnoreCase( iFile.getFileExtension() ) ) try {
                final XSOMParser parser = new XSOMParser();
                parser.setErrorHandler( new ErrorHandler() {
                    
                    @Override
                    public void error( final SAXParseException exception ) throws SAXException {
                        MessageDialog.openError( null, "Data Transformation Error", exception.getMessage() );
                        throw exception;
                    }
                    
                    @Override
                    public void fatalError( final SAXParseException exception ) throws SAXException {
                        MessageDialog.openError( null, "Data Transformation Fatal Error", exception.getMessage() );
                        throw exception;
                    }
                    
                    @Override
                    public void warning( final SAXParseException exception ) throws SAXException {
                        MessageDialog.openWarning( null, "Data Transformation Warning", exception.getMessage() );
                        throw exception;
                    }
                } );
                parser.setEntityResolver( new EntityResolver() {
                    
                    @Override
                    public InputSource resolveEntity( final String publicId,
                                                      final String systemId ) throws IOException {
                        final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile( Path.fromPortableString( systemId ) );
                        if ( file != null ) try {
                            final InputSource src = new InputSource( file.getContents() );
                            src.setSystemId( systemId );
                            return src;
                        } catch ( final CoreException error ) {
                            throw new IOException( error );
                        }
                        return null;
                    }
                } );
                final InputSource src = new InputSource( iFile.getContents() );
                src.setSystemId( iFile.getFullPath().toPortableString() );
                parser.parse( src );
                final XSSchemaSet set = parser.getResult();
                if ( set == null ) return;
                final Node root = Activator.plugin().workspaceRepositorySession().getRootNode();
                for ( final Iterator< XSSchema > iter = set.iterateSchema(); iter.hasNext(); ) {
                    final Node objNode = root.addNode( "schema", "xform:Object" );
                    objNode.setProperty( "xform:primaryType", "xs:Schema" );
                    final Node attrNode = objNode.addNode( "xs:targetNamespace", "xform:Containment" );
                    attrNode.setProperty( "xform:type", "xs:string" );
                    attrNode.setProperty( "xform:value", iter.next().getTargetNamespace() );
                }
            } catch ( final RuntimeException error ) {
                throw error;
            } catch ( final Exception error ) {
                throw new RuntimeException( error );
            }
            else MessageDialog.openError( null, "Data Transformation Error", "Unsupported file type: " + iFile.getFileExtension() );
    }
    
    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
     */
    @Override
    public void selectionChanged( final IAction action,
                                  final ISelection selection ) {
        files.clear();
        if ( selection instanceof IStructuredSelection ) for ( final Object obj : ( ( IStructuredSelection ) selection ).toList() )
            files.add( ( IFile ) obj );
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
