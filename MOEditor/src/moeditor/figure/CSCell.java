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

import moeditor.draw2d.Button;
import moeditor.draw2d.ButtonClickHandler;
import moeditor.draw2d.ComboBox;
import moeditor.draw2d.ComboBoxPrinter;
import moeditor.draw2d.EditableLabel;
import moeditor.draw2d.MultiLineEditableLabel;
import moeditor.draw2d.MultiLineEditableLabelPrinter;
import moeditor.draw2d.TheFigure;
import moeditor.model.ModelNode;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.swt.SWT;
import org.w3c.dom.Document;

public class CSCell extends TheFigure implements ComboBoxPrinter, ButtonClickHandler, MultiLineEditableLabelPrinter
{
	private EditableLabel label;
	private ModelNode capabilitySet;
	private ServiceFigure serviceFigure;
	
	public CSCell(ModelNode node, ServiceFigure parentFigure)
	{
		super(parentFigure);
		serviceFigure = parentFigure;
		
		setFocusColor(ColorConstants.white);
		
		capabilitySet = node;
		label = new EditableLabel(this, capabilitySet, "number");
		label.setPrinter(this);
		
		setLabel(label);
		
		setBorder(new LineBorder(ColorConstants.black,1));
		GridLayout layout = new GridLayout(3, true);
		setLayoutManager(layout);
		
		add(label);
		GridData gd = new GridData(SWT.CENTER);
		setConstraint(label, gd);
		
		Button cross = new Button(this, "red_cross.png", "delete");
		cross.setButtonClickHandler(this);
		add(cross);
		
		Button neww = new Button(this, "new.png", "new");
		neww.setButtonClickHandler(this);
		add(neww);
	}

	@Override
	public void print(MultiLineEditableLabel label, String text)
	{
		capabilitySet.setAttribute("number", text);
		capabilitySet.getMPE().printDocument((Document) capabilitySet.getRoot().getNode());
	}

	@Override
	public void print(ComboBox comboBox, String text)
	{
		capabilitySet.setAttribute("number", text);
		capabilitySet.getMPE().printDocument((Document) capabilitySet.getRoot().getNode());
	}

	@Override
	public void ButtonClicked(Button button)
	{
		if ( button.getId().equals("new") )
		{
			serviceFigure.newOperation(capabilitySet);
		}
		else if ( button.getId().equals("delete") )
		{
			serviceFigure.removeCS(capabilitySet);
		}
	}
	
	@Override
	public void mousePressed(MouseEvent me)
	{
		getRootFigure().closeComboBox();
	}

}
