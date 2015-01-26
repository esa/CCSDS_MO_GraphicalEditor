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

import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Point;

public class ComboBox extends TheFigure
{
	private Label label;
	private OpenedComboBox openedComboBox;
	private ModelNode data = null;
	private String attribute = null;
	private int gridWidth = 1;
	private ArrayList<TheFigure> protectedCombos = new ArrayList<TheFigure>();
	private String id = null;
	
	private ComboBoxPrinter printer = null;
	
	public void setPrinter(ComboBoxPrinter printer)
	{
		this.printer = printer;
	}
	
	public ComboBox(TheFigure parentFigure)
	{
		super(parentFigure);
		initialize();
	}
	
	public ComboBox(TheFigure parentFigure, String id)
	{
		super(parentFigure);
		setId(id);
		initialize();
	}
	
	public ComboBox(TheFigure parentFigure, ModelNode data)
	{
		super(parentFigure);
		initialize();
		setData(data);
	}
	
	public ComboBox(TheFigure parentFigure, ModelNode data, String attribute)
	{
		super(parentFigure);
		initialize();
		setData(data);
		setAttribute(attribute);
	}
	
	public ComboBox(TheFigure parentFigure, ModelNode data, String attribute, ArrayList<TheFigure> protectedCombos)
	{
		super(parentFigure);
		initialize();
		setData(data);
		setAttribute(attribute);
		this.protectedCombos = protectedCombos;
	}
	
	public ComboBox(TheFigure parentFigure, int gridWidth)
	{
		super(parentFigure);
		setGridWidth(gridWidth);
		initialize();
	}
	
	public ComboBox(TheFigure parentFigure, ModelNode data, String attribute, int gridWidth)
	{
		super(parentFigure);
		setData(data);
		setAttribute(attribute);
		setGridWidth(gridWidth);
		initialize();
	}
	
	private void setGridWidth(int gridWidth)
	{
		this.gridWidth = gridWidth;
	}

	private void initialize()
	{
		label = new Label();
		openedComboBox = new OpenedComboBox(this, gridWidth);
		
		ToolbarLayout layout = new ToolbarLayout();
		layout.setHorizontal(true);
		setLayoutManager(layout);
		layout.setSpacing(0);
		add(label);
	}
	
	@Override
	public void mouseDoubleClicked(MouseEvent me)
	{
		Point location = me.getLocation();
		//location.x -= 10;
		//location.y -= 10;
		getRootFigure().openComboBox(location, openedComboBox);
	}
	
	public String getText()
	{
		return label.getText();
	}

	public void setText(String text)
	{
		label.setText(text);
	}
	
	public String getTextByValue(String value)
	{
		return openedComboBox.getTextByValue(value);
	}

	public void selected(String text)
	{
		setText(text);
		if ( printer != null )
		{
			printer.print(this, text);
		}
		getRootFigure().closeComboBoxIfNotThese(protectedCombos);
	}
	
	public void addComboBoxEntry(String value, String text)
	{
		openedComboBox.addComboBoxEntry(value, text);
	}

	public ModelNode getData()
	{
		return data;
	}

	private void setData(ModelNode data)
	{
		this.data = data;
	}

	public String getAttribute()
	{
		return attribute;
	}

	private void setAttribute(String attribute)
	{
		this.attribute = attribute;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}
	
}
