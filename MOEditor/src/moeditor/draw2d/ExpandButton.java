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
import moeditor.model.Offspring;

import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public class ExpandButton extends TheFigure
{
	private ExpandButtonHandler handler = null;
	private Offspring data = null;
	private Label plus;
	private Label minus;
	
	public ExpandButton(TheFigure parentFigure, Offspring data)
	{
		super(parentFigure);
		setData(data);
		
		GridLayout layout = new GridLayout();
		layout.verticalSpacing = layout.horizontalSpacing = 0;
		layout.marginHeight = layout.marginWidth = 0;
		setLayoutManager(layout);
		
		drawButton();
	}
	
	private void drawButton()
	{
		removeAll();
		if ( data.isExpanded() ) add(minus);
		else add(plus);
	}

	@Override
	public void mousePressed(MouseEvent me)
	{
		data.setExpanded(!data.isExpanded());
		if ( handler != null )
		{
			handler.ButtonClicked(this);
		}
	}

	public ExpandButtonHandler getHandler()
	{
		return handler;
	}

	public void setHandler(ExpandButtonHandler handler)
	{
		this.handler = handler;
	}

	public Offspring getData()
	{
		return data;
	}

	public void setData(Offspring data)
	{
		this.data = data;
		plus = new Label(data.getName(), new Image(Display.getDefault(), MultiPageEditor.class.getResourceAsStream("/plus.png")));
		minus = new Label(data.getName(), new Image(Display.getDefault(), MultiPageEditor.class.getResourceAsStream("/minus.png")));
	}
}
