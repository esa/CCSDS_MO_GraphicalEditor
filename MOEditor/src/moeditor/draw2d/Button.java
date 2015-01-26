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

import moeditor.MultiPageEditor;
import moeditor.model.ModelNode;

import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public class Button extends TheFigure
{
	private Display display = Display.getDefault();
	private ButtonClickHandler buttonClickHandler = null;
	private String id;
	private ModelNode data = null;
	private String picture;
	private boolean inCombo = false;
	private Point mouseLocation = null;
	private TheFigure link = null;
	private String text = null;
	private String info = null;
	
	public Button(TheFigure parentFigure, String picture, String id)
	{
		super(parentFigure);
		setId(id);
		setPicture(picture);
		
		initialize();
	}

	public Button(TheFigure parentFigure, String picture, String id, ModelNode data)
	{
		super(parentFigure);
		setId(id);
		setPicture(picture);
		setData(data);
		
		initialize();
	}
	
	public Button(TheFigure parentFigure, String picture, String id, boolean isInCombo)
	{
		super(parentFigure);
		setId(id);
		setPicture(picture);
		setInCombo(isInCombo);
		
		initialize();
	}
	
	public Button(TheFigure parentFigure, String picture, String id, boolean isInCombo, ModelNode data)
	{
		super(parentFigure);
		setId(id);
		setPicture(picture);
		setInCombo(isInCombo);
		setData(data);
		
		initialize();
	}
	
	public Button(TheFigure parentFigure, String picture, String text, String id, boolean isInCombo)
	{
		super(parentFigure);
		setId(id);
		setPicture(picture);
		setInCombo(isInCombo);
		setText(text);
		
		initialize();
	}
	
	public Button(TheFigure parentFigure, String picture, String text, String id, ModelNode data)
	{
		super(parentFigure);
		setId(id);
		setPicture(picture);
		setData(data);
		setText(text);
		
		initialize();
	}
	
	public Button(TheFigure parentFigure, String picture, String text, String id, ModelNode data, boolean isInCombo)
	{
		super(parentFigure);
		setId(id);
		setPicture(picture);
		setData(data);
		setText(text);
		setInCombo(isInCombo);
		
		initialize();
	}
	
	public Button(TheFigure parentFigure, String picture, String id, boolean isInCombo, TheFigure link)//, ModelNode data)
	{
		super(parentFigure);
		setId(id);
		setPicture(picture);
		setInCombo(isInCombo);
		setLink(link);
		//setData(data);
		
		initialize();
	}
	
	public Button(TheFigure parentFigure, String picture, String id, boolean isInCombo, TheFigure link, ModelNode data)
	{
		super(parentFigure);
		setId(id);
		setPicture(picture);
		setInCombo(isInCombo);
		setLink(link);
		setData(data);
		
		initialize();
	}
	
	public Button(TheFigure parentFigure, String picture, String id, ModelNode data, boolean isInCombo)
	{
		super(parentFigure);
		setId(id);
		setPicture(picture);
		setData(data);
		setInCombo(isInCombo);
		
		initialize();
	}
	
	public Button(TheFigure parentFigure, String picture, String id, ModelNode data, boolean isInCombo, String info)
	{
		super(parentFigure);
		setId(id);
		setPicture(picture);
		setData(data);
		setInCombo(isInCombo);
		setInfo(info);
		
		initialize();
	}

	public Button(TheFigure parentFigure, String picture, String id, ModelNode data, boolean isInCombo, TheFigure link)
	{
		super(parentFigure);
		setId(id);
		setPicture(picture);
		setData(data);
		setInCombo(isInCombo);;
		setLink(link);
		
		initialize();
	}

	private void initialize()
	{
		GridLayout layout = new GridLayout();
		layout.verticalSpacing = layout.horizontalSpacing = 0;
		layout.marginHeight = layout.marginWidth = 0;
		setLayoutManager(layout);
		
		//setOpaque(false);
		
		Label label;
		if ( text == null )
		{
			label = new Label(new Image(display, MultiPageEditor.class.getResourceAsStream("/" + picture)));
		}
		else
		{
			label = new Label(text, new Image(display, MultiPageEditor.class.getResourceAsStream("/" + picture)));
		}
		
		add(label);
		GridData gd = new GridData(SWT.CENTER);
		setConstraint(label, gd);
	}
	
	public void setButtonClickHandler(ButtonClickHandler buttonClickHandler)
	{
		this.buttonClickHandler = buttonClickHandler;
	}
	
	@Override
	public void mousePressed(MouseEvent me)
	{
		if ( !isInCombo() ) getRootFigure().closeComboBox();
		if ( buttonClickHandler != null )
		{
			setMouseLocation(me.getLocation());
			buttonClickHandler.ButtonClicked(this);
		}
	}

	public String getId()
	{
		return id;
	}

	private void setId(String id)
	{
		this.id = id;
	}

	public ModelNode getData()
	{
		return data;
	}

	private void setData(ModelNode data)
	{
		this.data = data;
	}

	public String getPicture()
	{
		return picture;
	}

	private void setPicture(String picture)
	{
		this.picture = picture;
	}
	
	public boolean isInCombo()
	{
		return inCombo;
	}

	private void setInCombo(boolean inCombo)
	{
		this.inCombo = inCombo;
	}

	public Point getMouseLocation()
	{
		return mouseLocation;
	}

	private void setMouseLocation(Point mouseLocation)
	{
		this.mouseLocation = mouseLocation;
	}

	public TheFigure getLink()
	{
		return link;
	}

	private void setLink(TheFigure link)
	{
		this.link = link;
	}

	public String getText()
	{
		return text;
	}

	private void setText(String text)
	{
		this.text = text;
	}

	public String getInfo()
	{
		return info;
	}

	private void setInfo(String info)
	{
		this.info = info;
	}
}
