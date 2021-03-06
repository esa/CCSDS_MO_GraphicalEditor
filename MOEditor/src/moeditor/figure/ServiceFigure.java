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

import moeditor.draw2d.Arrow;
import moeditor.draw2d.Button;
import moeditor.draw2d.ButtonClickHandler;
import moeditor.draw2d.ComboBox;
import moeditor.draw2d.ComboBoxPrinter;
import moeditor.draw2d.EditableLabel;
import moeditor.draw2d.MultiLineEditableLabel;
import moeditor.draw2d.MultiLineEditableLabelPrinter;
import moeditor.draw2d.TableFigure;
import moeditor.draw2d.TheFigure;
import moeditor.model.ModelNode;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.swt.SWT;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ServiceFigure extends TableFigure implements MultiLineEditableLabelPrinter, ButtonClickHandler, ComboBoxPrinter
{	
	private ModelNode service;
	private ModelNode area;
	private boolean extended;
	private int plus = 0;
	private ArrayList<ModelNode> retpath;
	
	public ServiceFigure(ModelNode node, TheFigure parentFigure, ArrayList<ModelNode> retpath)
	{
		super(parentFigure, 7);
		
		setFocusColor(ColorConstants.white);
		
		area = node.getParent();
		service = node;
		setExtended(service.getAttribute("xsi:type").equals("com:ExtendedServiceType"));
		this.retpath = retpath;
		fillTable();
	}
	
	private void fillTable()
	{
		setHeadingHeading();
		setHeadingRow();
		setHeading();
		setTable();
	}
	
	private void setHeadingHeading()
	{
		Label fake;
		
		fake = new Label();
		add(fake);
		
		addHeadingCell("Area Identifier");
		addHeadingCell("Service Identifier");
		addHeadingCell("Area\nNumber");
		addHeadingCell("Service\nNumber");
		addHeadingCell("Area\nVersion");
		
		fake = new Label();
		add(fake);
	}
	
	private void setHeadingRow()
	{
		Label fake;
		
		fake = new Label();
		add(fake);
		
		addCell(area.getAttribute("name"));
		EditableLabel serviceName = new EditableLabel(this, service, "name");
		serviceName.setPrinter(this);
		addLabel(serviceName);
		
		addCell(area.getAttribute("number"));
		EditableLabel serviceNumber = new EditableLabel(this, service, "number");
		serviceNumber.setPrinter(this);
		addLabel(serviceNumber);
		addCell(area.getAttribute("version"));
		
		if ( isExtended() )
		{
			Button button = new Button(this, "rightarrow.png", "COM Usage", "features", service);
			button.setButtonClickHandler(this);
			add(button);
		}
		else
		{
			fake = new Label();
			add(fake);
		}
	}
	
	private void setHeading()
	{
		Label fake;

		fake = new Label();
		add(fake);
		
		addHeadingCell("Interaction Pattern");
		addHeadingCell("Operation Identifier");
		addHeadingCell("Operation Number");
		addHeadingCell("Support\nin Replay");
		
		Button button = new Button(this, "new.png", "new");
		button.setButtonClickHandler(this);
		addCellWithButtonRight(getHeadingColor(), button, "Capability\nSet");
		
		fake = new Label();
		add(fake);
	}

	
	@Override
	public void mousePressed(MouseEvent me)
	{
		getRootFigure().closeComboBox();
	}
	
	@Override
	public void print(MultiLineEditableLabel label, String text)
	{
		label.getData().setAttribute(label.getAttribute(), text);
		updateGround();
		/*
		if ( label.equals(serviceName) )
		{
			service.setAttribute("name", text);
			updateGround();
		}
		else if ( label.getData() != null && label.getAttribute() != null )
		{
			ModelNode operation = label.getData();
			String attribute = label.getAttribute();
			operation.setAttribute(attribute, text);
			repaintFigure();
		}*/
	}
	
	private void updateGround()
	{
		service.getMPE().printDocument((Document) service.getRoot().getNode());
	}
	
	private void repaintFigure()
	{
		updateGround();
		service.getMPE().createServiceFigure(service, retpath);
	}
	
	private void repaintFigureWithoutGroundUpdate()
	{
		service.getMPE().createServiceFigure(service, retpath);
	}
	
	@Override
	public void print(ComboBox comboBox, String text) {
		if ( comboBox.getData() != null )
		{
			if ( comboBox.getAttribute() == null )
			{
				ModelNode capabilitySet;
				String number;
				String name;
				String supportInReplay;
				String comment;
				
				Element elem;
				ModelNode ip;
				Document doc;
				
				if ( comboBox.getData().getLocalName().equals("capabilitySet") )
				{
					capabilitySet = comboBox.getData();
					doc = (Document) capabilitySet.getRoot().getNode();
					
					number =  new Integer(service.getMaxOf2("number") + 1).toString();
					name = text + "Name" + number;
					supportInReplay = "false";
					comment = "";
					
					elem = doc.createElementNS(capabilitySet.getNamespaceURI(), capabilitySet.getNode().getPrefix() + ":" + text);
					elem.setAttribute("comment", comment);
					elem.setAttribute("name", name);
					elem.setAttribute("number", number);
					elem.setAttribute("supportInReplay", supportInReplay);
					elem.setAttribute("comment", comment);
					
					ip = capabilitySet.appendNode(elem);
				}
				else
				{
					ModelNode operation = comboBox.getData();
					name = operation.getAttribute("name");
					number = operation.getAttribute("number");
					supportInReplay = operation.getAttribute("supportInReplay");
					comment = operation.getAttribute("comment");
					
					capabilitySet = operation.getParent();
					doc = (Document) capabilitySet.getRoot().getNode();
					
					elem = doc.createElementNS(capabilitySet.getNamespaceURI(), capabilitySet.getNode().getPrefix() + ":" + text);
					elem.setAttribute("comment", comment);
					elem.setAttribute("name", name);
					elem.setAttribute("number", number);
					elem.setAttribute("supportInReplay", supportInReplay);
					elem.setAttribute("comment", comment);
					
					ip = capabilitySet.replaceNode(operation, elem);
				}
				
				addMessagesToIP(doc, ip);
				
				capabilitySet.decAboutToBeEdited();
				repaintFigure();
			}
			else
			{
				comboBox.getData().setAttribute(comboBox.getAttribute(), text);
				repaintFigure();
			}
		}
	}
	
	private void addMessagesToIP(Document doc, ModelNode ip)
	{
		Element messagesNode = doc.createElementNS(ip.getNamespaceURI(), ip.getNode().getPrefix() + ":" + "messages");
		ModelNode messages = ip.appendNode(messagesNode);
		if ( ip.getLocalName().equals("sendIP") )
		{
			addOpearationToMessages(doc, messages, "send");
		}
		else if ( ip.getLocalName().equals("submitIP") )
		{
			addOpearationToMessages(doc, messages, "submit");
		}
		else if ( ip.getLocalName().equals("requestIP") )
		{
			addOpearationToMessages(doc, messages, "request");
			addOpearationToMessages(doc, messages, "response");
		}
		else if ( ip.getLocalName().equals("invokeIP") )
		{
			addOpearationToMessages(doc, messages, "invoke");
			addOpearationToMessages(doc, messages, "acknowledgement");
			addOpearationToMessages(doc, messages, "response");
		}
		else if ( ip.getLocalName().equals("progressIP") )
		{
			addOpearationToMessages(doc, messages, "progress");
			addOpearationToMessages(doc, messages, "acknowledgement");
			addOpearationToMessages(doc, messages, "update");
			addOpearationToMessages(doc, messages, "response");
		}
		else if ( ip.getLocalName().equals("pubsubIP") )
		{
			addOpearationToMessages(doc, messages, "publishNotify");
		}
	}
	
	private void addOpearationToMessages(Document doc, ModelNode messages, String name)
	{
		Element operation = doc.createElementNS(messages.getNamespaceURI(), messages.getNode().getPrefix() + ":" + name);
		operation.setAttribute("comment", "");
		messages.appendNode(operation);
	}

	@Override
	public void ButtonClicked(Button button)
	{
		if ( button.getId().equals("new") && button.getData() == null )
		{
			NodeList list;
			int nnum;
			
			int numdocumentation = 0;
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
			int lastdocumentation = 0;
			for ( int i = 0; i < list.getLength(); i++ )
			{
				nnum++;
				if ( list.item(i).getNodeType() == Node.ELEMENT_NODE )
				if ( list.item(i).getLocalName().equals("documentation") ) lastdocumentation = nnum;
			}
			//index of last documentation is nnum
			
			int numCS = 0;
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
			int lastCS = 0;
			for ( int i = 0; i < list.getLength(); i++ )
			{
				nnum++;
				if ( list.item(i).getNodeType() == Node.ELEMENT_NODE )
				if ( list.item(i).getLocalName().equals("capabilitySet") ) lastCS = nnum;
			}
			//index of last CS is nnum
			
			Document doc = (Document) service.getRoot().getNode();
			Element elem = doc.createElementNS(service.getNamespaceURI(), service.getNode().getPrefix() + ":" + "capabilitySet");
			elem.setAttribute("comment", "");
			elem.setAttribute("number", (new Integer(service.getMaxOf("number") + 1)).toString());
			
			service.insertNode(elem, numCS + numdocumentation, getmax2(lastdocumentation, lastCS));
			repaintFigure();
		}
		else if ( button.getId().equals("delete") && button.getData() != null )
		{
			if ( !button.getData().getLocalName().equals("capabilitySet") )
			{
				ModelNode operation = button.getData();
				ModelNode capabilitySet = operation.getParent();
				capabilitySet.deleteChild(operation);
				repaintFigure();
			}
			else
			{
				ModelNode capabilitySet = button.getData();
				capabilitySet.decAboutToBeEdited();
				repaintFigureWithoutGroundUpdate();
			}
		}
		else if ( button.getId().equals("features") )
		{
			FeaturesFigure figure = new FeaturesFigure(this, service);
			getRootFigure().openComboBox(button.getMouseLocation(), figure);
		}
		/*
		else if ( button.getId().equals("GoBack") )
		{
			ModelNode back = retpath.get(retpath.size() - 1);
			retpath.remove(back);
			area.getMPE().createFigure(back, retpath);
		}*/
	}
	
	int getmax2(int a, int b)
	{
		if ( a > b ) return a;
		return b;
	}
	
	private void setTable()
	{
		int row = 3;
		int minusFactor = 0;
		for ( ModelNode capabilitySet : service.getChildrenArray() )
		{
			if ( capabilitySet.getLocalName().equals("capabilitySet") )
			{
				int verticalSpan = 0;
				
				for ( ModelNode operation : capabilitySet.getChildrenArray() )
				{
					verticalSpan++;
					ComboBox ip = new ComboBox(this, operation);
					ip.setPrinter(this);
					Arrow arrow;
					Button cross;
					
					ip.addComboBoxEntry("sendIP", "SEND");
					ip.addComboBoxEntry("submitIP", "SUBMIT");
					ip.addComboBoxEntry("requestIP", "REQUEST");
					ip.addComboBoxEntry("invokeIP", "INVOKE");
					ip.addComboBoxEntry("progressIP", "PROGRESS");
					ip.addComboBoxEntry("pubsubIP", "PUBLISH-SUBSCRIBE");

					EditableLabel oi = new EditableLabel(this, operation, "name");
					oi.setPrinter(this);
					
					EditableLabel on = new EditableLabel(this, operation, "number");
					on.setPrinter(this);
					
					ComboBox sir = new ComboBox(this, operation, "supportInReplay");
					sir.addComboBoxEntry("true", "Yes");
					sir.addComboBoxEntry("false", "No");
					sir.setPrinter(this);
					
					ip.setText(ip.getTextByValue(operation.getLocalName()));
					sir.setText(operation.getAttribute("supportInReplay"));
					
					cross = new Button(this, "red_cross.png", "delete", operation);
					cross.setButtonClickHandler(this);
					add(cross);
					
					addCell(ip);
					addLabel(oi);
					addLabel(on);
					addCell(sir);
					
					arrow = new Arrow(this);
					//arrow.setProtectedCombos(protectedCombos);
					arrow.setFigure(new OperationFigure(arrow, operation));
					add(arrow);
				}
				for ( int i = 0 ; i < capabilitySet.isAboutToBeEdited(); i++ )
				{
					verticalSpan++;
					ComboBox ip = new ComboBox(this, capabilitySet);
					ip.setPrinter(this);
					
					Label fake;
					Button cross;
					
					ip.addComboBoxEntry("sendIP", "SEND");
					ip.addComboBoxEntry("submitIP", "SUBMIT");
					ip.addComboBoxEntry("requestIP", "REQUEST");
					ip.addComboBoxEntry("invokeIP", "INVOKE");
					ip.addComboBoxEntry("progressIP", "PROGRESS");
					ip.addComboBoxEntry("pubsubIP", "PUBLISH-SUBSCRIBE");

					ComboBox sir = new ComboBox(this);
					sir.addComboBoxEntry("true", "Yes");
					sir.addComboBoxEntry("false", "No");
					
					ip.setText("NEW IP");
					sir.setText("NEW supportInReplay");
					
					cross = new Button(this, "red_cross.png", "delete", capabilitySet);
					cross.setButtonClickHandler(this);
					add(cross);
					
					addCell(ip);
					addCell("NEW name");
					addCell("NEW number");
					addCell(sir);
					
					fake = new Label();
					add(fake);
				}
				addCSLabel(capabilitySet, row, verticalSpan, minusFactor);
				row += verticalSpan == 0 ? 1 : verticalSpan;
				minusFactor += ( verticalSpan == 0 ? 1 : verticalSpan ) - 1;
			}
		}
	}

	private void addCSLabel(ModelNode capabilitySet, int row, int verticalSpan,	int minusFactor)
	{
		if ( verticalSpan == 0 ){
			for ( int i = 0; i < 6; i++ ) // 7 - 1 = 6 Tos nevedels?
			{
				add(new Label());
			}
		}
		
		CSCell cs = new CSCell(capabilitySet, this);
		add(cs, row * 7 + 5 + plus - minusFactor);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, verticalSpan);
		setConstraint(cs, gridData);
	}

	public void removeCS(ModelNode capabilitySet)
	{
		service.deleteChild(capabilitySet);
		repaintFigure();
	}

	public void newOperation(ModelNode capabilitySet)
	{
		capabilitySet.incAboutToBeEdited();
		repaintFigureWithoutGroundUpdate();
	}

	public boolean isExtended()
	{
		return extended;
	}

	public void setExtended(boolean extended)
	{
		this.extended = extended;
	}
}
