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

import moeditor.draw2d.TableFigure;
import moeditor.draw2d.TheFigure;

public class WarnFigure extends TableFigure
{
	private ArrayList<TheFigure> allowedFigures = new ArrayList<TheFigure>();
	private String text;

	public WarnFigure(TheFigure parentFigure, String text)
	{
		super(parentFigure, 1);
		
		//setFocusColor(getNonFocusColor());
		allowedFigures.add(this);
		this.text = text;
		
		setFigure();
	}
	
	public void setAllowedFigures(ArrayList<TheFigure> allowedFigures)
	{
		for ( TheFigure fig : allowedFigures )
		{
			this.allowedFigures .add(fig);
		}
	}
	
	private void setFigure()
	{
		addCell(text);
	}

	@Override
	public void isAboutToBeClosed()
	{
		getParentFigure().isAboutToBeClosed();
	}
}
