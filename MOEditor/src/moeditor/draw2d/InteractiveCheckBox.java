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

import org.eclipse.draw2d.MouseEvent;

public class InteractiveCheckBox extends CheckBox
{
	private CheckBoxCheckedHandler handler = null;
	
	public InteractiveCheckBox(TheFigure parentFigure)
	{
		super(parentFigure);
	}
	
	public void setCheckBoxCheckedHandler(CheckBoxCheckedHandler handler)
	{
		this.handler = handler;
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
		if ( handler != null ) handler.CheckBoxChecked(isChecked());
	}

}
