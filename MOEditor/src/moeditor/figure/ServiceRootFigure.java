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

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import moeditor.MultiPageEditor;
import moeditor.draw2d.Arrow;
import moeditor.draw2d.Button;
import moeditor.draw2d.ButtonClickHandler;
import moeditor.draw2d.TableFigure;
import moeditor.draw2d.TheFigure;
import moeditor.model.ModelNode;

public class ServiceRootFigure extends TheFigure implements ButtonClickHandler
{
	public ModelNode dataTypes = null;
	private ModelNode errors = null;
	private ModelNode service;
	private ArrayList<ModelNode> retpath;
	private boolean erropen = false;
	
	private Figure dthFigure = null;
	private Figure dtFigure = null;
	
	private Figure ehFigure = null;
	private Figure eFigure = null;
	private int numdocumentation;
	private int lastdocumentation;
	private int numCS;
	private int lastCS;
	private int numdataTypes;
	private int lastdataTypes;
	public ServiceRootFigure(TheFigure parentFigure, ModelNode service, ArrayList<ModelNode> retpath)
	{
		super(parentFigure);
		
		service.getParent();
		this.service = service;
		
		ToolbarLayout layout = new ToolbarLayout();
		setLayoutManager(layout);
		setFocusColor(getNonFocusColor());
		
		this.retpath = retpath;
		
		setFigure();
	}
	
	private void setFigure()
	{
		if ( retpath.size() != 0 )
		{
			Figure figure = new Figure();
			ToolbarLayout layout = new ToolbarLayout(true);
			figure.setLayoutManager(layout);
			
			TableFigure gobackFig = new TableFigure(this, 1);
			gobackFig.setFocusColor(getNonFocusColor());
			
			Button buttonBack = new Button(this, "arrow_blue_left.png", "GoBack");
			buttonBack.setButtonClickHandler(this);
		
			gobackFig.addCellWithButtonRight(ColorConstants.green, buttonBack, "Go Back", 1);
			
			figure.add(gobackFig);
			add(figure);
		}
		
		//Figure figure = new Figure();
		//ToolbarLayout layout = new ToolbarLayout(true);
		//figure.setLayoutManager(layout);
		//figure.
		add(new ServiceFigure(service, this, retpath));
		//add(figure);
		
		addDocumentationHeading();
		addDataTypesHeading();
		addErrorsHeading();
	}
	
	private void addErrorsHeading()
	{
		errors = service.getChildByLocalName("errors");
		Font font = new Font(null, "Arial", 12, SWT.BOLD);
		
		ehFigure = new Figure();
		ToolbarLayout layout = new ToolbarLayout(true);
		ehFigure.setLayoutManager(layout);
		
		Label label = new Label("Errors");
		label.setFont(font);
		
		Button errorsButton;
		if ( errors == null )
		{
			errorsButton = new Button(this, "new.png", "newErrors", service);
			errorsButton.setButtonClickHandler(this);
		}
		else
		{
			errorsButton = new Button(this, "red_cross.png", "delete", errors);
			errorsButton.setButtonClickHandler(this);
		}
		
		ehFigure.add(label);
		ehFigure.add(errorsButton);
		add(ehFigure);
		
		addErrors();
	}

	private void addErrors()
	{
		if ( errors == null ) {
			eFigure = null;
			return;
		}
		
		eFigure = new Figure();
		ToolbarLayout layout = new ToolbarLayout(true);
		eFigure.setLayoutManager(layout);
		
		Label label = new Label(new Image(getDisplay(), MultiPageEditor.class.getResourceAsStream("/rightarrow.png")));
		
		Button dataTypesInfo = new Button(this, "info_small.png", "go", errors);
		dataTypesInfo.setButtonClickHandler(this);
		
		Arrow arrow = new Arrow(this);
		ArrayList<ModelNode> ret = new ArrayList<ModelNode>();
		for ( ModelNode item : retpath )
		{
			ret.add(item);
		}
		ret.add(service);
		arrow.setFigure(new ErrorsFigure(errors, arrow, ret, false));
		
		eFigure.add(label);
		eFigure.add(dataTypesInfo);
		eFigure.add(arrow);
		add(eFigure);
		
		if ( erropen  )
		{
			ButtonClicked(dataTypesInfo);
			erropen = false;
		}
	}

	private void addDataTypesHeading()
	{
		dataTypes = service.getChildByLocalName("dataTypes");
		Font font = new Font(null, "Arial", 12, SWT.BOLD);
		
		dthFigure = new Figure();
		ToolbarLayout layout = new ToolbarLayout(true);
		dthFigure.setLayoutManager(layout);
		
		Label label = new Label("Data Types");
		label.setFont(font);
		
		Button dataTypesButton;
		if ( dataTypes == null )
		{
			dataTypesButton = new Button(this, "new.png", "newDataTypes", service);
			dataTypesButton.setButtonClickHandler(this);
		}
		else
		{
			dataTypesButton = new Button(this, "red_cross.png", "delete", dataTypes);
			dataTypesButton.setButtonClickHandler(this);
		}
		
		dthFigure.add(label);
		dthFigure.add(dataTypesButton);
		add(dthFigure);
		
		addDataTypes();
	}

	private void addDataTypes()
	{
		 
		if ( dataTypes == null )
		{
			dtFigure = null;
			return;
		}
		
		dtFigure = new Figure();
		ToolbarLayout layout = new ToolbarLayout(true);
		dtFigure.setLayoutManager(layout);
		
		Label label = new Label(new Image(getDisplay(), MultiPageEditor.class.getResourceAsStream("/rightarrow.png")));
		
		Button dataTypesInfo = new Button(this, "info_small.png", "go", dataTypes);
		dataTypesInfo.setButtonClickHandler(this);
		
		Arrow arrow = new Arrow(this);
		ArrayList<ModelNode> ret = new ArrayList<ModelNode>();
		for ( ModelNode item : retpath )
		{
			ret.add(item);
		}
		ret.add(service);
		arrow.setFigure(new DataTypesFigure(arrow, dataTypes, ret, false));
		
		dtFigure.add(label);
		dtFigure.add(dataTypesInfo);
		dtFigure.add(arrow);
		add(dtFigure);
	}

	private void addDocumentationHeading()
	{
		Font font = new Font(null, "Arial", 12, SWT.BOLD);
		
		Figure figure = new Figure();
		ToolbarLayout layout = new ToolbarLayout(true);
		figure.setLayoutManager(layout);
		
		Label label = new Label("Documentation");
		label.setFont(font);
		
		Button newDocumentationButton = new Button(this, "new.png", "newDocumentation", service);
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
		figure.add(new DocumentationBaseFigure(this, service));
		add(figure);
	}


	@Override
	public void ButtonClicked(Button button)
	{
		if ( button.getId().equals("delete") )
		{
			service.deleteChild(button.getData());
			updateFigure();
		}
		else if ( button.getId().equals("go") )
		{
			retpath.add(service);
			service.getMPE().createFigure(button.getData(), retpath);
		}
		else if ( button.getId().equals("GoBack") )
		{
			ModelNode back = retpath.get(retpath.size() - 1);
			retpath.remove(back);
			service.getMPE().createFigure(back, retpath);
		}
		else
		{
			countCont();
			
			if ( button.getId().equals("newDocumentation") )
			{
				Document doc = (Document) service.getRoot().getNode();
				Element elem = doc.createElementNS(service.getNamespaceURI(), service.getNode().getPrefix() + ":" + "documentation");
				String order = (new Integer(service.getMaxOf("order") + 1)).toString();
				elem.setAttribute("name", "DocumentationNode" + order);
				elem.setAttribute("order", order);
				service.insertNode(elem, numdocumentation, lastdocumentation);
			}
			else if ( button.getId().equals("newDataTypes") )
			{
				newDataTypes();
			}
			else if ( button.getId().equals("newErrors") )
			{
				Document doc = (Document) service.getRoot().getNode();
				Element elem = doc.createElementNS(service.getNamespaceURI(), service.getNode().getPrefix() + ":" + "errors");
				errors = service.insertNode(elem, numCS + numdocumentation + numdataTypes, getmax3(lastdocumentation, lastCS, lastdataTypes));
				elem = doc.createElementNS(service.getNamespaceURI(), service.getNode().getPrefix() + ":" + "error");
				String number = (new Integer(errors.getMaxOf8("error", "number") + 1)).toString();
				elem.setAttribute("name", "ErrorName" + number);
				elem.setAttribute("number", number);
				elem.setAttribute("comment", "");
				errors.appendNode(elem);
				erropen = true;
			}
			
			updateFigure();
		}
	}
	
	public ModelNode newDataTypes()
	{
		Document doc = (Document) service.getRoot().getNode();
		Element elem = doc.createElementNS(service.getNamespaceURI(), service.getNode().getPrefix() + ":" + "dataTypes");
		return service.insertNode(elem, numCS + numdocumentation, getmax2(lastdocumentation, lastCS));
	}
	
	public void countCont()
	{
		NodeList list;
		int nnum;
		
		numdocumentation = 0;
		for ( ModelNode documentation : service.getChildrenArray() )
		{
			if ( documentation.getLocalName().equals("documentation") )
			{
				numdocumentation++;
			}
		}
		//there's numdocumentation documentations
		list = service.getChildNodes();
		nnum = 0;
		lastdocumentation = 0;
		for ( int i = 0; i < list.getLength(); i++ )
		{
			nnum++;
			if ( list.item(i).getNodeType() == Node.ELEMENT_NODE )
			if ( list.item(i).getLocalName().equals("documentation") ) lastdocumentation = nnum;
		}
		//index of last documentation is nnum
		
		numCS = 0;
		for ( ModelNode capabilitySet : service.getChildrenArray() )
		{
			if ( capabilitySet.getLocalName().equals("capabilitySet") )
			{
				numCS++;
			}
		}
		//there's numCS CSs
		list = service.getChildNodes();
		nnum = 0;
		lastCS = 0;
		for ( int i = 0; i < list.getLength(); i++ )
		{
			nnum++;
			if ( list.item(i).getNodeType() == Node.ELEMENT_NODE )
			if ( list.item(i).getLocalName().equals("capabilitySet") ) lastCS = nnum;
		}
		//index of last CS is nnum
		
		numdataTypes = 0;
		for ( ModelNode dataTypes : service.getChildrenArray() )
		{
			if ( dataTypes.getLocalName().equals("dataTypes") )
			{
				numdataTypes++;
			}
		}
		//there's numdataTypes dataTypess
		list = service.getChildNodes();
		nnum = 0;
		lastdataTypes = 0;
		for ( int i = 0; i < list.getLength(); i++ )
		{
			nnum++;
			if ( list.item(i).getNodeType() == Node.ELEMENT_NODE )
			if ( list.item(i).getLocalName().equals("dataTypes") ) lastdataTypes = nnum;
		}
		//index of last dataTypes is nnum
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
		service.getMPE().printDocument((Document) service.getRoot().getNode());
	}
	
	public void redraw()
	{
		if ( eFigure != null ) remove(eFigure);
		if ( ehFigure != null ) remove(ehFigure);
		if ( dtFigure != null ) remove(dtFigure);
		if ( dthFigure != null ) remove(dthFigure);
		addDataTypesHeading();
		addErrorsHeading();
	}

}
