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

import java.util.ArrayList;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.swt.SWT;

public class OpenedComboBox extends TheFigure
{
	private ArrayList<ComboBoxEntry> comboBoxEntry = new ArrayList<ComboBoxEntry>();
	
	public OpenedComboBox(TheFigure parentFigure, int gridWidth)
	{
		super(parentFigure);
		setFocusColor(ColorConstants.white);
		
		GridLayout layout = new GridLayout(gridWidth, true);
		setLayoutManager(layout);
		
	}

	public ArrayList<ComboBoxEntry> getComboBoxEntry()
	{
		return comboBoxEntry;
	}

	public void addComboBoxEntry(String value, String text)
	{
		ComboBoxEntry newComboBoxEntry = new ComboBoxEntry(this, value, text);
		add(newComboBoxEntry);
		GridData gd = new GridData(SWT.CENTER);
		setConstraint(newComboBoxEntry, gd);
		comboBoxEntry.add(newComboBoxEntry);
	}
	
	@Override
	public void focusGained()
	{
	}
	
	public String getTextByValue(String value)
	{
		String text = "WTF";
		for ( ComboBoxEntry cbe : comboBoxEntry )
		{
			if ( value.equals(cbe.getValue()) ) text = cbe.getText();
		}
		return text;
	}
}
