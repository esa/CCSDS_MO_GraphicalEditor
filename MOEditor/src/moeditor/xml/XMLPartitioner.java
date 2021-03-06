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

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;


public class XMLPartitioner extends FastPartitioner
{

	public XMLPartitioner(IPartitionTokenScanner scanner, String[] legalContentTypes)
	{
		super(scanner, legalContentTypes);
	}

	public ITypedRegion[] computePartitioning(int offset, int length, boolean includeZeroLengthPartitions)
	{
		return super.computePartitioning(offset, length, includeZeroLengthPartitions);
	}

	public void connect(IDocument document, boolean delayInitialization)
	{
		super.connect(document, delayInitialization);
		printPartitions(document);
	}

	public void printPartitions(IDocument document)
	{
		StringBuffer buffer = new StringBuffer();

		ITypedRegion[] partitions = computePartitioning(0, document.getLength());
		for (int i = 0; i < partitions.length; i++)
		{
			try
			{
				buffer.append("Partition type: " + partitions[i].getType() + ", offset: " + partitions[i].getOffset()
						+ ", length: " + partitions[i].getLength());
				buffer.append("\n");
				buffer.append("Text:\n");
				buffer.append(document.get(partitions[i].getOffset(), partitions[i].getLength()));
				buffer.append("\n---------------------------\n\n\n");
			}
			catch (BadLocationException e)
			{
				e.printStackTrace();
			}
		}
	}
}