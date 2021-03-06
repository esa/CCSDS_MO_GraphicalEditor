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

import org.eclipse.draw2d.ChangeListener;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

public class TheFigure extends Figure
{
	private static TheFigureHandler theEventHandler;
	private Color focusColor = ColorConstants.lightGray;
	private Color nonFocusColor  = ColorConstants.white;
	private TheFigure parentFigure = null;
	private TheFigure rootFigure;
	private ArrayList<TheFigure> comboBox = new ArrayList<TheFigure>();
	private FigureCanvas canvas = null;
	private MultiLineEditableLabel label = null;
	private boolean isMovable = false;
	
	public TheFigure(TheFigure parentFigure)
	{
		setParentFigure(parentFigure);
		init();
	}
	
	public TheFigure()
	{
		init();
	}
	
	public TheFigure(FigureCanvas canvas)
	{
		setCanvas(canvas);
		init();
	}

	public void init()
	{
		setOpaque(true);
		setRequestFocusEnabled(true);
		setFocusTraversable(true);
		theEventHandler = new TheFigureHandler(this);
		hookEventHandler(theEventHandler);
		if ( getParentFigure() == null )
		{
			setRootFigure(this);
		}
		else
		{
			setRootFigure(getParentFigure().getRootFigure());
		}
	}
	
	public TheFigureHandler getEventHandler()
	{
		return theEventHandler;
	}

	private void hookEventHandler(TheFigureHandler handler)
	{
		addMouseListener(handler);
		addMouseMotionListener(handler);
		addChangeListener(handler);
		addKeyListener(handler);
		addFocusListener(handler);
	}
	
	public void unhookEventHandler()
	{
		removeMouseListener(theEventHandler);
		removeMouseMotionListener(theEventHandler);
		removeChangeListener(theEventHandler);
		removeKeyListener(theEventHandler);
		removeFocusListener(theEventHandler);
	}

	private void removeChangeListener(TheFigureHandler handler)
	{
		removeListener(ChangeListener.class, handler);
	}

	private void addChangeListener(TheFigureHandler handler)
	{
		addListener(ChangeListener.class, handler);
	}

	public void openComboBox(Point location, TheFigure comboBox)
	{
		this.comboBox.add(comboBox);
		add(comboBox);
		setConstraint(comboBox, new Rectangle(location.x, location.y, MultiPageEditor.widght, MultiPageEditor.widght));
		/*if ( getCanvas().getViewport() != null ) *///getCanvas().getViewport().setViewLocation(location);
		//getCanvas().scrollTo(location.x + comboBox.getSize().width, location.y);
	}

	public void closeComboBoxIfNotThis(TheFigure figure)
	{
		for ( int i = comboBox.size() - 1; i >= 0; i-- )
		{
			if ( !comboBox.get(i).equals(figure) )
			{
				comboBox.get(i).isAboutToBeClosed();
				remove(comboBox.get(i));
				comboBox.remove(i);
			}
		}
	}
	
	public void closeComboBox()
	{
		for ( int i = comboBox.size() - 1; i >= 0; i-- )
		{
			comboBox.get(i).isAboutToBeClosed();
			remove(comboBox.get(i));
			comboBox.remove(i);
		}
	}
	
	public void isAboutToBeClosed()
	{	
	}

	public Color getFocusColor()
	{
		return focusColor;
	}

	public Color getNonFocusColor()
	{
		return nonFocusColor;
	}

	public void setFocusColor(Color focusColor)
	{
		this.focusColor = focusColor;
		if ( hasFocus() ) setBackgroundColor(focusColor);
	}
	
	public void setNonFocusColor(Color nonFocusColor)
	{
		this.nonFocusColor = nonFocusColor;
		if ( !hasFocus() ) setBackgroundColor(nonFocusColor);
	}

	public TheFigure getParentFigure()
	{
		return parentFigure;
	}

	public void setParentFigure(TheFigure parentFigure)
	{
		this.parentFigure = parentFigure;
	}

	public TheFigure getRootFigure()
	{
		return rootFigure;
	}

	public void setRootFigure(TheFigure rootFigure)
	{
		this.rootFigure = rootFigure;
	}
	
	public void setColor(Color color)
	{
		setNonFocusColor(color);
		setBackgroundColor(color);
	}
	
/*	public void resetColor()
	{
		setBackgroundColor(getNonFocusColor());
	}*/

	public void mouseDoubleClicked(MouseEvent me)
	{
	}

	public void focusGained()
	{
	}
	
	public void focusLost()
	{
	}

	public void mouseScrolled(org.eclipse.swt.events.MouseEvent e)
	{	
	}

	public void mousePressed(MouseEvent me)
	{
		//getRootFigure().closeComboBox();
	}

	public FigureCanvas getCanvas()
	{
		return canvas;
	}

	public void setCanvas(FigureCanvas canvas)
	{
		this.canvas = canvas;
		this.canvas.getViewport().setContents(this);
	}

	protected static Display getDisplay() {
		Display display = Display.getCurrent();
		//may be null if outside the UI thread
		if (display == null) display = Display.getDefault();
		return display;
	}

	public boolean closeComboBoxIfNotThese(ArrayList<TheFigure> protectedCombos)
	{
		boolean retval = false;
		for ( int i = comboBox.size() - 1; i >= 0; i-- )
		{
			boolean isProtected = false;
			for ( TheFigure figure : protectedCombos )
			{
				if ( comboBox.get(i).equals(figure) )
				{
					isProtected = true;
					break;
				}
			}
			
			if ( !isProtected )
			{
				retval = true;
				comboBox.get(i).isAboutToBeClosed();
				remove(comboBox.get(i));
				comboBox.remove(i);
			}
		}
		return retval;
	}
	
	public void updateFigure()
	{
		
	}

	public MultiLineEditableLabel getLabel()
	{
		return label;
	}

	public void setLabel(MultiLineEditableLabel label)
	{
		this.label = label;
	}

	public boolean isMovable()
	{
		return isMovable;
	}

	public void setMovable(boolean isMovable)
	{
		this.isMovable = isMovable;
	}

}
