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

package moeditor.draw2d;

import moeditor.MultiPageEditor;

import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public class ComboBoxEntry extends TheFigure
{
	private Label label;
	private String value;
	private Display display = Display.getDefault();
	
	public ComboBoxEntry(TheFigure parentFigure, String value, String text)
	{
		super(parentFigure);
		ToolbarLayout layout = new ToolbarLayout();
		setLayoutManager(layout);
		
		label = new Label(new Image(display, MultiPageEditor.class.getResourceAsStream("/item.png")));
		setText(text);
		this.value = value;
		add(label);
	}

	private void setText(String text)
	{
		label.setText(text);
	}
	
	public String getText()
	{
		return label.getText();
	}
	
	public String getValue()
	{
		return value;
	}
	
	@Override
	public void mousePressed(MouseEvent me)
	{
		((ComboBox) getParentFigure().getParentFigure()).selected(getValue());
	}
	
	@Override
	public void focusGained()
	{
	}
	
}
