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
import org.w3c.dom.NodeList;

public class ROFundamentalFigure extends TableFigure
{
	private Node fundamental;
	private ArrayList<TheFigure> protectedCombos = new ArrayList<TheFigure>();

	public ROFundamentalFigure(Node node, TheFigure parentFigure)
	{
		super(parentFigure, 2);
		fundamental = node;
		
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
		addHeadingCell("Name");
		addCell(((Element) fundamental).getAttribute("name"));
		
		addHeadingCell("Comment");
		addCell(((Element) fundamental).getAttribute("comment"));
		
		addHeadingCell("Extends");
		NodeList exts = ((Element) fundamental).getElementsByTagNameNS("*", "extends");
		if ( exts.getLength() != 1 )
		{
			addCell("");
		}
		else
		{
			NodeList typs = ((Element) exts.item(0)).getElementsByTagNameNS("*", "type");
			if ( typs.getLength() != 1 )
			{
				addCell("");
			}
			else
			{
				Element type = (Element) typs.item(0);
				String area = type.getAttribute("area");
				String name = type.getAttribute("name");
				String temp = area + "::" + name;
				String result = type.getAttribute("list").equals("true") ? "List<" + temp  + ">" : temp;
				addCell(result);
			}
		}		
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
