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
import moeditor.draw2d.Button;
import moeditor.draw2d.ButtonClickHandler;
import moeditor.draw2d.EditableLabel;
import moeditor.draw2d.MultiLineEditableLabel;
import moeditor.draw2d.MultiLineEditableLabelPrinter;
import moeditor.draw2d.TableFigure;
import moeditor.draw2d.TheFigure;
import moeditor.model.ModelNode;

public class DocumentationBaseFigure extends TableFigure implements MultiLineEditableLabelPrinter
{
	private ModelNode area;

	public DocumentationBaseFigure(TheFigure parentFigure, ModelNode area)
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
		addHeadingCell("order");
		//addHeadingCell("text");
		fake = new Label();
		add(fake);
	}

	private void setTable()
	{
		for ( ModelNode documentation : area.getChildrenByLocalName("documentation") )
		{
			Button delete = new Button(this, "red_cross.png", "delete", documentation);
			delete.setButtonClickHandler((ButtonClickHandler) getParentFigure());
			add(delete);
			
			EditableLabel name = new EditableLabel(this, documentation, "name");
			name.setPrinter(this);
			addLabel(name);
			
			EditableLabel order = new EditableLabel(this, documentation, "order");
			order.setPrinter(this);
			addLabel(order);
			
			//EditableLabel text = new EditableLabel(getText(documentation), this, documentation, "text");
			//text.setPrinter(this);
			//addLabel(text);
			ArrayList<TheFigure> protectedCombos = new ArrayList<TheFigure>();
			Arrow arrow = new Arrow(this);
			arrow.setProtectedCombos(protectedCombos);
			TextFigure comment = new TextFigure(arrow, documentation, getText(documentation));
			comment.setAllowedFigures(protectedCombos);
			arrow.setFigure(comment);
			add(arrow);
		}
	}

	private String getText(ModelNode documentation)
	{
		String text = documentation.getNode().getTextContent();
		if ( text == null ) return "";
		else 
		{
			String retstr = "";
			for ( String part : text.split("\n") )
			{
				retstr += part.trim() + "\n";
			}
			return retstr.trim();
		}
	}

	@Override
	public void print(MultiLineEditableLabel label, String text)
	{
		if ( !label.getAttribute().equals("text") )
		{
			label.getData().setAttribute(label.getAttribute(), text);
		}
		else
		{
			/*Node doc = label.getData().getNode();
			for ( Node node = doc.getFirstChild(); node != null; node = node.getNextSibling() )
			{
				doc.removeChild(node);
			}
			
			Node newcontent = ((Document) label.getData().getRoot().getNode()).createTextNode(text);
			doc.appendChild(newcontent);*/
			text += "\n";
			for ( int i = 0; i < label.getData().getDepth() - 1; i++ )
			{
				text += "\t";
			}
			label.getData().getNode().setTextContent(text);
		}
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

