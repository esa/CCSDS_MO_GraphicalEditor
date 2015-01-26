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

import moeditor.MultiPageEditor;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public class Arrow extends TheFigure
{
	private Display display = Display.getDefault();
	private TheFigure figure = null;
	private boolean isOpen = false;
	private Label label;
	private ArrayList<TheFigure> protectedCombos = new ArrayList<TheFigure>();
	
	private Image sh;
	private Image l;
	private Image ll;
	
	public Arrow(TheFigure parentFigure)
	{
		super(parentFigure);
		setFocusColor(ColorConstants.white);
		
		GridLayout layout = new GridLayout();
		setLayoutManager(layout);
		
		sh = new Image(display, MultiPageEditor.class.getResourceAsStream("/molnija_short.png"));
		l = new Image(display, MultiPageEditor.class.getResourceAsStream("/molnija.png"));
		ll = new Image(display, MultiPageEditor.class.getResourceAsStream("/molnija_long.png"));
		
		label = new Label(sh);
		
		add(label);
	}
	
	public void setFigure(TheFigure figure)
	{
		this.figure = figure;
	}
	
	@Override
	public void mousePressed(MouseEvent me)
	{
		if ( getProtectedCombos().size() != 0 && isOpen ) return;
		Point location = me.getLocation();
		location.x += 50;
		getRootFigure().closeComboBoxIfNotThese(getProtectedCombos());
		figure.updateFigure();
		getRootFigure().openComboBox(location, figure);
		isOpen = true;
		remove(label);
		label = new Label(ll);
		add(label);
	}
	
	@Override
	public void isAboutToBeClosed()
	{
		isOpen = false;
		/*if ( !isFocus ) */focusLost();
	}
	
	@Override
	public void focusGained()
	{
		if ( isOpen ) return;
		remove(label);
		label = new Label(l);
		add(label);
	}
	
	@Override
	public void focusLost()
	{
		if ( isOpen ) return;
		remove(label);
		label = new Label(sh);
		add(label);
	}

	public void open(Point location)
	{
		focusGained();
		isOpen = true;
		remove(label);
		label = new Label(ll);
		add(label);
		//Point location = new Point(getParentFigure().getBounds().width() + 30, getParentFigure().getBounds().height() * (th) / of);
		//location.x += label.getBounds().height() / 2;
		//location.y += label.getBounds().width();
		getRootFigure().closeComboBoxIfNotThese(getProtectedCombos());
		figure.updateFigure();
		getRootFigure().openComboBox(location, figure);
	}

	public ArrayList<TheFigure> getProtectedCombos()
	{
		return protectedCombos;
	}

	public void setProtectedCombos(ArrayList<TheFigure> protectedCombos)
	{
		this.protectedCombos = protectedCombos;
	}
	
	public void addProtectedCombo(TheFigure protectedCombo)
	{
		this.protectedCombos.add(protectedCombo);
	}
}
