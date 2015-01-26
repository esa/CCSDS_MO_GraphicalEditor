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

import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public class CheckBox extends TheFigure
{
	private boolean checked = false;
	private Label yes = new Label(new Image(Display.getDefault(), MultiPageEditor.class.getResourceAsStream("/check-yes.png")));
	private Label no = new Label(new Image(Display.getDefault(), MultiPageEditor.class.getResourceAsStream("/check-no.png")));
	
	public CheckBox(TheFigure parentFigure)
	{
		super(parentFigure);
		
		initialize();
	}

	private void initialize()
	{
		GridLayout layout = new GridLayout();
		layout.verticalSpacing = layout.horizontalSpacing = 0;
		layout.marginHeight = layout.marginWidth = 0;
		setLayoutManager(layout);
		
		add(yes);
		setChecked(false);
	}
	
	@Override
	public void mousePressed(MouseEvent me)
	{
		toggleChecked();
	}

	private void toggleChecked()
	{
		if ( isChecked() ) setChecked(false);
		else setChecked(true);
	}

	public boolean isChecked()
	{
		return checked;
	}

	public void setChecked(boolean checked)
	{
		if ( checked )
		{
			remove(no);
			add(yes);
		}
		else
		{
			remove(yes);
			add(no);
		}
		this.checked = checked;
	}
}
