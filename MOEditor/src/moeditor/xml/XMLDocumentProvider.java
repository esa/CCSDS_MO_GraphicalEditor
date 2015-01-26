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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.ui.editors.text.FileDocumentProvider;

public class XMLDocumentProvider extends FileDocumentProvider
{
	protected IDocument createDocument(Object element) throws CoreException
	{
	    IDocument document = super.createDocument(element);
	    if (document != null)
	    {
	        IDocumentPartitioner partitioner = new XMLPartitioner(new XMLPartitionScanner(), new String[]
	        {
	                XMLPartitionScanner.XML_START_TAG,
	                XMLPartitionScanner.XML_PI,
	                XMLPartitionScanner.XML_DOCTYPE,
	                XMLPartitionScanner.XML_END_TAG,
	                XMLPartitionScanner.XML_TEXT,
	                XMLPartitionScanner.XML_CDATA,
	                XMLPartitionScanner.XML_COMMENT
	        });
	        partitioner.connect(document);
	        document.setDocumentPartitioner(partitioner);
	    }
	    return document;
	}
}
