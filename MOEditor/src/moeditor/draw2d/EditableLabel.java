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

import moeditor.model.ModelNode;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.swt.graphics.Color;

public class EditableLabel extends MultiLineEditableLabel
{
	private static Color bgColor = ColorConstants.lightGray;
	private static Color selColor = ColorConstants.yellow;
	
	public EditableLabel(TheFigure parentFigure, ModelNode data, String attribute, ArrayList<TheFigure> allowedFigures, boolean restricted)
	{
		super(10, 10, bgColor, selColor, data.getAttribute(attribute), parentFigure, restricted);
		setAllowedFigures(allowedFigures);
		setData(data);
		setAttribute(attribute);
	}
	
	public EditableLabel(TheFigure parentFigure, ModelNode data, String attribute)
	{
		super(10, 10, bgColor, selColor, data.getAttribute(attribute), parentFigure, true);
		setAllowedFigures(new ArrayList<TheFigure>());
		setData(data);
		setAttribute(attribute);
	}
	
	public EditableLabel(TheFigure parentFigure, ModelNode data, String attribute, boolean restricted)
	{
		super(10, 10, bgColor, selColor, data.getAttribute(attribute), parentFigure, restricted);
		setAllowedFigures(new ArrayList<TheFigure>());
		setData(data);
		setAttribute(attribute);
	}
	
	public EditableLabel(TheFigure parentFigure, ModelNode data, String attribute, ArrayList<TheFigure> allowedFigures)
	{
		super(10, 10, bgColor, selColor, data.getAttribute(attribute), parentFigure, true);
		setAllowedFigures(allowedFigures);
		setData(data);
		setAttribute(attribute);
	}
	
	public EditableLabel(String text, TheFigure parentFigure, ModelNode data, String attribute)
	{
		super(10, 10, bgColor, selColor, text, parentFigure, false);
		setAllowedFigures(new ArrayList<TheFigure>());
		setData(data);
		setAttribute(attribute);
	}
	
	public EditableLabel(TheFigure parentFigure, ModelNode data, String attribute, ArrayList<TheFigure> allowedFigures, boolean restricted, String text)
	{
		super(10, 10, bgColor, selColor, text, parentFigure, restricted);
		setAllowedFigures(allowedFigures);
		setData(data);
		setAttribute(attribute);
	}
}