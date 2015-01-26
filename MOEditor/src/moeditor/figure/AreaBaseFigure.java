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

import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MouseEvent;
import org.w3c.dom.Document;

import moeditor.draw2d.Arrow;
import moeditor.draw2d.EditableLabel;
import moeditor.draw2d.MultiLineEditableLabel;
import moeditor.draw2d.MultiLineEditableLabelPrinter;
import moeditor.draw2d.TableFigure;
import moeditor.draw2d.TheFigure;
import moeditor.model.ModelNode;

public class AreaBaseFigure extends TableFigure implements MultiLineEditableLabelPrinter
{
	private ModelNode area;

	public AreaBaseFigure(TheFigure parentFigure, ModelNode area)
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
		
		addHeadingCell("Name");
		addHeadingCell("Number");
		addHeadingCell("Version");
		//addHeadingCell("Comment");
		
		fake = new Label();
		add(fake);
		
		EditableLabel name = new EditableLabel(this, area, "name");
		name.setPrinter(this);
		addLabel(name);
		
		EditableLabel number = new EditableLabel(this, area, "number");
		number.setPrinter(this);
		addLabel(number);
		
		EditableLabel version = new EditableLabel(this, area, "version");
		version.setPrinter(this);
		addLabel(version);
		
		ArrayList<TheFigure> protectedCombos = new ArrayList<TheFigure>();
		Arrow arrow = new Arrow(this);
		arrow.setProtectedCombos(protectedCombos);
		CommentFigure comment = new CommentFigure(arrow, area);
		comment.setAllowedFigures(protectedCombos);
		arrow.setFigure(comment);
		add(arrow);
		
		/*
		EditableLabel comment = new EditableLabel(this, area, "comment", false);
		comment.setPrinter(this);
		addLabel(comment);
		*/
		
		//fake = new Label();
		//add(fake);
	}

	private void setTable()
	{
		
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
	}
	
	private void updateGround()
	{
		area.getMPE().printDocument((Document) area.getRoot().getNode());
	}
}
