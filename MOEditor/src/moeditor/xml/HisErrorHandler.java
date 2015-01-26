/* ----------------------------------------------------------------------------
* Copyright (C) 2014      European Space Agency
*                         European Space Operations Centre
*                         Darmstadt
*                         Germany
* ----------------------------------------------------------------------------
* System                : CCSDS MO Graphical Service Editor
* ----------------------------------------------------------------------------
* Licensed under the European Space Agency Public License, Version 2.0
* You may not use this file except in compliance with the License.
*
* Except as expressly set forth in this License, the Software is provided to
* You on an "as is" basis and without warranties of any kind, including without
* limitation merchantability, fitness for a particular purpose, absence of
* defects or errors, accuracy or non-infringement of intellectual property rights.
* 
* See the License for the specific language governing permissions and
* limitations under the License.
* ----------------------------------------------------------------------------
*/

package moeditor.xml;

import moeditor.MultiPageEditor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class HisErrorHandler implements ErrorHandler {
	
	public String message = "AllOK";
	private IFile iFile = null;
	private IDocument iDocument = null;
	
	public static final String ERROR_MARKER_ID = "MOEditor.xmlerror";
	
	public HisErrorHandler()
	{	
	}
	
	public void removeExistingMarkers()
	{
		try
		{
			iFile.deleteMarkers(ERROR_MARKER_ID, true, IResource.DEPTH_ZERO);
		}
		catch (CoreException e)
		{
			e.printStackTrace(MultiPageEditor.getRedStream());
		}
	}
	
	private String getParseExceptionInfo(SAXParseException spe) {
        String systemId = spe.getSystemId();
        if (systemId == null)
        {
            systemId = "null";
        }
        String info = "URI = " + systemId + "; Line = " + spe.getLineNumber() + "; Column = " + spe.getColumnNumber() + ":\n" + spe.getMessage();
        IMarker marker;
        try
        {
			marker = iFile.createMarker(ERROR_MARKER_ID);
			marker.setAttribute(IMarker.CHAR_START, iDocument.getLineOffset(spe.getLineNumber() - 1));
			marker.setAttribute(IMarker.CHAR_END, iDocument.getLineOffset(spe.getLineNumber() - 1) + spe.getColumnNumber());
			marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
			marker.setAttribute(IMarker.MESSAGE, info);
		}
        catch (CoreException e)
		{
			e.printStackTrace(MultiPageEditor.getRedStream());
		}
        catch (BadLocationException e)
        {
			e.printStackTrace(MultiPageEditor.getRedStream());
		}
        return info;
    }

	@Override
	public void error(SAXParseException arg0) throws SAXException
	{
		this.message = "ERROR " + getParseExceptionInfo(arg0);
		throw new SAXException(message);
	}

	@Override
	public void fatalError(SAXParseException arg0) throws SAXException
	{
		this.message = "FATAL " + getParseExceptionInfo(arg0);
		throw new SAXException(message);
	}

	@Override
	public void warning(SAXParseException arg0) throws SAXException
	{
		this.message = "WARNING " + getParseExceptionInfo(arg0);
		throw new SAXException(message);
	}

	public void setIFile(IFile iFile)
	{
		this.iFile = iFile;
		removeExistingMarkers();
	}

	public void setIDocument(IDocument iDocument)
	{
		this.iDocument = iDocument;
	}
	
}
