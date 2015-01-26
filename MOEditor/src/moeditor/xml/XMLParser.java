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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import moeditor.MultiPageEditor;
import moeditor.model.ModelNode;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IFileEditorInput;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

public class XMLParser
{
	private static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
	private static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
	private static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
	
	private static final String malSchemaFile = "/ServiceSchema.xsd";
	private static final String comSchemaFile = "/COMSchema.xsd";
	
	private static String comSchemaTemp;
	private static String malSchemaTemp;
	
	private static String[] schemaTempfiles;
	
	private Document document = null;
	private String text = null;
	private ModelNode root = null;
	
	private ErrorHandler errorHandler = null;
	
	private IFile iFile;
	private IDocument iDocument;
	private MultiPageEditor editor;
	
	private String last = null;
	
	public XMLParser(MultiPageEditor editor)
	{
		this.editor = editor;
		this.iDocument = editor.getXMLEditor().getDocumentProvider().getDocument(editor.getEditorInput());
		IFileEditorInput ifei = (IFileEditorInput) editor.getXMLEditor().getEditorInput();
		this.iFile = ifei.getFile();
		this.text = iDocument.get();
		setSchemas();
	}
	
	public XMLParser(MultiPageEditor editor, File file)
	{
		this.editor = editor;
		setSchemas();
		setText(file);
	}
	
	private void setSchemas() {
		malSchemaTemp = getSchemaStreamTemp(malSchemaFile);
		comSchemaTemp = getSchemaStreamTemp(comSchemaFile);
		schemaTempfiles = new String [] { malSchemaTemp, comSchemaTemp };
	}
	
	public void setErrorHandler(ErrorHandler errorHandler)
	{
		this.errorHandler = errorHandler;
	}

	private String getSchemaStreamTemp(String schemaFile) {
		File temp;
		try
		{
			temp = File.createTempFile("pattern", ".suffix");
		    temp.deleteOnExit();

		    BufferedWriter out = new BufferedWriter(new FileWriter(temp));
		    out.write(getSchema(schemaFile));
		    out.close();
		    return temp.getAbsolutePath();

		}
		catch (IOException e)
		{
			e.printStackTrace(new PrintStream(editor.getRedConsoleStream()));
		}
		return null;
	}
	
	private String getSchema(String schemaFile) {
		InputStream is = XMLParser.class.getResourceAsStream(schemaFile);
		
		StringWriter writer = new StringWriter();
				
		BufferedReader in = new BufferedReader(new InputStreamReader(is));
		String line = null;
		try
		{
			while( ( line = in.readLine() ) != null )
			{
				writer.write(line);
			}
		}
		catch (IOException e)
		{
			e.printStackTrace(new PrintStream(editor.getRedConsoleStream()));
		}
		return  writer.toString();
	}
	
	public Document parse()
	{
		if ( text == null ) return null;
		
		InputStream is = new ByteArrayInputStream(text.getBytes());
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		dbf.setValidating(true);
				
		dbf.setIgnoringComments(false);
        dbf.setIgnoringElementContentWhitespace(false);
        dbf.setCoalescing(false);
        dbf.setExpandEntityReferences(!false);

        DocumentBuilder db;
        Document doc = null;
        
        boolean success = true;
        
		try
		{
			dbf.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
			dbf.setAttribute(JAXP_SCHEMA_SOURCE, schemaTempfiles);
			db = dbf.newDocumentBuilder();
			db.setErrorHandler(setErrorHandler());
			doc = db.parse(is);
		}
		catch (IllegalArgumentException e)
		{
			ShowException("IllegalArgumentException: \n" + e.getMessage());
			success = false;
		}
		catch (ParserConfigurationException e)
		{
			ShowException("ParserConfigurationException: \n" + e.getMessage());
			success = false;
		}
		catch (SAXException e)
		{
			ShowException("SAXException: \n" + e.getMessage());
			success = false;
		}
		catch (IOException e)
		{
			ShowException("IOException: \n" + e.getMessage());
			success = false;
		}
		
		if ( errorHandler != null )
		{
			editor.setValid(success);
		}
		
		if ( !success ) return null;
		
		this.document = doc;
		return doc;
	}
	
	private ErrorHandler setErrorHandler()
	{
		if ( errorHandler != null ) return errorHandler;
		
        HisErrorHandler hisErrorHandler = new HisErrorHandler();
        hisErrorHandler.setIFile(iFile);
        hisErrorHandler.setIDocument(iDocument);

		return hisErrorHandler;
	}

	private void ShowException(String string)
	{
		editor.getRedConsoleStream().println(string);
		setLast(string);
		Display display = getDisplay();
		Shell shell = new Shell(display);
		MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
        messageBox.setText("Exception");
        String message = string;
        String[] pole = string.split("\n");
        if ( pole.length > 2 )
        {
        	if ( pole[2].startsWith("Duplicate unique value") )
        	{
        		try
        		{
        			String what = pole[2].split("\\[")[1].split("\\]")[0];
        			String where = pole[2].split("\"")[1];
        			try
        			{
        				Integer num = new Integer(what);
        				message = "The number " + num.toString() + " shall be unique in the " + where + ".";
        			}
        			catch ( NumberFormatException e )
        			{
        				message = "There can be only one " + what + " in the " + where + ".";
        			}
        		}
        		catch ( Exception e )
        		{
        			ShowException(e.getMessage() + "\nFailed to parse the following error message.\n" + string);
        			return;
        		}
        	}
        }
        messageBox.setMessage(message);
        messageBox.open();
	}

	public static Display getDisplay() {
		Display display = Display.getCurrent();
		//may be null if outside the UI thread
		if (display == null) display = Display.getDefault();
		return display;
	}
	
	public Document getDocument()
	{
		return document;
	}

	private void setText(File file)
	{
		String line;
		StringBuilder contents = new StringBuilder();
		try
		{
			BufferedReader input =  new BufferedReader(new FileReader(file));
			try
			{
				while ( ( line = input.readLine() ) != null )
				{
					contents.append(line);
					contents.append(System.getProperty("line.separator"));
				}
				text = contents.toString();
			}
			catch (IOException e)
			{
				e.printStackTrace(new PrintStream(editor.getRedConsoleStream()));
			}
			finally
			{
		        input.close();
		    }
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace(new PrintStream(editor.getRedConsoleStream()));
		}
		catch (IOException e)
		{
			e.printStackTrace(new PrintStream(editor.getRedConsoleStream()));
		}
		
	}

	public ModelNode CreateModelRootNode(Document doc, MultiPageEditor mpe) {
		ModelNode root = CreateModelNode(null, doc, mpe);
		this.root = root;
		return root;
	}
	
	public ModelNode getRoot()
	{
		return root;
	}

	private ModelNode CreateModelNode(ModelNode parent, Node node, MultiPageEditor mpe)
	{
		ModelNode modelNode = new ModelNode(node, parent, mpe);
		for ( Node child = node.getFirstChild(); child != null; child = child.getNextSibling() )
		{
			if ( child.getNodeType() == Node.ELEMENT_NODE )
			{
				ModelNode newModelNode = CreateModelNode(modelNode, child, mpe);
				modelNode.addChild(newModelNode);
			}
		}
		return modelNode;
	}

	public String getLast()
	{
		return last;
	}

	public void setLast(String last)
	{
		this.last = last;
	}

}
