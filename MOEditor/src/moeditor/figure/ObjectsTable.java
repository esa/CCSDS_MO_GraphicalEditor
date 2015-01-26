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

public class ObjectsTable extends TableFigure implements ExpandButtonHandler
{
	private ModelNode specification;
	private ButtonClickHandler buttonClickHandler;
	private ModelNode message;
	private Offspring offspring;
	private ModelNode buttonData = null;
	private String object;
	
	public ObjectsTable(ModelNode node, TheFigure parentFigure, String object)
	{
		super(parentFigure, 5);
		buttonClickHandler = (ButtonClickHandler) parentFigure;
		
		this.object = object;
		
		//setNonFocusColor(ColorConstants.gray);
		setFocusColor(getNonFocusColor());
		setOpaque(false);
		
		specification = node.getRoot().getChildByLocalName("specification");
		
		fillTable();
	}
	
	private void fillTable()
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
				ModelNode features = mService.getChildByLocalName("features");
				if ( features != null )
				{
					ModelNode objects = features.getChildByLocalName("objects");
					if ( objects != null )
					for ( ModelNode type : objects.getChildrenArray() )
					{
						String name = type.getAttribute("name");
						Offspring os = new Offspring(name, serviceBranch);
						os.setNumber(type.getAttribute("number"));
					}
					objects = features.getChildByLocalName("events");
					if ( objects != null )
					for ( ModelNode type : objects.getChildrenArray() )
					{
						String name = type.getAttribute("name");
						Offspring os = new Offspring(name, serviceBranch);
						os.setNumber(type.getAttribute("number"));
					}
				}
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
					NodeList type = serviceItem.getElementsByTagName("com:object");
					for ( int typeNum = 0; typeNum < type.getLength(); typeNum++ )
					{
						if ( type.item(typeNum).getNodeType() == Node.ELEMENT_NODE )
						{
							Element typeItem = (Element) type.item(typeNum);
							String name = typeItem.getAttribute("name");
							Offspring os = new Offspring(name, serviceBranch);
							os.setNumber(typeItem.getAttribute("number"));
						}
					}
					type = serviceItem.getElementsByTagName("com:event");
					for ( int typeNum = 0; typeNum < type.getLength(); typeNum++ )
					{
						if ( type.item(typeNum).getNodeType() == Node.ELEMENT_NODE )
						{
							Element typeItem = (Element) type.item(typeNum);
							String name = typeItem.getAttribute("name");
							Offspring os = new Offspring(name, serviceBranch);
							os.setNumber(typeItem.getAttribute("number"));
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
							//CheckBox checkBox = new CheckBox(this);

							ExpandButton ae = new ExpandButton(this, area);
							ae.setHandler(this);
							ExpandButton se = new ExpandButton(this, service);
							se.setHandler(this);
							
							String serviceName = service.getName() == null ? "" : service.getName();
							
							Button button = new Button(this, "arrow_blue_left.png", area.getName() + "::" + serviceName + "::" + name.getName() + "::" + name.getNumber(), buttonData, true, object);
							button.setNonFocusColor(getNonFocusColor());
							button.setButtonClickHandler(buttonClickHandler);
							
							add(button);
							
							addCell(ae, getEditableColor());
							
							if ( service.getName() == null )
							{
								addCell(new Label());
							}
							else
							{
								addCell(se, getEditableColor());
							}
							
							addCell(name.getName());
							addCell(name.getNumber());
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
			}
		}
	}

	private void setHeading()
	{
		add(new Label());
		addHeadingCell("Area");
		addHeadingCell("Service");
		addHeadingCell("Name");
		addHeadingCell("Number");
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
