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

import java.util.ResourceBundle;

import moeditor.Activator;
import moeditor.MultiPageEditor;
import moeditor.model.ModelNode;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.TextOperationAction;
import org.w3c.dom.Document;

public class XMLEditor extends TextEditor
{
	private ColorManager colorManager;
	private IEditorInput input;
	private MultiPageEditor mpe;

	public XMLEditor(MultiPageEditor mpe)
	{
		super();
		this.mpe = mpe;
		colorManager = new ColorManager();
		setSourceViewerConfiguration(new XMLConfiguration(colorManager));
		setDocumentProvider(new XMLDocumentProvider());
	}
	
	public MultiPageEditor getMPE()
	{
		return mpe;
	}
	
	public void dispose()
	{
		colorManager.dispose();
		super.dispose();
	}
	
	protected void doSetInput(IEditorInput newInput) throws CoreException
	{
		super.doSetInput(newInput);
		this.input = newInput;
		validateAndMark();
	}

	protected void editorSaved()
	{
		super.editorSaved();
		validateAndMark();
	}

	protected void validateAndMark()
	{
		ModelNode root = null;
		XMLParser parser = new XMLParser(mpe);
		Document doc = parser.parse();
		if ( doc != null ) root = parser.CreateModelRootNode(doc, getMPE());
		getMPE().setRoot(root);
	}

	protected IDocument getInputDocument()
	{
		IDocument document = getDocumentProvider().getDocument(input);
		return document;
	}

	protected IFile getInputFile()
	{
		IFileEditorInput ife = (IFileEditorInput) input;
		IFile file = ife.getFile();
		return file;
	}
	
	
	public IEditorInput getInput()
	{
		return input;
	}
	

	protected void createActions()
	{
		super.createActions();
		ResourceBundle bundle = Activator.getDefault().getResourceBundle();
		setAction("ContentFormatProposal", new TextOperationAction(bundle, "ContentFormatProposal.", this, ISourceViewer.FORMAT));
		setAction("ContentAssistProposal", new TextOperationAction(bundle, "ContentAssistProposal.", this, ISourceViewer.CONTENTASSIST_PROPOSALS));
		setAction("ContentAssistTip", new TextOperationAction(bundle, "ContentAssistTip.", this, ISourceViewer.CONTENTASSIST_CONTEXT_INFORMATION));
	}
		
}
