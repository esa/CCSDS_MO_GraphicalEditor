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

public class TextFigure extends TableFigure implements MultiLineEditableLabelPrinter
{
		private ModelNode node;
		private ArrayList<TheFigure> allowedFigures = new ArrayList<TheFigure>();
		
		public TextFigure(TheFigure parentFigure, ModelNode node, String text)
		{
			super(parentFigure, 1);
			this.node = node;
			
			setFocusColor(getNonFocusColor());
			allowedFigures.add(this);
			
			setFigure(text);
		}
		
		public void setAllowedFigures(ArrayList<TheFigure> allowedFigures)
		{
			for ( TheFigure fig : allowedFigures )
			{
				this.allowedFigures.add(fig);
			}
		}
		
		private void setFigure(String text)
		{
			addHeadingCell(node.getAttribute("name") + " Text");
			EditableLabel comment = new EditableLabel(this, node, "text", allowedFigures, false, text);
			comment.setPrinter(this);
			addLabel(comment, getNonFocusColor(), 1);
		}

		@Override
		public void print(MultiLineEditableLabel label, String text)
		{
			//label.getData().setAttribute(label.getAttribute(), text);
			text += "\n";
			for ( int i = 0; i < label.getData().getDepth() - 1; i++ )
			{
				text += "\t";
			}
			label.getData().getNode().setTextContent(text);
			node.getMPE().printDocument((Document) node.getRoot().getNode());
		}

		@Override
		public void isAboutToBeClosed()
		{
			getParentFigure().isAboutToBeClosed();
		}
	}
