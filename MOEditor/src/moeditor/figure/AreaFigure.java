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

package moeditor.figure;

import java.util.ArrayList;

import moeditor.MultiPageEditor;
import moeditor.draw2d.Arrow;
import moeditor.draw2d.Button;
import moeditor.draw2d.ButtonClickHandler;
import moeditor.draw2d.TheFigure;
import moeditor.model.ModelNode;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class AreaFigure extends TheFigure implements ButtonClickHandler
{
	private ModelNode area;
	private ModelNode dataTypes = null;
	private ModelNode errors = null;
	private ArrayList<ModelNode> retpath = new ArrayList<ModelNode>();
	private boolean erropen = false;
	
	public AreaFigure(TheFigure parentFigure, ModelNode area)
	{
		super(parentFigure);
		this.area = area;
		retpath.add(area.getRoot());
		
		ToolbarLayout layout = new ToolbarLayout();
		//layout.setStretchMinorAxis(true);
		setLayoutManager(layout);
		setFocusColor(getNonFocusColor());
		
		setFigure();
	}
	
	private void setFigure()
	{
		Figure figure = new Figure();
		ToolbarLayout layout = new ToolbarLayout(true);
		figure.setLayoutManager(layout);
		
		figure.add(new AreaBaseFigure(this, area));
		add(figure);
		
		addDocumentationHeading();
		addServiceHeading();
		addDataTypesHeading();
		addErrorsHeading();
	}
	
	private void addErrorsHeading()
	{
		errors = area.getChildByLocalName("errors");
		Font font = new Font(null, "Arial", 12, SWT.BOLD);
		
		Figure figure = new Figure();
		ToolbarLayout layout = new ToolbarLayout(true);
		figure.setLayoutManager(layout);
		
		Label label = new Label("Errors");
		label.setFont(font);
		
		Button errorsButton;
		if ( errors == null )
		{
			errorsButton = new Button(this, "new.png", "newErrors", area);
			errorsButton.setButtonClickHandler(this);
		}
		else
		{
			errorsButton = new Button(this, "red_cross.png", "delete", errors);
			errorsButton.setButtonClickHandler(this);
		}
		
		figure.add(label);
		figure.add(errorsButton);
		add(figure);
		
		addErrors();
	}

	private void addErrors()
	{
		if ( errors == null ) return;
		
		Figure figure = new Figure();
		ToolbarLayout layout = new ToolbarLayout(true);
		figure.setLayoutManager(layout);
		
		Label label = new Label(new Image(getDisplay(), MultiPageEditor.class.getResourceAsStream("/rightarrow.png")));
		
		Button dataTypesInfo = new Button(this, "info_small.png", "go", errors);
		dataTypesInfo.setButtonClickHandler(this);
		
		Arrow arrow = new Arrow(this);
		arrow.setFigure(new ErrorsFigure(errors, arrow, retpath, false));
		
		figure.add(label);
		figure.add(dataTypesInfo);
		figure.add(arrow);
		add(figure);
		
		if ( erropen )
		{
			ButtonClicked(dataTypesInfo);
			erropen = false;
		}
	}

	private void addDataTypesHeading()
	{
		dataTypes = area.getChildByLocalName("dataTypes");
		Font font = new Font(null, "Arial", 12, SWT.BOLD);
		
		Figure figure = new Figure();
		ToolbarLayout layout = new ToolbarLayout(true);
		figure.setLayoutManager(layout);
		
		Label label = new Label("Data Types");
		label.setFont(font);
		
		Button dataTypesButton;
		if ( dataTypes == null )
		{
			dataTypesButton = new Button(this, "new.png", "newDataTypes", area);
			dataTypesButton.setButtonClickHandler(this);
		}
		else
		{
			dataTypesButton = new Button(this, "red_cross.png", "delete", dataTypes);
			dataTypesButton.setButtonClickHandler(this);
		}
		
		figure.add(label);
		figure.add(dataTypesButton);
		add(figure);
		
		addDataTypes();
	}

	private void addDataTypes()
	{
		 
		if ( dataTypes == null ) return;
		
		Figure figure = new Figure();
		ToolbarLayout layout = new ToolbarLayout(true);
		figure.setLayoutManager(layout);
		
		Label label = new Label(new Image(getDisplay(), MultiPageEditor.class.getResourceAsStream("/rightarrow.png")));
		
		Button dataTypesInfo = new Button(this, "info_small.png", "go", dataTypes);
		dataTypesInfo.setButtonClickHandler(this);
		
		Arrow arrow = new Arrow(this);
		arrow.setFigure(new DataTypesFigure(arrow, dataTypes, retpath, false));
		
		figure.add(label);
		figure.add(dataTypesInfo);
		figure.add(arrow);
		add(figure);
	}

	private void addServiceHeading()
	{
		Font font = new Font(null, "Arial", 12, SWT.BOLD);
		
		Figure figure = new Figure();
		ToolbarLayout layout = new ToolbarLayout(true);
		figure.setLayoutManager(layout);
		
		Label label = new Label("Service");
		label.setFont(font);
		
		Button newServiceButton = new Button(this, "new.png", "newService", area);
		newServiceButton.setButtonClickHandler(this);
		
		figure.add(label);
		figure.add(newServiceButton);
		add(figure);
		
		addService();
	}

	private void addService()
	{
		Figure figure = new Figure();
		ToolbarLayout layout = new ToolbarLayout(true);
		figure.setLayoutManager(layout);
		
		Label label = new Label(new Image(getDisplay(), MultiPageEditor.class.getResourceAsStream("/rightarrow.png")));
		figure.add(label);
		figure.add(new ServiceBaseFigure(this, area));
		add(figure);
	}

	private void addDocumentationHeading()
	{
		Font font = new Font(null, "Arial", 12, SWT.BOLD);
		
		Figure figure = new Figure();
		ToolbarLayout layout = new ToolbarLayout(true);
		figure.setLayoutManager(layout);
		
		Label label = new Label("Documentation");
		label.setFont(font);
		
		Button newDocumentationButton = new Button(this, "new.png", "newDocumentation", area);
		newDocumentationButton.setButtonClickHandler(this);
		
		figure.add(label);
		figure.add(newDocumentationButton);
		add(figure);
		
		addDocumentation();
	}
	
	public void addDocumentation()
	{
		Figure figure = new Figure();
		ToolbarLayout layout = new ToolbarLayout(true);
		figure.setLayoutManager(layout);
		
		Label label = new Label(new Image(getDisplay(), MultiPageEditor.class.getResourceAsStream("/rightarrow.png")));
		figure.add(label);
		figure.add(new DocumentationBaseFigure(this, area));
		add(figure);
	}

	@Override
	public void ButtonClicked(Button button)
	{
		if ( button.getId().equals("delete") )
		{
			area.deleteChild(button.getData());
			updateFigure();
		}
		else if ( button.getId().equals("go") )
		{
			area.getMPE().createFigure(button.getData(), retpath);
		}
		else
		{
			NodeList list;
			int nnum;
			
			int numdocumentation = 0;
			for ( ModelNode documentation : area.getChildrenArray() )
			{
				if ( documentation.getLocalName().equals("documentation") )
				{
					numdocumentation++;
				}
			}
			//there's numdocumentation documentations
			list = area.getChildNodes();
			nnum = 0;
			int lastdocumentation = 0;
			for ( int i = 0; i < list.getLength(); i++ )
			{
				nnum++;
				if ( list.item(i).getNodeType() == Node.ELEMENT_NODE )
				if ( list.item(i).getLocalName().equals("documentation") ) lastdocumentation = nnum;
			}
			//index of last documentation is nnum
			
			int numservice = 0;
			for ( ModelNode service : area.getChildrenArray() )
			{
				if ( service.getLocalName().equals("service") )
				{
					numservice++;
				}
			}
			//there's numservice services
			list = area.getChildNodes();
			nnum = 0;
			int lastservice = 0;
			for ( int i = 0; i < list.getLength(); i++ )
			{
				nnum++;
				if ( list.item(i).getNodeType() == Node.ELEMENT_NODE )
				if ( list.item(i).getLocalName().equals("service") ) lastservice = nnum;
			}
			//index of last service is nnum
			
			int numdataTypes = 0;
			for ( ModelNode dataTypes : area.getChildrenArray() )
			{
				if ( dataTypes.getLocalName().equals("dataTypes") )
				{
					numdataTypes++;
				}
			}
			//there's numdataTypes dataTypess
			list = area.getChildNodes();
			nnum = 0;
			int lastdataTypes = 0;
			for ( int i = 0; i < list.getLength(); i++ )
			{
				nnum++;
				if ( list.item(i).getNodeType() == Node.ELEMENT_NODE )
				if ( list.item(i).getLocalName().equals("dataTypes") ) lastdataTypes = nnum;
			}
			//index of last dataTypes is nnum
			
			if ( button.getId().equals("newDocumentation") )
			{
				Document doc = (Document) area.getRoot().getNode();
				Element elem = doc.createElementNS(area.getNamespaceURI(), area.getNode().getPrefix() + ":" + "documentation");
				String order = (new Integer(area.getMaxOf("order") + 1)).toString();
				elem.setAttribute("name", "DocumentationNode" + order);
				elem.setAttribute("order", order);
				area.insertNode(elem, numdocumentation, lastdocumentation);
			}
			else if ( button.getId().equals("newService") )
			{
				Document doc = (Document) area.getRoot().getNode();
				Element elem = doc.createElementNS(area.getNamespaceURI(), area.getNode().getPrefix() + ":" + "service");
				String number = (new Integer(area.getMaxOf("number") + 1)).toString();
				elem.setAttribute("name", "ServiceName" + number);
				elem.setAttribute("xsi:type", "com:ExtendedServiceType");
				elem.setAttribute("number", number);
				elem.setAttribute("comment", "");
				area.insertNode(elem, numservice + numdocumentation, getmax2(lastdocumentation, lastservice));
			}
			else if ( button.getId().equals("newDataTypes") )
			{
				Document doc = (Document) area.getRoot().getNode();
				Element elem = doc.createElementNS(area.getNamespaceURI(), area.getNode().getPrefix() + ":" + "dataTypes");
				area.insertNode(elem, numservice + numdocumentation, getmax2(lastdocumentation, lastservice));
			}
			else if ( button.getId().equals("newErrors") )
			{
				Document doc = (Document) area.getRoot().getNode();
				Element elem = doc.createElementNS(area.getNamespaceURI(), area.getNode().getPrefix() + ":" + "errors");
				errors = area.insertNode(elem, numservice + numdocumentation + numdataTypes, getmax3(lastdocumentation, lastservice, lastdataTypes));
				elem = doc.createElementNS(area.getNamespaceURI(), area.getNode().getPrefix() + ":" + "error");
				String number = (new Integer(errors.getMaxOf8("error", "number") + 1)).toString();
				elem.setAttribute("name", "ErrorName" + number);
				elem.setAttribute("number", number);
				elem.setAttribute("comment", "");
				errors.appendNode(elem);
				erropen  = true;
			}
			
			updateFigure();
		}
	}
	
	int getmax2(int a, int b)
	{
		if ( a > b ) return a;
		return b;
	}
	
	int getmax3(int a, int b, int c)
	{
		return getmax2(getmax2(a, b), getmax2(b, c));
	}

	@Override
	public void updateFigure()
	{
		removeAll();
		updateGround();
		setFigure();
	}
	
	private void updateGround()
	{
		area.getMPE().printDocument((Document) area.getRoot().getNode());
	}
	
	@Override
	public void mousePressed(MouseEvent me)
	{
		getRootFigure().closeComboBox();
	}
}
