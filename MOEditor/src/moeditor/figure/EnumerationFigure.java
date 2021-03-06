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
import moeditor.draw2d.CheckBox;
import moeditor.draw2d.EditableLabel;
import moeditor.draw2d.MultiLineEditableLabel;
import moeditor.draw2d.MultiLineEditableLabelPrinter;
import moeditor.draw2d.TableFigure;
import moeditor.draw2d.TheFigure;
import moeditor.model.ModelNode;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Label;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class EnumerationFigure extends TableFigure implements ButtonClickHandler, MultiLineEditableLabelPrinter
{
	private ModelNode enumeration;
	private ArrayList<TheFigure> protectedCombos = new ArrayList<TheFigure>();
	private ArrayList<ModelNode> retpath;
	private boolean flag = false;
	
	public EnumerationFigure(ModelNode node, TheFigure parentFigure, ArrayList<ModelNode> retpath)
	{
		super(parentFigure, 3);
		enumeration = node;
		this.retpath = retpath;
		
		if ( parentFigure instanceof Arrow )
		{
			protectedCombos = ((Arrow) parentFigure).getProtectedCombos();
		}
		protectedCombos.add(this);
		setFocusColor(getNonFocusColor());
		
		fillTable();
	}
	
	public EnumerationFigure(ModelNode node, TheFigure parentFigure, ArrayList<TheFigure> protectedCombos, boolean flag)
	{
		super(parentFigure, 3);
		enumeration = node;
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
		for ( ModelNode item : enumeration.getChildrenArray() )
		{
			EditableLabel value = new EditableLabel(this, item, "value", protectedCombos);
			value.setPrinter(this);
			
			Button delete = new Button(this, "red_cross.png", "Delete item", item, true);
			delete.setButtonClickHandler(this);
			
			addLabelWithButtonLeft(value, getEditableColor(), 1, delete);
			
			EditableLabel nvalue = new EditableLabel(this, item, "nvalue", protectedCombos);
			nvalue.setPrinter(this);
			addLabel(nvalue);
			
			EditableLabel comment = new EditableLabel(this, item, "comment", protectedCombos, false);
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
		
			addCellWithButtonRight(ColorConstants.green, buttonBack, "Go Back", 3);
		}
		
		if ( flag )
		{
			CheckBox cb = new CheckBox(this);
			
			Button ok = new Button(this, "check-yes.png", "defined", enumeration, true, cb);
			ok.setButtonClickHandler((ButtonClickHandler) getParentFigure());
			Button ko = new Button(this, "check-no.png", "canceled", enumeration, true, "optional");
			ko.setButtonClickHandler((ButtonClickHandler) getParentFigure());
			addCellWithTwoButtons(new Label("Name"), getHeadingColor(), 1, ok, ko);
			
			EditableLabel name = new EditableLabel(this, enumeration, "name", protectedCombos);
			name.setPrinter(this);
			
			addLabel(name, 2);
			
			addHeadingCell("Use in a list", 2, 1);
			addCell(cb, 1);
		}
		else
		{
			EditableLabel name = new EditableLabel(this, enumeration, "name", protectedCombos);
			name.setPrinter(this);
			
			addHeadingCell("Name");
			addLabel(name, 2);
		}
		
		EditableLabel shortFormPart = new EditableLabel(this, enumeration, "shortFormPart", protectedCombos);
		shortFormPart.setPrinter(this);
		
		addHeadingCell("Short Form Part");
		addLabel(shortFormPart, 2);
		
		EditableLabel comment = new EditableLabel(this, enumeration, "comment", protectedCombos, false);
		comment.setPrinter(this);
		
		addHeadingCell("Comment");
		addLabel(comment, 2);
		
		Button newButton = new Button(this, "new.png", "New item", true);
		newButton.setButtonClickHandler(this);
		
		addCellWithButtonRight(getHeadingColor(), newButton, "Enumeration Value");
		addHeadingCell("Numerical Value");
		addHeadingCell("Comment");
	}

	@Override
	public void ButtonClicked(Button button)
	{
		if ( button.getId().equals("GoBack") )
		{
			ModelNode back = retpath.get(retpath.size() - 1);
			retpath.remove(back);
			enumeration.getMPE().createFigure(back, retpath);
		}
		else if ( button.getId().equals("New item") )
		{
			Document doc = (Document) enumeration.getRoot().getNode();
			Element elem = doc.createElementNS(enumeration.getNamespaceURI(), enumeration.getNode().getPrefix() + ":" + "item");
			String number = new Integer(enumeration.getMaxOf("nvalue") + 1).toString();
			elem.setAttribute("value", "name" + number);
			elem.setAttribute("nvalue", number);
			elem.setAttribute("comment", "");
			enumeration.appendNode(elem);
			removeAll();
			fillTable();
			updateGround();
		}
		else if ( button.getId().equals("Delete item") )
		{
			enumeration.deleteChild(button.getData());
			removeAll();
			fillTable();
			updateGround();
		}
	}

	@Override
	public void print(MultiLineEditableLabel label, String text)
	{
		label.getData().setAttribute(label.getAttribute(), text);
		updateGround();
	}

	private void updateGround()
	{
		enumeration.getMPE().printDocument((Document) enumeration.getRoot().getNode());
	}
	
	@Override
	public void isAboutToBeClosed()
	{
		if ( flag ) ((OperationMessagesFigure) getParentFigure()).cancelOp(enumeration);
		getParentFigure().isAboutToBeClosed();
	}
}
