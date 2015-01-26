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
import moeditor.draw2d.CheckBox;
import moeditor.draw2d.ComboBox;
import moeditor.draw2d.ComboBoxPrinter;
import moeditor.draw2d.EditableLabel;
import moeditor.draw2d.MultiLineEditableLabel;
import moeditor.draw2d.MultiLineEditableLabelPrinter;
import moeditor.draw2d.TableFigure;
import moeditor.draw2d.TheFigure;
import moeditor.model.ModelNode;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MouseEvent;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class CompositeFigure extends TableFigure implements ButtonClickHandler, MultiLineEditableLabelPrinter, ComboBoxPrinter
{
	private ModelNode composite;
	private DataTypesTable dataTypes;
	private ArrayList<TheFigure> protectedCombos = new ArrayList<TheFigure>();
	private ArrayList<ModelNode> retpath;
	private boolean flag = false;

	public CompositeFigure(ModelNode node, TheFigure parentFigure, ArrayList<ModelNode> retpath)
	{
		super(parentFigure, 4);
		composite = node;
		this.retpath = retpath;
		
		if ( parentFigure instanceof Arrow )
		{
			protectedCombos = ((Arrow) parentFigure).getProtectedCombos();
		}
		protectedCombos.add(this);
		setFocusColor(getNonFocusColor());

		fillTable();
	}
	
	public CompositeFigure(ModelNode node, TheFigure parentFigure, ArrayList<TheFigure> protectedCombos, boolean flag)
	{
		super(parentFigure, 4);
		composite = node;
		this.retpath = new ArrayList<ModelNode>();
		this.flag = true;
		
		this.protectedCombos.add(this);
		for ( TheFigure fig : protectedCombos )
		{
			this.protectedCombos.add(fig);
		}
		setFocusColor(getNonFocusColor());

		fillTable();
	}
	
	private void fillTable()
	{
		setHeading();
		setItems();
	}

	private void setItems()
	{
		for ( ModelNode field : composite.getChildrenArray() )
		{
			if ( !field.getLocalName().equals("field") ) continue;
			ModelNode type = field.getChildByLocalName("type");
			String result;
			if ( type != null )
			{
				String area = type.getAttribute("area");
				String name = type.getAttribute("name");
				String temp = area + "::" + name;
				result = type.getAttribute("list").equals("true") ? "List<" + temp  + ">" : temp;
			}
			else result = "UNKNOWN";
			
			Button button = new Button(this, "new.png", "New type", field, true);
			button.setButtonClickHandler(this);
			
			EditableLabel name = new EditableLabel(this, field, "name", protectedCombos);
			name.setPrinter(this);
			
			Button delete = new Button(this, "red_cross.png", "Delete type", field, true);
			delete.setButtonClickHandler(this);
			
			addLabelWithButtonLeft(name, getEditableColor(), 1, delete);
			
			addCellWithButtonRight(getEditableColor(), button, result);
			
			
			ComboBox canBeNull = new ComboBox(this, field, "canBeNull", protectedCombos);
			canBeNull.addComboBoxEntry("true", "Yes");
			canBeNull.addComboBoxEntry("false", "No");
			canBeNull.setText(field.getAttribute("canBeNull"));
			canBeNull.setPrinter(this);
			
			addCell(canBeNull);
			
			EditableLabel comment = new EditableLabel(this, field, "comment", protectedCombos, false);
			comment.setPrinter(this);
			
			addLabel(comment);
		}
	}

	private void setHeading()
	{
		if ( retpath.size() != 0 )
		{
			Button buttonBack = new Button(this, "arrow_blue_left.png", "GoBack");
			buttonBack.setButtonClickHandler(this);
		
			addCellWithButtonRight(ColorConstants.green, buttonBack, "Go Back", 4);
		}
		
		if ( flag )
		{
			CheckBox cb = new CheckBox(this);
			
			Button ok = new Button(this, "check-yes.png", "defined", composite, true, cb);
			ok.setButtonClickHandler((ButtonClickHandler) getParentFigure());
			Button ko = new Button(this, "check-no.png", "canceled", composite, true, "optional");
			ko.setButtonClickHandler((ButtonClickHandler) getParentFigure());
			addCellWithTwoButtons(new Label("Name"), getHeadingColor(), 1, ok, ko);
			
			EditableLabel name = new EditableLabel(this, composite, "name", protectedCombos);
			name.setPrinter(this);
			
			addLabel(name, 3);
			
			addHeadingCell("Use in a list", 3, 1);
			addCell(cb, 1);
		}
		else
		{
			addHeadingCell("Name");
			EditableLabel name = new EditableLabel(this, composite, "name", protectedCombos);
			name.setPrinter(this);
			
			addLabel(name, 3);
		}
		
		setExtend();
		
		addHeadingCell("Short Form Part");
		
		if ( ( (Element) composite.getNode()).hasAttribute("shortFormPart") )
		{
			EditableLabel shortFormPart = new EditableLabel(this, composite, "shortFormPart", protectedCombos);
			shortFormPart.setPrinter(this);
			
			Button delete = new Button(this, "red_cross.png", "delete attribute", composite, true);
			delete.setButtonClickHandler(this);
			
			addLabelWithButtonRight(shortFormPart, getEditableColor(), 3, delete);
		}
		else
		{
			Button delete = new Button(this, "new.png", "add attribute", composite, true);
			delete.setButtonClickHandler(this);
			
			addCellWithButtonLeft(getEditableColor(), delete, "Not used", 3);
		}
		
		Button button = new Button(this, "new.png", "New field", composite, true);
		button.setButtonClickHandler(this);
		addCellWithButtonRight(getHeadingColor(), button, "Field");
		
		addHeadingCell("Type");
		addHeadingCell("Nullable");
		addHeadingCell("Comment");
	}

	private void setExtend()
	{
		Button button = new Button(this, "new.png", "New extends", composite, true);
		button.setButtonClickHandler(this);
		addCellWithButtonRight(getHeadingColor(), button, "Extends");
		//addHeadingCell("Extends");
		
		ModelNode extend = composite.getChildByLocalName("extends");
		if ( extend != null )
		{
			ModelNode type = extend.getChildByLocalName("type");
			if ( type != null )
			{
				String area = type.getAttribute("area");
				String name = type.getAttribute("name");
				String temp = area + "::" + name;
				String result = type.getAttribute("list").equals("true") ? "List<" + temp  + ">" : temp;
				addCell(result, 3);
			}
			else
			{
				addCell("", getEditableColor(), 3, 1);
			}
		}
		else
		{
			addCell("", getEditableColor(), 3, 1);
		}
	}

	@Override
	public void ButtonClicked(Button button)
	{
		if ( button.getId().equals("New extends") )
		{
			//getRootFigure().closeComboBox();
			dataTypes = new DataTypesTable(composite, this);
			dataTypes.setMessage(button.getData());
			getRootFigure().openComboBox(button.getMouseLocation(), dataTypes);
		}
		else if ( button.getId().equals("New field") )
		{
			addField();
		}
		else if ( button.getId().equals("New type") )
		{
			//getRootFigure().closeComboBox();
			dataTypes = new DataTypesTable(composite, this);
			dataTypes.setMessage(button.getData());
			getRootFigure().openComboBox(button.getMouseLocation(), dataTypes);
		}
		else if ( button.getId().equals("Delete type") )
		{
			button.getData().getParent().deleteChild(button.getData());
			removeAll();
			fillTable();
			updateGround();
		}
		else if ( button.getId().equals("GoBack") )
		{
			ModelNode back = retpath.get(retpath.size() - 1);
			retpath.remove(back);
			composite.getMPE().createFigure(back, retpath);
		}
		else if ( button.getId().equals("delete attribute") )
		{
			((Element) composite.getNode()).removeAttribute("shortFormPart");
			removeAll();
			fillTable();
			updateGround();
		}
		else if ( button.getId().equals("add attribute") )
		{
			String number = new Integer(ModelNode.getMax2(composite.getMaxOf8("composite", "shortFormPart"), composite.getMaxOf8("enumeration", "shortFormPart")) + 1).toString();
			((Element) composite.getNode()).setAttribute("shortFormPart", number);
			removeAll();
			fillTable();
			updateGround();
		}
		else {
			addDataType(dataTypes.getMessage(), button.getId(), ((CheckBox) button.getLink()).isChecked());
		}
	}

	private void addField()
	{
		Document doc = (Document) composite.getRoot().getNode();
		Element elem = doc.createElementNS(composite.getNamespaceURI(), composite.getNode().getPrefix() + ":" + "field");
		elem.setAttribute("name", "name" + (composite.getChildrenArray().size() + 1) );
		elem.setAttribute("comment", "");
		elem.setAttribute("canBeNull", "true");
		ModelNode field = composite.appendNode(elem);
		elem = doc.createElementNS(composite.getNamespaceURI(), composite.getNode().getPrefix() + ":" + "type");
		elem.setAttribute("name", "String");
		elem.setAttribute("area", "MAL");
		field.appendNode(elem);
		removeAll();
		fillTable();
		updateGround();
	}

	private void addDataType(ModelNode message, String text, boolean list)
	{
		Document doc = (Document) composite.getRoot().getNode();
		
		Element elem = doc.createElementNS(composite.getNamespaceURI(), composite.getNode().getPrefix() + ":" + "type");
		
		int i = 0;
		for ( String item : text.split("::") )
		{
			switch ( i )
			{
			case 0:
				elem.setAttribute("area", item);
				break;
			case 1:
				if ( !item.isEmpty() ) elem.setAttribute("service", item);
				break;
			case 2:
				elem.setAttribute("name", item);
				break;
			default:
				MultiPageEditor.getRedStream().println("CompositeFigure.java: The ID is invalid: " + text);
			}
			i++;
		}
		elem.setAttribute("list", list ? "true" : "false");

		if ( message.equals(composite) )
		{
			ModelNode extend = composite.getChildByLocalName("extends");
			if ( extend != null )
			{
				composite.deleteChild(extend);
			}
			Element element = doc.createElementNS(composite.getNamespaceURI(), composite.getNode().getPrefix() + ":" + "extends");
			extend = composite.insertNode(element, 0, 0);
			extend.appendNode(elem);
		}
		else
		{
			ModelNode typ = message.getChildByLocalName("type");
			if ( typ != null )
			{
				message.deleteChild(typ);
			}
			message.appendNode(elem);
		}
		
		getRootFigure().closeComboBoxIfNotThese(protectedCombos);
		removeAll();
		fillTable();
		updateGround();
	}
	
	private void updateGround()
	{
		composite.getMPE().printDocument((Document) composite.getRoot().getNode());
	}
	
	@Override
	public void mousePressed(MouseEvent me)
	{
		getRootFigure().closeComboBoxIfNotThese(protectedCombos);
	}

	@Override
	public void print(MultiLineEditableLabel label, String text)
	{
		ModelNode data = label.getData();
		data.setAttribute(label.getAttribute(), text);
		//removeAll();
		//fillTable();
		updateGround();
	}

	@Override
	public void print(ComboBox comboBox, String text)
	{
		ModelNode data = comboBox.getData();
		data.setAttribute(comboBox.getAttribute(), text);
		//removeAll();
		//fillTable();
		updateGround();		
	}
	
	@Override
	public void isAboutToBeClosed()
	{
		if ( flag ) ((OperationMessagesFigure) getParentFigure()).cancelOp(composite);
		getParentFigure().isAboutToBeClosed();
	}
}
