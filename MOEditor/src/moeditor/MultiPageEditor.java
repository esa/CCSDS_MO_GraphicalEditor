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

package moeditor;

import java.io.PrintStream;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import moeditor.draw2d.TheFigure;
import moeditor.figure.CompositeFigure;
import moeditor.figure.DataTypesFigure;
import moeditor.figure.EnumerationFigure;
import moeditor.figure.ErrorsFigure;
import moeditor.figure.RootFigure;
import moeditor.figure.ServiceRootFigure;
import moeditor.model.ModelNode;
import moeditor.xml.XMLEditor;
import moeditor.xml.XMLParser;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.*;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.ide.IDE;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MultiPageEditor extends MultiPageEditorPart implements IResourceChangeListener{

	private XMLEditor editor;
	
	private FigureCanvas rootCanvas = null;
	private TheFigure rootFigure = null;
	private int rootPageIndex = -1;
	private ModelNode root = null;
	
	private FigureCanvas externalCanvas = null;
	private TheFigure externalFigure = null;
	private int externalPageIndex = -1;
	
	private FigureCanvas humlCanvas = null;
	private TheFigure humlFigure = null;
	private int humlPageIndex = -1;
	
	public ArrayList<InternalResources> malcom = new ArrayList<InternalResources>();
	public ArrayList<ExternalResources> checkBox = new ArrayList<ExternalResources>();
	public boolean ws = true;
	public String projectName = null;
	public String outputFolder = null;
	public String docxFolder = null;
	private MessageConsole console;
	
	private ExternalPage externalPage = null;

	private HumlDiagram humlDiagram = null;
	
	public static int widght = 65535;
	
	private boolean valid = false;
	private boolean humlValid = false;
	
	public MultiPageEditor()
	{
		super();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
		malcom.add(new InternalResources("/ServiceDefMAL.xml", this));
		malcom.add(new InternalResources("/ServiceDefCOM.xml", this));
		console = findConsole("CCSDS MO Editor Console");	
	}
	
	public XMLEditor getXMLEditor()
	{
		return editor;
	}
	
	public MessageConsoleStream getConsoleStream()
	{
		openConsole();
		MessageConsoleStream out = console.newMessageStream();
		return out;
	}
	
	public MessageConsoleStream getRedConsoleStream()
	{
		openConsole();
		MessageConsoleStream out = console.newMessageStream();
		out.setColor(ColorConstants.red);
		return out;
	}
	
	public static PrintStream getRedStream()
	{
		MessageConsoleStream out = findConsole("CCSDS MO Editor Console").newMessageStream();
		out.setColor(ColorConstants.red);
		return new PrintStream(out, true);
	}
	
	public void openConsole()
	{
		IWorkbenchPage page = getSite().getWorkbenchWindow().getActivePage();
		String id = IConsoleConstants.ID_CONSOLE_VIEW;
		IConsoleView view;
		try
		{
			view = (IConsoleView) page.showView(id);
			view.display(console);
		}
		catch (PartInitException e)
		{
			e.printStackTrace();
		}
	}
	
	public static MessageConsole findConsole(String name)
	{
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = plugin.getConsoleManager();
		IConsole[] existing = conMan.getConsoles();
		for ( int i = 0; i < existing.length; i++ )
		{
			if ( name.equals(existing[i].getName()) )
			{
				return (MessageConsole) existing[i];
			}
		}
		//no console found, so create a new one
		MessageConsole myConsole = new MessageConsole(name, null);
		conMan.addConsoles(new IConsole[]{myConsole});
		return myConsole;
	}

	void createPageXML()
	{
		try {
			editor = new XMLEditor(this);
			int index = addPage(editor, getEditorInput());
			setPageText(index, editor.getTitle());
			this.setPartName(editor.getTitle());
		} catch (PartInitException e) {
			ErrorDialog.openError(
				getSite().getShell(),
				"Error creating nested text editor",
				null,
				e.getStatus());
		}
		
		for ( InternalResources ir : malcom )
		{
			ir.prepareDoc();
		}
	}
	
	public void setRoot(ModelNode root)
	{
		this.root = root;
		createRootFigure(root);
	}
	
	void createGraphicalPage()
	{
		Composite composite = new Composite(getContainer(), SWT.NONE);
		FillLayout layout = new FillLayout();
		composite.setLayout(layout);
		
		rootCanvas = new FigureCanvas(composite, SWT.DOUBLE_BUFFERED | SWT.H_SCROLL | SWT.V_SCROLL);
		//Viewport vp = rootCanvas.getViewport();
		//vp.setContentsTracksHeight(false);
		//vp.setContentsTracksWidth(false);
		//rootCanvas.setViewport(vp);
		rootCanvas.setBackground(ColorConstants.white);
		
		rootFigure = new TheFigure(rootCanvas);
		rootFigure.setFocusColor(ColorConstants.white);

		XYLayout figlayout = new XYLayout();
		rootFigure.setLayoutManager(figlayout);
		
		rootCanvas.setContents(rootFigure);
		//System.out.println(rootCanvas.getViewport().getContentsTracksHeight() + " & " + rootCanvas.getViewport().getContentsTracksWidth());
		
		rootPageIndex = addPage(composite);
		setPageText(rootPageIndex, "Root Page");
		createRootFigure(root);
	}
	
	public void createRootFigure(ModelNode root) {
		if ( root == null ) return;
		if ( rootFigure == null ) return;
		
		RootFigure figure = new RootFigure(rootFigure, root);
		rootFigure.closeComboBox();
		rootFigure.removeAll();
		
		rootFigure.add(figure);
		
		rootFigure.setConstraint(figure, new Rectangle(5, 5, widght, widght));
		setPageText(rootPageIndex, "Root Page");
		setActivePage(rootPageIndex);
	}
	
	public void createFigure(ModelNode node, ArrayList<ModelNode> retpath)
	{
		if ( node.equals(node.getRoot()) ) createRootFigure(node);
		else if ( node.getLocalName().equals("dataTypes") ) createDataTypesFigure(node, retpath);
		else if ( node.getLocalName().equals("enumeration") ) createEnumerationFigure(node, retpath);
		else if ( node.getLocalName().equals("composite") ) createCompositeFigure(node, retpath);
		else if ( node.getLocalName().equals("errors") ) createErrorsFigure(node, retpath);
		else if ( node.getLocalName().equals("service") ) createServiceFigure(node, retpath);
	}
	
	private void createCompositeFigure(ModelNode composite, ArrayList<ModelNode> retpath)
	{
		CompositeFigure figure = new CompositeFigure(composite, rootFigure, retpath);
		
		rootFigure.closeComboBox();
		rootFigure.removeAll();
		rootFigure.add(figure);
		rootFigure.setConstraint(figure, new Rectangle(5, 5, widght, widght));
		setPageText(rootPageIndex, "Composite Type: " + composite.getAttribute("name"));
		setActivePage(rootPageIndex);
	}

	private void createEnumerationFigure(ModelNode enumeration, ArrayList<ModelNode> retpath)
	{
		EnumerationFigure figure = new EnumerationFigure(enumeration, rootFigure, retpath);
		
		rootFigure.closeComboBox();
		rootFigure.removeAll();
		rootFigure.add(figure);
		rootFigure.setConstraint(figure, new Rectangle(5, 5, widght, widght));
		setPageText(rootPageIndex, "Enumeration Type: " + enumeration.getAttribute("name"));
		setActivePage(rootPageIndex);
	}

	private void createDataTypesFigure(ModelNode dataTypes, ArrayList<ModelNode> retpath)
	{
		DataTypesFigure figure = new DataTypesFigure(rootFigure, dataTypes, retpath);
		
		rootFigure.closeComboBox();
		rootFigure.removeAll();
		rootFigure.add(figure);
		rootFigure.setConstraint(figure, new Rectangle(5, 5, widght, widght));
		
		setPageText(rootPageIndex, "Data Types");
		setActivePage(rootPageIndex);
	}
	
	private void createErrorsFigure(ModelNode errors, ArrayList<ModelNode> retpath)
	{
		ErrorsFigure figure = new ErrorsFigure(errors, rootFigure, retpath);
		
		rootFigure.closeComboBox();
		rootFigure.removeAll();
		rootFigure.add(figure);
		rootFigure.setConstraint(figure, new Rectangle(5, 5, widght, widght));
		
		setPageText(rootPageIndex, "Errors");
		setActivePage(rootPageIndex);
	}
	
	public void createServiceFigure(ModelNode service, ArrayList<ModelNode> retpath)
	{
		ServiceRootFigure figure = new ServiceRootFigure(rootFigure, service, retpath);
		
		rootFigure.closeComboBox();
		rootFigure.removeAll();
		rootFigure.add(figure);
		rootFigure.setConstraint(figure, new Rectangle(5, 5, widght, widght));
		
		setPageText(rootPageIndex, "Service " + service.getAttribute("name"));
		setActivePage(rootPageIndex);
	}
	

	void createPageOptions()
	{
		Composite composite = new Composite(getContainer(), SWT.NONE);
		FillLayout layout = new FillLayout();
		composite.setLayout(layout);
		
		externalCanvas = new FigureCanvas(composite, SWT.DOUBLE_BUFFERED | SWT.H_SCROLL | SWT.V_SCROLL);
		externalCanvas.setBackground(ColorConstants.white);
		
		externalFigure = new TheFigure(externalCanvas);
		externalFigure.setFocusColor(ColorConstants.white);

		XYLayout figlayout = new XYLayout();
		externalFigure.setLayoutManager(figlayout);
		
		externalCanvas.setContents(externalFigure);
		
		externalPageIndex = addPage(composite);
		setPageText(externalPageIndex, "Auto Generation");
		createAutoGenerationFigure();
	}
	
	void createPageHuml()
	{
		Composite composite = new Composite(getContainer(), SWT.NONE);
		FillLayout layout = new FillLayout();
		composite.setLayout(layout);
		
		humlCanvas = new FigureCanvas(composite, SWT.DOUBLE_BUFFERED | SWT.H_SCROLL | SWT.V_SCROLL);
		humlCanvas.setBackground(ColorConstants.white);
		
		humlFigure = new TheFigure(humlCanvas);
		humlFigure.setFocusColor(ColorConstants.white);

		XYLayout figlayout = new XYLayout();
		humlFigure.setLayoutManager(figlayout);
		
		humlCanvas.setContents(humlFigure);
		
		humlPageIndex = addPage(composite);
		setPageText(humlPageIndex, "COM UML Diagram");
		createAutoGenerationFigure();

	}
	
	public void createHulmFigure()
	{
		HumlDiagram newHuml = new HumlDiagram(humlFigure, this, root);
		boolean refresh = true;
		if ( humlDiagram != null )
		{
			
			if ( humlDiagram.getRoot().equals(root) )
			{
				refresh = false;
			}
			else if ( newHuml.getHuml().equals(humlDiagram.getHuml()) )
			{
				refresh = false;
			}
		}
		
		if ( refresh )
		{
			humlFigure.closeComboBox();
			humlFigure.removeAll();
			humlDiagram  = newHuml;
			setHumlValid(humlDiagram.fillTable());
		}
	}
	
	private void createAutoGenerationFigure()
	{
		externalPage = new ExternalPage(externalFigure, this);
		
		externalFigure.closeComboBox();
		externalFigure.removeAll();
		externalFigure.add(externalPage);
		externalFigure.setConstraint(externalPage, new Rectangle(5, 5, widght, widght));
	}

	
	public static Node isLocalType(Document doc, Element typ)
	{
		Node retval = null;
		if ( typ.getParentNode().getLocalName().equals("errorRef") )
		{
			
		}
		NodeList area = doc.getElementsByTagNameNS("*", "area");
		for ( int a = 0; a < area.getLength(); a++ )
		{
			Element ae = (Element) area.item(a);
			if ( ae.getAttribute("name").equals(typ.getAttribute("area")) )
			{
				Element dt = null;
				NodeList error = null;
				if ( typ.getAttribute("service").isEmpty() )
				{
					NodeList dataType = ae.getChildNodes();
					for ( int d = 0; d < dataType.getLength(); d++ )
					{
						Node candidate = dataType.item(d);
						if ( candidate.getNodeType() == Node.ELEMENT_NODE )
						{
							if ( !typ.getParentNode().getLocalName().equals("errorRef") )
							{
								if ( candidate.getLocalName().equals("dataTypes") )
								{
									dt = (Element) candidate;
									break;
								}
							}
							else
							{
								if ( candidate.getLocalName().equals("errors") )
								{
									error = ((Element) candidate).getChildNodes();
									break;
								}
							}
						}
					}
				}
				else
				{
					NodeList service = ae.getElementsByTagNameNS("*", "service");
					for ( int s = 0; s < service.getLength(); s++ )
					{
						Element se = (Element) service.item(s);
						if ( se.getAttribute("name").equals(typ.getAttribute("service")) )
						{
							if ( typ.getParentNode().getLocalName().equals("errorRef") )
							{
								error = se.getElementsByTagNameNS("*", "error");
								break;
							}
							else
							{
								NodeList dataType = se.getChildNodes();
								for ( int d = 0; d < dataType.getLength(); d++ )
								{
									Node candidate = dataType.item(d);
									if ( candidate.getNodeType() == Node.ELEMENT_NODE )
									{
										if ( candidate.getLocalName().equals("dataTypes") )
										{
											dt = (Element) candidate;
											break;
										}
									}
								}
							}
						}
					}
				}
				if ( dt != null )
				{
					NodeList types = dt.getChildNodes();
					for ( int t = 0; t < types.getLength(); t++ )
					{
						Node candidate = types.item(t);
						if ( candidate.getNodeType() == Node.ELEMENT_NODE )
						{
							if ( ((Element) candidate).getAttribute("name").equals(typ.getAttribute("name")) ) return candidate;
						}
					}
				}
				if ( error != null )
				{
					for ( int e = 0; e < error.getLength(); e++ )
					{
						Node candidate = error.item(e);
						if ( candidate.getNodeType() == Node.ELEMENT_NODE )
						{
							if ( ((Element) candidate).getAttribute("name").equals(typ.getAttribute("name")) ) return candidate;
						}
					}
				}
			}
		}
		return retval;
	}

	public void ShowException(String string)
	{
		MessageConsoleStream out = getConsoleStream();
		out.setColor(ColorConstants.red);
		out.println(string);
		Display display = getDisplay();
		Shell shell = new Shell(display);
		MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK); //SWT.ABORT | SWT.RETRY | SWT.IGNORE);
        messageBox.setText("Exception");
        String message = string;
        messageBox.setMessage(message);
        messageBox.open();
	}
	
	public static Display getDisplay()
	{
		Display display = Display.getCurrent();
		//may be null if outside the UI thread
		if (display == null) display = Display.getDefault();
		return display;
	}
	

	@Override
	protected void createPages()
	{
		createPageXML();
		createGraphicalPage();
		createPageOptions();
		createPageHuml();
	}
	/**
	 * The <code>MultiPageEditorPart</code> implementation of this 
	 * <code>IWorkbenchPart</code> method disposes all nested editors.
	 * Subclasses may extend.
	 */
	public void dispose() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		super.dispose();
	}
	/**
	 * Saves the multi-page editor's document.
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
		getEditor(0).doSave(monitor);
	}
	/**
	 * Saves the multi-page editor's document as another file.
	 * Also updates the text for page 0's tab, and updates this multi-page editor's input
	 * to correspond to the nested editor's.
	 */
	@Override
	public void doSaveAs() {
		IEditorPart editor = getEditor(0);
		editor.doSaveAs();
		setPageText(0, editor.getTitle());
		setInput(editor.getEditorInput());
	}
	/* (non-Javadoc)
	 * Method declared on IEditorPart
	 */
	public void gotoMarker(IMarker marker) {
		setActivePage(0);
		IDE.gotoMarker(getEditor(0), marker);
	}
	/**
	 * The <code>MultiPageEditorExample</code> implementation of this method
	 * checks that the input is an instance of <code>IFileEditorInput</code>.
	 */
	public void init(IEditorSite site, IEditorInput editorInput) throws PartInitException {
		if ( !(editorInput instanceof IFileEditorInput) ) throw new PartInitException("Invalid Input: Must be IFileEditorInput");
		super.init(site, editorInput);
	}
	/* (non-Javadoc)
	 * Method declared on IEditorPart.
	 */
	@Override
	public boolean isSaveAsAllowed() {
		return true;
	}
	/**
	 * Calculates the contents of page 2 when the it is activated.
	 */
	protected void pageChange(int newPageIndex) {
		super.pageChange(newPageIndex);
		if ( newPageIndex == externalPageIndex )
		{
			createAutoGenerationFigure();
		}
		else if ( newPageIndex == humlPageIndex )
		{
			createHulmFigure();
		}
	}
	/**
	 * Closes all project files on project close.
	 */
	@Override
	public void resourceChanged(final IResourceChangeEvent event)
	{
		if ( event.getType() == IResourceChangeEvent.PRE_CLOSE )
		{
			//Display.getDefault().asyncExec(new Runnable(){
			getDisplay().asyncExec(new Runnable()
			{
				public void run()
				{
					IWorkbenchPage[] pages = getSite().getWorkbenchWindow().getPages();
					for ( int i = 0; i < pages.length; i++ )
					{
						if ( ((FileEditorInput)editor.getEditorInput()).getFile().getProject().equals(event.getResource()) )
						{
							IEditorPart editorPart = pages[i].findEditor(editor.getEditorInput());
							pages[i].closeEditor(editorPart,true);
						}
					}
				}            
			});
		}
	}

	public void printDocument(Document doc)
	{
		Source source = new DOMSource(doc);
		StringWriter out = new StringWriter();
		Result result = new StreamResult(out);

		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer;
		try
		{
			transformer = tFactory.newTransformer();
			transformer.transform(source, result);
		}
		catch (TransformerConfigurationException e)
		{
			ShowException("XML Model Error!\n" + e.getMessage());
		}
		catch (TransformerException e)
		{
			ShowException("XML Model Error!\n" + e.getMessage());
		}

		String xml = out.toString();
		String original = editor.getDocumentProvider().getDocument(editor.getEditorInput()).get();
		if ( !original.equals(xml) )
		{
			editor.getDocumentProvider().getDocument(editor.getEditorInput()).set(xml);
		}
		
		//IDocument dokument = editor.getDocumentProvider().getDocument(editor.getEditorInput());
		//IFile fajl = ((IFileEditorInput) editor.getEditorInput()).getFile();
		//XMLParser parser = new XMLParser(dokument, fajl);
		XMLParser parser = new XMLParser(this);
		doc = parser.parse();
		if ( doc != null )
		{
			ModelNode root = parser.CreateModelRootNode(doc, this);
			this.root = root;
		}
	}

	public ArrayList<Document> getCheckedResources()
	{
		return externalPage.getCheckedResources();
	}

	public boolean isValid()
	{
		return valid;
	}

	public void setValid(boolean valid)
	{
		this.valid = valid;
	}

	public boolean isHumlValid()
	{
		return humlValid;
	}

	public void setHumlValid(boolean humlValid)
	{
		this.humlValid = humlValid;
	}

}
