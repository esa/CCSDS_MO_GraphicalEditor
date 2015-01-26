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

import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MouseEvent;
import org.w3c.dom.Document;

public class ServiceBaseFigure extends TableFigure implements MultiLineEditableLabelPrinter
{
	private ModelNode area;

	public ServiceBaseFigure(TheFigure parentFigure, ModelNode area)
	{
		super(parentFigure, 4);
		this.area = area;
		setFocusColor(getNonFocusColor());
		
		setFigure();
	}
	
	private void setFigure()
	{
		setHeading();
		setTable();
	}

	private void setHeading()
	{
		Label fake;
		
		fake = new Label();
		add(fake);
		addHeadingCell("name");
		addHeadingCell("number");
		//addHeadingCell("comment");
		fake = new Label();
		add(fake);
	}

	private void setTable()
	{
		for ( ModelNode service : area.getChildrenByLocalName("service") )
		{
			Button delete = new Button(this, "red_cross.png", "delete", service);
			delete.setButtonClickHandler((ButtonClickHandler) getParentFigure());
			add(delete);
			
			EditableLabel name = new EditableLabel(this, service, "name");
			name.setPrinter(this);
			//addLabel(name);this, "info_small.png", "go", dataTypes
			
			Button button = new Button(this, "info_small.png", "go", service);
			button.setButtonClickHandler((ButtonClickHandler) getParentFigure());
			addLabelWithButtonRight(name, getEditableColor(), 1, button);
			
			EditableLabel number = new EditableLabel(this, service, "number");
			number.setPrinter(this);
			addLabel(number);
			
			//EditableLabel comment = new EditableLabel(this, service, "comment", false);
			//comment.setPrinter(this);
			//addLabel(comment);
			ArrayList<TheFigure> protectedCombos = new ArrayList<TheFigure>();
			Arrow arrow = new Arrow(this);
			arrow.setProtectedCombos(protectedCombos);
			CommentFigure comment = new CommentFigure(arrow, service);
			comment.setAllowedFigures(protectedCombos);
			arrow.setFigure(comment);
			add(arrow);
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
		area.getMPE().printDocument((Document) area.getRoot().getNode());
	}
	
	@Override
	public void mousePressed(MouseEvent me)
	{
		getRootFigure().closeComboBox();
	}

}
