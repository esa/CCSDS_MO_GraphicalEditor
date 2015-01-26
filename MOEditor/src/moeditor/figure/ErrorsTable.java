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

import moeditor.draw2d.Button;
import moeditor.draw2d.ButtonClickHandler;
import moeditor.draw2d.ExpandButton;
import moeditor.draw2d.ExpandButtonHandler;
import moeditor.draw2d.TableFigure;
import moeditor.draw2d.TheFigure;
import moeditor.model.ModelNode;
import moeditor.model.Offspring;

import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MouseEvent;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ErrorsTable extends TableFigure implements ExpandButtonHandler
{
	private ModelNode specification;
	private ButtonClickHandler buttonClickHandler;
	private ModelNode message;
	private Offspring offspring;
	private ModelNode buttonData = null;
	
	public ErrorsTable(ModelNode node, TheFigure parentFigure, ModelNode buttonData)
	{
		super(parentFigure, 6);
		buttonClickHandler = (ButtonClickHandler) parentFigure;
		
		this.buttonData = buttonData;
		
		//setNonFocusColor(ColorConstants.gray);
		setFocusColor(getNonFocusColor());
		setOpaque(false);
		
		specification = node.getRoot().getChildByLocalName("specification");
		
		setTable();
	}
	
	public ErrorsTable(ModelNode node, TheFigure parentFigure)
	{
		super(parentFigure, 6);
		buttonClickHandler = (ButtonClickHandler) parentFigure;
		
		//setNonFocusColor(ColorConstants.gray);
		setFocusColor(getNonFocusColor());
		setOpaque(false);
		
		specification = node.getRoot().getChildByLocalName("specification");
		
		setTable();
	}
	
	private ArrayList<Element> getErrorsOf(Element root)
	{
		ArrayList<Element> result = new ArrayList<Element>();
		NodeList list = root.getElementsByTagName("mal:error");
		for ( int i = 0; i < list.getLength(); i++ )
		{
			result.add((Element) list.item(i));
		}
		return result;
	}
	
	private void setErrorsOf(Node node, Offspring area, Offspring service)
	{
		for ( Element error : getErrorsOf((Element) node) )
		{
			String ename = error.getAttribute("name");
			String enumber = error.getAttribute("number");
			String ecomment = error.getAttribute("comment");
			
			ArrayList<String> attribute = new ArrayList<String>();
			attribute.add(enumber);
			attribute.add(ecomment);
			
			new Offspring(ename, service, attribute);
			/*
			Button button = new Button(this, "arrow_blue_left.png", area.getName() + "::" + service.getName() + "::" + name.getName(), true);
			button.setNonFocusColor(getNonFocusColor());
			button.setButtonClickHandler(buttonClickHandler);
			
			add(button);
			
			addEditableLabel(areaName);
			addEditableLabel(serviceName);
			addEditableLabel(name);
			addEditableLabel(number);
			addEditableLabel(comment);
			*/
		}
	}
	
	private void setTable()
	{
		offspring = new Offspring();
		for ( ModelNode mArea : specification.getChildrenByLocalName("area") )
		{
			String areaName = mArea.getAttribute("name");
			Offspring areaBranch = new Offspring(areaName, offspring);
			
			for ( ModelNode mService : mArea.getChildrenByLocalName("service") )
			{
				String serviceName = mService.getAttribute("name");
				Offspring serviceBranch = new Offspring(serviceName, areaBranch);
				setErrorsOf(mService.getNode(), areaBranch, serviceBranch);
			}
			
			ModelNode errors = mArea.getChildByLocalName("errors");
			if ( errors != null )
			{
				Offspring serviceBranch = new Offspring(null, areaBranch);
				setErrorsOf(errors.getNode(), areaBranch, serviceBranch);
			}
		}
		for ( Document doc : specification.getMPE().getCheckedResources() )
		{
			NodeList area = doc.getElementsByTagName("mal:area");
			for ( int areaNum = 0; areaNum < area.getLength(); areaNum++ )
			{
				Element areaItem = (Element) area.item(areaNum);
				String areaName = areaItem.getAttribute("name");
				Offspring areaBranch = new Offspring(areaName, offspring);
				NodeList service = areaItem.getElementsByTagName("mal:service");
				for ( int serviceNum = 0; serviceNum < service.getLength(); serviceNum++ )
				{
					Element serviceItem = (Element) service.item(serviceNum);
					String serviceName = serviceItem.getAttribute("name");
					Offspring serviceBranch = new Offspring(serviceName, areaBranch);
					setErrorsOf(serviceItem, areaBranch, serviceBranch);
				}
				NodeList child = areaItem.getChildNodes();
				Offspring serviceBranch = new Offspring(null, areaBranch);
				for ( int childNum = 0; childNum < child.getLength(); childNum++ )
				{
					if ( child.item(childNum).getNodeType() == Node.ELEMENT_NODE )
					{
						if ( child.item(childNum).getLocalName().equals("errors") )
						{
							setErrorsOf(child.item(childNum), areaBranch, serviceBranch);
						}
					}
				}
			}
		}
		for ( int i = offspring.getChildren().size() - 1; i >= 0; i-- )
		{
			Offspring area = offspring.getChildren().get(i);
			for ( int j = area.getChildren().size() - 1; j >= 0; j-- )
			{
				Offspring service = area.getChildren().get(j);
				if ( service.getName() == null ) service.setExpanded(true);
				if ( service.getChildren().size() == 0 )
				{
					area.removeChild(service);
				}
			}
			if ( area.getChildren().size() == 0 )
			{
				offspring.removeChild(area);
			}
		}
		drawTable();
	}
	
	private void drawTable()
	{
		setHeading();
		for ( Offspring area : offspring.getChildren() )
		{
			if ( area.isExpanded() )
			{
				for ( Offspring service : area.getChildren() )
				{
					if ( service.isExpanded() )
					{
						for ( Offspring name : service.getChildren() )
						{
							ExpandButton ae = new ExpandButton(this, area);
							ae.setHandler(this);
							ExpandButton se = new ExpandButton(this, service);
							se.setHandler(this);
							
							String serviceName = service.getName() == null ? "" : service.getName();

							Button button = new Button(this, "arrow_blue_left.png", area.getName() + "::" + serviceName + "::" + name.getName(), true, buttonData);
							button.setNonFocusColor(getNonFocusColor());
							button.setButtonClickHandler(buttonClickHandler);
							
							add(button);
							
							addCell(ae, getEditableColor());
							addCell(se, getEditableColor());
							
							addCell(name.getName());
							
							for ( String token : name.getAttribute() )
							{
								addCell(token);
							}
						}
					}
					else
					{
						ExpandButton ae = new ExpandButton(this, area);
						ae.setHandler(this);
						ExpandButton se = new ExpandButton(this, service);
						se.setHandler(this);
						Label fake = new Label();
						addCell(fake, getNonFocusColor());
						addCell(ae, getEditableColor());
						addCell(se, getEditableColor());
						addCell(fake, getNonFocusColor());
						addCell(fake, getNonFocusColor());
						addCell(fake, getNonFocusColor());
					}
				}
			}
			else
			{
				ExpandButton ae = new ExpandButton(this, area);
				ae.setHandler(this);
				Label fake = new Label();
				addCell(fake, getNonFocusColor());
				addCell(ae, getEditableColor());
				addCell(fake, getNonFocusColor());
				addCell(fake, getNonFocusColor());
				addCell(fake, getNonFocusColor());
				addCell(fake, getNonFocusColor());
			}
		}
	}

	private void setHeading()
	{
		addCell(new Label());
		addHeadingCell("Area");
		addHeadingCell("Service");
		addHeadingCell("Name");
		addHeadingCell("Number");
		addHeadingCell("Comment");
	}

	@Override
	public void focusGained()
	{
	}
	
	@Override
	public void mousePressed(MouseEvent me)
	{
	}

	public ModelNode getMessage()
	{
		return message;
	}

	public void setMessage(ModelNode message)
	{
		this.message = message;
	}
	
	@Override
	public void ButtonClicked(ExpandButton button)
	{
		removeAll();
		drawTable();
	}
}
