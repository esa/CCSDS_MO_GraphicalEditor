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
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ErrorsFigure extends TableFigure implements MultiLineEditableLabelPrinter, ButtonClickHandler
{
	ModelNode errors;
	private ArrayList<TheFigure> protectedCombos = new ArrayList<TheFigure>();
	private ArrayList<ModelNode> retpath;
	private boolean retflag = true;
	
	public ErrorsFigure(ModelNode node, TheFigure parentFigure, ArrayList<ModelNode> retpath)
	{
		super(parentFigure, 3);
		
		if ( parentFigure instanceof Arrow )
		{
			protectedCombos = ((Arrow) parentFigure).getProtectedCombos();
		}
		protectedCombos.add(this);
		this.retpath = retpath;
		
		setFocusColor(getNonFocusColor());
		
		errors = node;
		fillTable();
	}
	
	public ErrorsFigure(ModelNode node, TheFigure parentFigure, ArrayList<ModelNode> retpath, boolean retflag)
	{
		super(parentFigure, 3);
		
		if ( parentFigure instanceof Arrow )
		{
			protectedCombos = ((Arrow) parentFigure).getProtectedCombos();
		}
		protectedCombos.add(this);
		this.retpath = retpath;
		this.retflag = retflag;
		
		setFocusColor(getNonFocusColor());
		
		errors = node;
		fillTable();
	}
	
	private void fillTable()
	{
		if ( retpath.size() != 0 && retflag )
		{
			Button buttonBack = new Button(this, "arrow_blue_left.png", "GoBack");
			buttonBack.setButtonClickHandler(this);
		
			addCellWithButtonRight(ColorConstants.green, buttonBack, "Go Back", 3);
		}
		
		setHeading();
		setErrors();
	}

	private void setErrors()
	{
		for ( ModelNode error : errors.getChildrenByLocalName("error") )
		{
			EditableLabel name = new EditableLabel(this, error, "name", protectedCombos);
			name.setPrinter(this);
			
			Button button = new Button(this, "red_cross.png", "Delete error", error, true);
			button.setButtonClickHandler(this);
			
			EditableLabel number = new EditableLabel(this, error, "number", protectedCombos);
			number.setPrinter(this);
			
			EditableLabel comment = new EditableLabel(this, error, "comment", protectedCombos, false);
			comment.setPrinter(this);
			
			addLabelWithButtonLeft(name, getEditableColor(), 1, button);
			addLabel(number);
			addLabel(comment);
		}
	}

	private void setHeading()
	{
		Button button = new Button(this, "new.png", "New error", true);
		button.setButtonClickHandler(this);
		addCellWithButtonRight(getHeadingColor(), button, "Error");
		
		//addHeadingLabel("Error");
		addHeadingCell("Error #");
		addHeadingCell("Comment");
	}

	@Override
	public void print(MultiLineEditableLabel label, String text)
	{
		label.getData().setAttribute(label.getAttribute(), text);
		updateGround();
	}

	private void updateGround()
	{
		errors.getMPE().printDocument((Document) errors.getRoot().getNode());
	}

	@Override
	public void ButtonClicked(Button button)
	{
		if ( button.getId().equals("New error") )
		{
			Document doc = (Document) errors.getRoot().getNode();
			Element elem = doc.createElementNS(errors.getNamespaceURI(), errors.getNode().getPrefix() + ":" + "error");
			String number = new Integer(errors.getMaxOf8("error", "number") + 1).toString();
			elem.setAttribute("name", "ErrorName" + number);
			elem.setAttribute("number", number);
			elem.setAttribute("comment", "");
			errors.appendNode(elem);
			removeAll();
			fillTable();
			updateGround();
		}
		else if ( button.getId().equals("Delete error") )
		{
			errors.deleteChild(button.getData());
			removeAll();
			fillTable();
			updateGround();
		}
		else if ( button.getId().equals("GoBack") )
		{
			ModelNode back = retpath.get(retpath.size() - 1);
			retpath.remove(back);
			errors.getMPE().createFigure(back, retpath);
		}
	}
	
	@Override
	public void isAboutToBeClosed()
	{
		getParentFigure().isAboutToBeClosed();
	}
}
