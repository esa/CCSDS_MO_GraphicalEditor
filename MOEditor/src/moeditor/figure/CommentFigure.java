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

import org.w3c.dom.Document;

import moeditor.draw2d.EditableLabel;
import moeditor.draw2d.MultiLineEditableLabel;
import moeditor.draw2d.MultiLineEditableLabelPrinter;
import moeditor.draw2d.TableFigure;
import moeditor.draw2d.TheFigure;
import moeditor.model.ModelNode;

public class CommentFigure extends TableFigure implements MultiLineEditableLabelPrinter
{
	private ModelNode node;
	private ArrayList<TheFigure> allowedFigures = new ArrayList<TheFigure>();
	
	public CommentFigure(TheFigure parentFigure, ModelNode node)
	{
		super(parentFigure, 1);
		this.node = node;
		
		setFocusColor(getNonFocusColor());
		allowedFigures.add(this);
		
		setFigure();
	}
	
	public void setAllowedFigures(ArrayList<TheFigure> allowedFigures)
	{
		for ( TheFigure fig : allowedFigures )
		{
			this.allowedFigures.add(fig);
		}
	}
	
	private void setFigure()
	{
		addHeadingCell(node.getAttribute("name") + " Comment");
		EditableLabel comment = new EditableLabel(this, node, "comment", allowedFigures, false);
		comment.setPrinter(this);
		//comment.setData(node);
		//comment.setAttribute("comment");
		/*EditableLabel comment = new EditableLabel(this, true, allowedFigures , node, "comment");
		comment.setPrinter(this);
		comment.setText(node.getAttribute("comment"));*/
		addLabel(comment, getNonFocusColor(), 1);
	}

	@Override
	public void print(MultiLineEditableLabel label, String text)
	{
		label.getData().setAttribute(label.getAttribute(), text);
		node.getMPE().printDocument((Document) node.getRoot().getNode());
	}

	@Override
	public void isAboutToBeClosed()
	{
		getParentFigure().isAboutToBeClosed();
	}
}
