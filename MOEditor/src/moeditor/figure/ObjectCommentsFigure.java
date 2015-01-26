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

import moeditor.draw2d.EditableLabel;
import moeditor.draw2d.MultiLineEditableLabel;
import moeditor.draw2d.MultiLineEditableLabelPrinter;
import moeditor.draw2d.TableFigure;
import moeditor.draw2d.TheFigure;
import moeditor.model.ModelNode;

import org.w3c.dom.Document;

public class ObjectCommentsFigure extends TableFigure implements MultiLineEditableLabelPrinter
{
	private ModelNode node;
	private ArrayList<TheFigure> allowedFigures = new ArrayList<TheFigure>();
	private String Object = "ERROR";
	
	public ObjectCommentsFigure(TheFigure parentFigure, ModelNode node, String objectName)
	{
		super(parentFigure, 2);
		this.node = node;
		
		setFocusColor(getNonFocusColor());
		allowedFigures.add(this);
		
		if ( objectName.equals("object") ) Object = "object";
		else if ( objectName.equals("event") ) Object = "event";
		
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
		setComment();
		setObjectComment("relatedObject");
		setObjectComment("sourceObject");
	}
	
	private void setComment()
	{
		addHeadingCell(Object + " Comment");
		EditableLabel comment = new EditableLabel(this, node, "comment", allowedFigures, false);
		comment.setPrinter(this);
		addLabel(comment);
	}
	
	private void setObjectComment(String localName)
	{
		ObjectFigure of = (ObjectFigure) getParentFigure().getParentFigure();
		ModelNode object = node.getChildByLocalName(localName);
		if ( object == null )
		{
			object = of.addObjectChild(node, localName);
		}
		addHeadingCell(localName + " Comment");
		EditableLabel comment = new EditableLabel(this, object, "comment", allowedFigures, false);
		comment.setPrinter(this);
		addLabel(comment);
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
