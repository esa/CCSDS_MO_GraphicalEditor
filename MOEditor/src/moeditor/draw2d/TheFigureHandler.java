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

import org.eclipse.draw2d.ChangeEvent;
import org.eclipse.draw2d.ChangeListener;
import org.eclipse.draw2d.FigureListener;
import org.eclipse.draw2d.FocusEvent;
import org.eclipse.draw2d.FocusListener;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.KeyEvent;
import org.eclipse.draw2d.KeyListener;
import org.eclipse.draw2d.LayoutManager;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.UpdateManager;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.widgets.Display;

public class TheFigureHandler implements MouseListener, FigureListener, ChangeListener, KeyListener, FocusListener, MouseMotionListener, MouseWheelListener
{
	protected Display display = Display.getDefault();
	private Point location;
	private final TheFigure figure;
	
	public TheFigureHandler(TheFigure figure)
	{
		super();
		this.figure = figure;
	}

	@Override
	public void focusGained(FocusEvent fe) {
		figure.setBackgroundColor(figure.getFocusColor());
		figure.focusGained();
		figure.repaint();
	}

	@Override
	public void focusLost(FocusEvent fe)
	{
		figure.setBackgroundColor(figure.getNonFocusColor());
		if ( figure.getLabel() != null )
		{
			if ( figure.getLabel().isActive() )
			{
				figure.getLabel().setInactiveAndWrite();
			}
		}
		figure.focusLost();
		figure.repaint();
	}

	@Override
	public void keyPressed(KeyEvent ke)
	{
		if ( figure.getLabel() != null ) figure.getLabel().getHandler().keyPressed(ke);
		ke.consume();
	}

	@Override
	public void keyReleased(KeyEvent ke)
	{
		if ( figure.getLabel() != null ) figure.getLabel().getHandler().keyReleased(ke);
		ke.consume();
	}

	@Override
	public void handleStateChanged(ChangeEvent event)
	{
	}

	@Override
	public void figureMoved(IFigure source)
	{
	}

	@Override
	public void mousePressed(MouseEvent me)
	{
		location = me.getLocation();
		me.consume();
		if ( figure.getLabel() != null )
		{
			if ( !figure.getLabel().isActive() ) figure.getLabel().setActive();
		}
		figure.mousePressed(me);
	}

	@Override
	public void mouseReleased(MouseEvent me)
	{
		if (location == null) return;
		location = null;
		me.consume();
	}

	@Override
	public void mouseDoubleClicked(MouseEvent me)
	{
		figure.mouseDoubleClicked(me);
		me.consume();
	}

	@Override
	public void mouseDragged(MouseEvent me)
	{
		if ( !figure.isMovable() )
		{
			return;
		}
		
		if (location == null) return;
		
		Point newLocation = me.getLocation();
		if (newLocation == null) return;
		
		Dimension offset = newLocation.getDifference(location);
		if (offset.width == 0 && offset.height == 0) return;
		
		location = newLocation;
		
		UpdateManager updateMgr = figure.getUpdateManager();
		LayoutManager layoutMgr = figure.getParent().getLayoutManager();
		Rectangle bounds = figure.getBounds();
		updateMgr.addDirtyRegion(figure.getParent(), bounds);
		bounds = bounds.getCopy().translate(offset.width, offset.height);
		layoutMgr.setConstraint(figure, bounds);
		figure.translate(offset.width, offset.height);
		updateMgr.addDirtyRegion(figure.getParent(), bounds);
		me.consume();
	}

	@Override
	public void mouseEntered(MouseEvent me)
	{
		figure.requestFocus();
		me.consume();
	}

	@Override
	public void mouseExited(MouseEvent me)
	{
		me.consume();
	}

	@Override
	public void mouseHover(MouseEvent me)
	{
		me.consume();
	}

	@Override
	public void mouseMoved(MouseEvent me)
	{
		me.consume();
	}

	@Override
	public void mouseScrolled(org.eclipse.swt.events.MouseEvent e) {
		figure.mouseScrolled(e);
	}
}