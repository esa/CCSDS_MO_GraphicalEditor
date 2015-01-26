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

import moeditor.draw2d.Arrow;
import moeditor.draw2d.TableFigure;
import moeditor.draw2d.TheFigure;
import org.eclipse.draw2d.MouseEvent;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ROEnumerationFigure extends TableFigure
{
	private Node enumeration;
	private ArrayList<TheFigure> protectedCombos = new ArrayList<TheFigure>();

	public ROEnumerationFigure(Node node, TheFigure parentFigure)
	{
		super(parentFigure, 3);
		enumeration = node;
		
		if ( parentFigure instanceof Arrow )
		{
			for ( TheFigure fig: ((Arrow) parentFigure).getProtectedCombos() )
			{
				protectedCombos.add(fig);
			}
		}
		protectedCombos.add(this);
		setFocusColor(getNonFocusColor());

		fillTable();
	}

	private void fillTable()
	{
		setHeading();
		setItems();
	}
	
	private void setItems()
	{
		for ( Node item = enumeration.getFirstChild(); item != null; item = item.getNextSibling() )
		{
			if ( item.getNodeType() != Node.ELEMENT_NODE ) continue;
			
			addCell(((Element) item).getAttribute("value"));
			addCell(((Element) item).getAttribute("nvalue"));
			addCell(((Element) item).getAttribute("comment"));
		}
	}

	private void setHeading()
	{
		addHeadingCell("Name");
		addCell(((Element) enumeration).getAttribute("name"), 2);
		
		addHeadingCell("Short Form Part");
		addCell(((Element) enumeration).getAttribute("shortFormPart"), 2);
		
		addHeadingCell("Comment");
		addCell(((Element) enumeration).getAttribute("comment"), 2);
		
		addHeadingCell("Enumeration Value");
		addHeadingCell("Numerical Value");
		addHeadingCell("Comment");
	}

	@Override
	public void mousePressed(MouseEvent me)
	{
		getRootFigure().closeComboBoxIfNotThese(protectedCombos);
	}

	@Override
	public void isAboutToBeClosed()
	{
		getParentFigure().isAboutToBeClosed();
	}
}
