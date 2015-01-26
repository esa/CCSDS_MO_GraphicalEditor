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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import moeditor.draw2d.Button;
import moeditor.draw2d.ButtonClickHandler;
import moeditor.draw2d.TheFigure;
import moeditor.xml.XMLParser;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.ui.IFileEditorInput;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import esa.mo.tools.stubgen.Generator;
import esa.mo.tools.stubgen.xsd.SpecificationType;

public class ExternalPage extends TheFigure implements ButtonClickHandler
{
	private String ERROR_MARKER_ID = "MOEditor.datatypeerror";
	
	private MultiPageEditor editor;
	
	public ExternalPage(TheFigure parentFigure, MultiPageEditor editor)
	{
		super(parentFigure);
		this.editor = editor;
		
		ToolbarLayout layout  = new ToolbarLayout();
		setLayoutManager(layout);
		
		setFocusColor(getNonFocusColor());
		
		fillTable();
	}
	
	private void fillTable()
	{

		Font font = new Font(null, "Arial", 12, SWT.BOLD);
		Label label;
		
		label = new Label("External References");
		label.setFont(font);
		add(label);
		add(new ExternalResourcesFigure(this, editor));
		
		Figure figure = new Figure();
		ToolbarLayout layout = new ToolbarLayout(true);
		figure.setLayoutManager(layout);
		label = new Label("Validity Check");
		label.setFont(font);
		figure.add(label);
		Button button = new Button(this, "go.png", "go");
		button.setButtonClickHandler(this);
		figure.add(button);
		add(figure);

		label = new Label("MS Word Document Generation");
		label.setFont(font);
		add(label);
		add(new MSWordFigure(this, editor));
		
		label = new Label("Java Code Generation");
		label.setFont(font);
		add(label);
		add(new JavaFigure(this, editor));
	}

	public ArrayList<Document> getCheckedResources()
	{
		ArrayList<Document> result = new ArrayList<Document>();
		for ( ExternalResources resource : editor.checkBox )
		{
			if ( resource.isChecked() && resource.getDoc() != null )
			{
				result.add(resource.getDoc());
			}
		}
		for ( InternalResources ir : editor.malcom )
		{
			if ( ir.getDocument() != null )
			{
				result.add(ir.getDocument());
			}
		}
		return result;
	}
	
	public ArrayList<File> getCheckedResourceFiles()
	{
		ArrayList<File> result = new ArrayList<File>();
		for ( ExternalResources resource : editor.checkBox )
		{
			if ( resource.isChecked() && resource.getDoc() != null )
			{
				result.add(resource.getFile());
			}
		}
		return result;
	}

	protected void validityCheck()
	{
		MLog log = new MLog(editor.getConsoleStream(), editor.getConsoleStream(), editor.getConsoleStream(), editor.getConsoleStream(), editor.getConsoleStream());
		log.giveMemory(true);

		IFile fajl = ((IFileEditorInput) editor.getEditorInput()).getFile();
		
		XMLParser parser = new XMLParser(editor);
		Document doc = parser.parse();
		
		try
		{
			fajl.deleteMarkers(ERROR_MARKER_ID, true, IResource.DEPTH_ZERO);
		}
		catch ( CoreException e )
		{
			editor.ShowException(e.getMessage());
			e.printStackTrace(MultiPageEditor.getRedStream());
		}
		
		log.setHeading("Validity Check");
		
		if ( doc == null )
		{
			log.error(parser.getLast());
			log.info("XML is broken => check stopped.");
		}
		else
		{
			NodeList type = doc.getElementsByTagNameNS("*", "type");
			for ( int i = 0; i < type.getLength(); i++ )
			{
				Element typ = (Element) type.item(i);
				boolean found = false;
				if ( MultiPageEditor.isLocalType(doc, typ) != null )
				{
					log.debug("Type " + typ.getAttribute("area") + "::" + typ.getAttribute("service") + "::" + typ.getAttribute("name") + " is local.");
					found = true;
				}
				else
				{
					for ( InternalResources ir : editor.malcom )
					{
						if ( MultiPageEditor.isLocalType(ir.getDocument(), typ) != null )
						{
							String text = "Type " + typ.getAttribute("area") + "::" + typ.getAttribute("service") + "::" + typ.getAttribute("name") + " found in " + ir.getFileName();
							log.info(text);
							found = true;
							break;
						}
					}
					for ( ExternalResources resource : editor.checkBox )
					{
						if ( resource.isChecked() && resource.getDoc() != null )
						{
							if ( MultiPageEditor.isLocalType(resource.getDoc(), typ) != null )
							{
								String text = "Type " + typ.getAttribute("area") + "::" + typ.getAttribute("service") + "::" + typ.getAttribute("name") + " found in " + resource.getFile().getAbsolutePath();
								log.info(text);
								found = true;
								break;
							}
						}
					}
				}
				if ( !found )
				{
					String text = "Type " + typ.getAttribute("area") + "::" + typ.getAttribute("service") + "::" + typ.getAttribute("name") + " not found!";
					editor.setValid(false);
					if ( log.errorB(text) )
					{
				        IMarker marker;
				        try
				        {
							marker = fajl.createMarker(ERROR_MARKER_ID);
							//marker.setAttribute(IMarker.CHAR_START, dokument.getLineOffset(spe.getLineNumber() - 1));
							//marker.setAttribute(IMarker.CHAR_END, dokument.getLineOffset(spe.getLineNumber() - 1) + spe.getColumnNumber());
							marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
							marker.setAttribute(IMarker.MESSAGE, text);
						}
				        catch (CoreException e)
				        {
							editor.ShowException(e.getMessage());
							e.printStackTrace(MultiPageEditor.getRedStream());
						}
					}
				}
			}
			
		}
		log.printStat();
	}

	void generateGenerator(final String outputFolder, final Generator generator)
	{
		try
		{
			validityCheck();
			editor.createHulmFigure();
			
			if ( !editor.isValid() || !editor.isHumlValid() )
			{
				throw new XeXepwn();
			}
			
			TreeMap<String, String> extraprop = new TreeMap<String, String>();
			extraprop.put("docx.includeMessageFieldNames", "true");
			generator.init(outputFolder, true, true, extraprop);
			generator.setJaxbBindings(new TreeMap<String, String>());
			for ( File file : getCheckedResourceFiles() )
			{
				generator.preProcess(loadSpecification(file));
			}
			for ( InternalResources ir : editor.malcom )
			{
				generator.preProcess(loadSpecification(ir.getFile()));
			}
			File temp;
			temp = File.createTempFile("pattern", ".suffix");
		    temp.deleteOnExit();

		    BufferedWriter out = new BufferedWriter(new FileWriter(temp));
		    out.write(editor.getXMLEditor().getDocumentProvider().getDocument(editor.getEditorInput()).get());
		    out.close();
			generator.preProcess(loadSpecification(temp));
			/*
			 * 
			 */
			final JAXBContext jc = JAXBContext.newInstance("esa.mo.tools.stubgen.xsd");
			final Unmarshaller unmarshaller = jc.createUnmarshaller();
			final JAXBElement rootElement = (JAXBElement) unmarshaller.unmarshal(temp);
			/*
			 * 
			 */
			generator.compile(outputFolder, loadSpecification(temp), rootElement);
			generator.close(outputFolder);
		}
		catch (IOException e)
		{
			editor.ShowException(e.getMessage());
			e.printStackTrace(MultiPageEditor.getRedStream());
		}
		catch (JAXBException e)
		{
			editor.ShowException(e.getMessage());
			e.printStackTrace(MultiPageEditor.getRedStream());
		} catch (XeXepwn e) {
			editor.ShowException("The file seems to be invalid. Generator execution stopped.");
		}
		catch (Exception e)
		{
			editor.ShowException(e.getMessage());
			e.printStackTrace(MultiPageEditor.getRedStream());
		}
	}

	@SuppressWarnings("rawtypes")
	private static SpecificationType loadSpecification(final File is) throws IOException, JAXBException
	{
		final JAXBContext jc = JAXBContext.newInstance("esa.mo.tools.stubgen.xsd");
	    final Unmarshaller unmarshaller = jc.createUnmarshaller();
	    final JAXBElement rootElement = (JAXBElement) unmarshaller.unmarshal(is);
	    return (SpecificationType) rootElement.getValue();
	}

	@Override
	public void ButtonClicked(Button button)
	{
		validityCheck();
	}
}
