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
import moeditor.draw2d.EditableLabel;
import moeditor.draw2d.MultiLineEditableLabel;
import moeditor.draw2d.MultiLineEditableLabelPrinter;
import moeditor.draw2d.TableFigure;
import moeditor.draw2d.TheFigure;
import moeditor.model.ModelNode;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.geometry.Point;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class DataTypesFigure extends TableFigure implements ButtonClickHandler, MultiLineEditableLabelPrinter
{
	private ModelNode dataTypes;
	private String enumopen = null;
	private int total;
	private int act;
	private ArrayList<ModelNode> retpath;
	private boolean retflag = true;
	private ArrayList<TheFigure> allowedFigures = new ArrayList<TheFigure>();

	public DataTypesFigure(TheFigure parentFigure, ModelNode dataTypes, ArrayList<ModelNode> retpath)
	{
		super(parentFigure, 4);
		this.dataTypes = dataTypes;
		this.retpath = retpath;
		allowedFigures.add(this);
		setFocusColor(getNonFocusColor());
		
		fillTable();
	}
	
	public DataTypesFigure(TheFigure parentFigure, ModelNode dataTypes, ArrayList<ModelNode> retpath, boolean retflag)
	{
		super(parentFigure, 4);
		this.dataTypes = dataTypes;
		this.retpath = retpath;
		this.retflag = retflag;
		allowedFigures.add(this);
		setFocusColor(getNonFocusColor());
		
		fillTable();
	}
	
	private void fillTable()
	{
		act = 0;
		
		if ( retpath.size() != 0 && retflag )
		{
			Button buttonBack = new Button(this, "arrow_blue_left.png", "GoBack");
			buttonBack.setButtonClickHandler(this);
		
			addCellWithButtonRight(ColorConstants.green, buttonBack, "Go Back", 3);
			
			Label fake = new Label();
			add(fake);
			
			act += 1;
		}
		
		setTypes();
	}
	
	private void setHeading()
	{
		addHeadingCell("Name");
		addHeadingCell("Short Form Part");
		addHeadingCell("Comment");
		Label fake = new Label();
		add(fake);
	}

	private void setTypes()
	{
		Label fake;
		
		Button eButton = new Button(this, "new.png", "New enumeration", true);
		eButton.setButtonClickHandler(this);
		addCellWithButtonRight(ColorConstants.green, eButton, "Enumeration Type", 3);
		fake = new Label();
		add(fake);
		setType("enumeration");
		
		Button cButton = new Button(this, "new.png", "New composite", true);
		cButton.setButtonClickHandler(this);
		addCellWithButtonRight(ColorConstants.green, cButton, "Composite Type", 3);
		fake = new Label();
		add(fake);
		setType("composite");
		
		total = act + 4;
	}
	
	private void setType(String typ)
	{
		setHeading();
		for ( ModelNode type : dataTypes.getChildrenByLocalName(typ) )
		{
			act += 1;
			
			EditableLabel name = new EditableLabel(this, type, "name", allowedFigures);
			name.setPrinter(this);
			
			Button delete = new Button(this, "red_cross.png", "Delete type", type, true);
			delete.setButtonClickHandler(this);
			
			Button button = new Button(this, "info_small.png", "Show", type, true);
			button.setButtonClickHandler(this);
			addLabelWithTwoButtons(name, getEditableColor(), 1, button, delete);
			
			if ( type.getLocalName().equals("composite") )
			{
				if ( ( (Element) type.getNode()).hasAttribute("shortFormPart") )
				{
					EditableLabel shortFormPart = new EditableLabel(this, type, "shortFormPart", allowedFigures);
					shortFormPart.setPrinter(this);
					
					Button brekeke = new Button(this, "red_cross.png", "delete attribute", type, true);
					brekeke.setButtonClickHandler(this);
					
					addLabelWithButtonRight(shortFormPart, getEditableColor(), 1, brekeke);
				}
				else
				{
					Button brekeke = new Button(this, "new.png", "add attribute", type, true);
					brekeke.setButtonClickHandler(this);
					
					addCellWithButtonLeft(getEditableColor(), brekeke, "Not used");
				}
			}
			else
			{
				EditableLabel shortFormPart = new EditableLabel(this, type, "shortFormPart", allowedFigures);
				shortFormPart.setPrinter(this);
				addLabel(shortFormPart);
			}
			
			EditableLabel comment = new EditableLabel(this, type, "comment", allowedFigures, false);
			comment.setPrinter(this);
			
			addLabel(comment);
			
			Arrow arrow = new Arrow(this);
			arrow.addProtectedCombo(this);
			if ( type.getLocalName().equals("composite") )
			{
				arrow.setFigure(new CompositeFigure(type, arrow, new ArrayList<ModelNode>()));
				add(arrow);
			}
			else if ( type.getLocalName().equals("enumeration") )
			{
				arrow.setFigure(new EnumerationFigure(type, arrow, new ArrayList<ModelNode>()));
				add(arrow);
				if ( enumopen != null )
				{
					if ( enumopen.equals(type.getAttribute("name")) )
					{
						Point location = new Point(getLocation().x + getBounds().width() + 30, getLocation().y + getBounds().height() * (act + 2) / total);
						arrow.open(location);
						enumopen = null;
					}
				}
			}
		}
	}

	@Override
	public void ButtonClicked(Button button)
	{
		if ( button.getId().equals("Show") )
		{
			if ( retflag ) retpath.add(dataTypes);
			dataTypes.getMPE().createFigure(button.getData(), retpath);
		}
		else if ( button.getId().startsWith("New") )
		{
			String typ = button.getId().split(" ")[1];
			Document doc = (Document) dataTypes.getRoot().getNode();
			Element elem = doc.createElementNS(dataTypes.getNamespaceURI(), dataTypes.getNode().getPrefix() + ":" + typ);
			String number = new Integer(ModelNode.getMax2(dataTypes.getMaxOf8("composite", "shortFormPart"), dataTypes.getMaxOf8("enumeration", "shortFormPart")) + 1).toString();
			elem.setAttribute("name", typ + "Name" + number);
			elem.setAttribute("comment", "");
			elem.setAttribute("shortFormPart", number);
			if ( typ.equals("composite") )
			{
				ModelNode composite = dataTypes.appendNode(elem);
				Element ext = doc.createElementNS(composite.getNamespaceURI(), composite.getNode().getPrefix() + ":" + "type");
				
				ext.setAttribute("area", "MAL");
				ext.setAttribute("name", "Composite");
				ext.setAttribute("list", "false");

				ModelNode extend = composite.getChildByLocalName("extends");
				Element element = doc.createElementNS(composite.getNamespaceURI(), composite.getNode().getPrefix() + ":" + "extends");
				extend = composite.insertNode(element, 0, 0);
				extend.appendNode(ext);
			}
			else if ( typ.equals("enumeration") )
			{
				ModelNode enumeration = dataTypes.appendNode(elem);
				Element ele = doc.createElementNS(enumeration.getNamespaceURI(), enumeration.getNode().getPrefix() + ":" + "item");
				String num = new Integer(enumeration.getMaxOf("nvalue") + 1).toString();
				ele.setAttribute("value", "name" + num);
				ele.setAttribute("nvalue", num);
				ele.setAttribute("comment", "");
				enumeration.appendNode(ele);
				
				enumopen = typ + "Name" + number;
			}
			removeAll();
			fillTable();
			updateGround();
		}
		else if ( button.getId().equals("Delete type") )
		{
			dataTypes.deleteChild(button.getData());
			removeAll();
			fillTable();
			updateGround();
		}
		else if ( button.getId().equals("GoBack") )
		{
			ModelNode back = retpath.get(retpath.size() - 1);
			retpath.remove(back);
			dataTypes.getMPE().createFigure(back, retpath);
		}
		else if ( button.getId().equals("delete attribute") )
		{
			((Element) button.getData().getNode()).removeAttribute("shortFormPart");
			removeAll();
			fillTable();
			updateGround();
		}
		else if ( button.getId().equals("add attribute") )
		{
			String number = new Integer(ModelNode.getMax2(button.getData().getMaxOf8("composite", "shortFormPart"), button.getData().getMaxOf8("enumeration", "shortFormPart")) + 1).toString();
			((Element) button.getData().getNode()).setAttribute("shortFormPart", number);
			removeAll();
			fillTable();
			updateGround();
		}
	}
	
	private void updateGround()
	{
		dataTypes.getMPE().printDocument((Document) dataTypes.getRoot().getNode());
	}

	@Override
	public void print(MultiLineEditableLabel label, String text)
	{
		label.getData().setAttribute(label.getAttribute(), text);
		updateGround();
	}
	
	@Override
	public void mousePressed(MouseEvent me)
	{
		getRootFigure().closeComboBoxIfNotThis(this);
	}
	
	@Override
	public void updateFigure()
	{
		removeAll();
		fillTable();
		updateGround();
	}
	
	@Override
	public void isAboutToBeClosed()
	{
		getParentFigure().isAboutToBeClosed();
	}
}
