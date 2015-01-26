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

package moeditor;

import moeditor.draw2d.TableFigure;
import moeditor.draw2d.TheFigure;
import moeditor.model.ObjectObject;

public class ObjectBox extends TableFigure
{
	private ObjectObject object;
	
	public ObjectBox(TheFigure parentFigure, ObjectObject object)
	{
		super(parentFigure, 2);
		
		setObject(object);
		setMovable(true);
		
		fillTable();
	}
	
	private void fillTable()
	{
		addHeadingCell(getObject().getName(), 2, 1);
		addHeadingCell("Number");
		addCell(getObject().getNumber());
		addHeadingCell("Area");
		addCell(getObject().getAreaName());
		addHeadingCell("Service");
		addCell(getObject().getServiceName());
	}

	public ObjectObject getObject()
	{
		return object;
	}

	private void setObject(ObjectObject object)
	{
		this.object = object;
		this.object.setObjectBox(this);
	}
}
