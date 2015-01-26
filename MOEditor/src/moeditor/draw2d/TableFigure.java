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

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;

public class TableFigure extends TheFigure
{
	private Color headingColor = ColorConstants.lightBlue;
	private Color editableColor = ColorConstants.white;
	
	private Figure vertical;
	private Figure horizontal;
	
	public TableFigure(TheFigure parentFigure, int numberOfColumns)
	{
		super(parentFigure);
		
		vertical = new Figure();
		horizontal = new Figure();
		
		ToolbarLayout horLayout = new ToolbarLayout(true);
		ToolbarLayout verLayout = new ToolbarLayout(false);
		
		GridLayout layout = new GridLayout(numberOfColumns, false);
		layout.verticalSpacing = layout.horizontalSpacing = 0;
		layout.marginHeight = layout.marginWidth = 0;
		horizontal.setLayoutManager(layout);
		
		vertical.setLayoutManager(horLayout);
		setLayoutManager(verLayout);
		
		vertical.add(horizontal);
		super.add(vertical);
	}
	
	public void add(Figure figure)
	{
		horizontal.add(figure);
	}
	
	public void add(Figure figure, int index)
	{
		horizontal.add(figure, index);
	}
	
	public void removeAll()
	{
		horizontal.removeAll();
	}
	
	public void setConstraint(Figure figure, Object constraint)
	{
		horizontal.setConstraint(figure, constraint);
	}
	
	public void addCell(String text)
	{
		addCell(text, getEditableColor(), 1, 1);
	}
	
	public void addCell(String text, Color color)
	{
		addCell(text, color, 1, 1);
	}
	
	public void addCell(String text, int horizontalSpan)
	{
		addCell(text, getEditableColor(), horizontalSpan, 1);
	}
	
	public void addHeadingCell(String text)
	{
		addCell(text, getHeadingColor(), 1, 1);
	}
	
	public void addHeadingCell(String text, int horizontalSpan, int verticalSpan)
	{
		addCell(text, getHeadingColor(), horizontalSpan, verticalSpan);
	}
	
	protected void addCell(String text, Color color, int horizontalSpan, int verticalSpan)
	{
		Label label = new Label(text);
		/*Figure figure = new Figure();
		figure.setOpaque(true);
		figure.setBackgroundColor(color);
		figure.setBorder(new LineBorder(ColorConstants.black,1));
		GridLayout layout = new GridLayout();
		figure.setLayoutManager(layout);
		figure.add(label);
		GridData gd = new GridData(SWT.CENTER);
		figure.setConstraint(label, gd);
		add(figure);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.horizontalSpan = horizontalSpan;
		setConstraint(figure, gridData);*/
		addCell(label, color, horizontalSpan, verticalSpan);
	}

	protected void addCell(TheFigure theFigure)
	{
		addCell(theFigure, getEditableColor(), 1, 1);
	}
	
	protected void addCell(TheFigure theFigure, Color color)
	{
		addCell(theFigure, color, 1, 1);
	}
	
	protected void addCell(TheFigure theFigure, int horizontalSpan)
	{
		addCell(theFigure, getEditableColor(), horizontalSpan, 1);
	}
	
	protected void addCell(TheFigure theFigure, Color color, int horizontalSpan, int verticalSpan)
	{
		Figure figure = new Figure();
		figure.setOpaque(true);
		figure.setBackgroundColor(color);
		figure.setBorder(new LineBorder(ColorConstants.black,1));
		GridLayout layout = new GridLayout();
		figure.setLayoutManager(layout);
		theFigure.setColor(color);
		figure.add(theFigure);
		GridData gd = new GridData(SWT.CENTER);
		figure.setConstraint(theFigure, gd);
		add(figure);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.horizontalSpan = horizontalSpan;
		gridData.verticalSpan = verticalSpan;
		setConstraint(figure, gridData);
	}

	protected void addCell(Label label)
	{
		addCell(label, getEditableColor(), 1, 1);
	}
	
	protected void addCell(Label label, Color color)
	{
		addCell(label, color, 1, 1);
	}
	
	protected void addCell(Label label, Color color, int horizontalSpan, int verticalSpan)
	{
		Figure figure = new Figure();
		figure.setOpaque(true);
		figure.setBackgroundColor(color);
		figure.setBorder(new LineBorder(ColorConstants.black,1));
		GridLayout layout = new GridLayout();
		figure.setLayoutManager(layout);
		figure.add(label);
		GridData gd = new GridData(SWT.CENTER);
		figure.setConstraint(label, gd);
		add(figure);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.horizontalSpan = horizontalSpan;
		gridData.verticalSpan = verticalSpan;
		setConstraint(figure, gridData);
	}	
	
	public void addLabel(EditableLabel label)
	{
		addLabel(label, getEditableColor(), 1);
	}
			
	protected void addLabel(EditableLabel label, Color color)
	{
		addLabel(label, color, 1);
	}
	
	protected void addLabel(EditableLabel label, int horizontalSpan)
	{
		addLabel(label, getEditableColor(), horizontalSpan);
	}
	
	protected void addLabel(EditableLabel label, Color color, int horizontalSpan)
	{
		TheFigure figure = new TheFigure();
		figure.setLabel(label);
		figure.setOpaque(true);
		figure.setBackgroundColor(color);
		figure.setBorder(new LineBorder(ColorConstants.black,1));
		GridLayout layout = new GridLayout();
		figure.setLayoutManager(layout);
		figure.add(label);
		GridData gd = new GridData(SWT.CENTER);
		figure.setConstraint(label, gd);
		add(figure);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.horizontalSpan = horizontalSpan;
		setConstraint(figure, gridData);
	}
	
	/*protected void addLabel(MultiLineEditableLabel label, Color color, int horizontalSpan)
	{
		TheFigure figure = new TheFigure();
		figure.setLabel(label);
		figure.setOpaque(true);
		figure.setBackgroundColor(color);
		figure.setBorder(new LineBorder(ColorConstants.black,1));
		GridLayout layout = new GridLayout();
		figure.setLayoutManager(layout);
		//label.setColor(color);
		figure.add(label);
		GridData gd = new GridData(SWT.CENTER);
		figure.setConstraint(label, gd);
		add(figure);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.horizontalSpan = horizontalSpan;
		setConstraint(figure, gridData);
	}*/

	protected void addLabelWithButtonLeft(EditableLabel label, Color color, int horizontalSpan, Button button)
	{
		button.setNonFocusColor(color);
		
		TheFigure figure = new TheFigure();
		figure.setLabel(label);
		figure.setOpaque(true);
		figure.setBackgroundColor(color);
		figure.setBorder(new LineBorder(ColorConstants.black,1));
		GridLayout layout = new GridLayout(2, false);
		figure.setLayoutManager(layout);
		//label.setColor(color);
		
		figure.add(button);
		GridData gd1 = new GridData(SWT.CENTER);
		figure.setConstraint(button, gd1);
		
		figure.add(label);
		GridData gd = new GridData(SWT.CENTER);
		figure.setConstraint(label, gd);
		
		add(figure);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.horizontalSpan = horizontalSpan;
		setConstraint(figure, gridData);
	}
	
	protected void addLabelWithButtonRight(EditableLabel label, Color color, int horizontalSpan, Button button)
	{
		button.setNonFocusColor(color);
		
		TheFigure figure = new TheFigure();
		figure.setLabel(label);
		figure.setOpaque(true);
		figure.setBackgroundColor(color);
		figure.setBorder(new LineBorder(ColorConstants.black,1));
		GridLayout layout = new GridLayout(2, false);
		figure.setLayoutManager(layout);
		//label.setColor(color);
		
		figure.add(label);
		GridData gd = new GridData(SWT.CENTER);
		figure.setConstraint(label, gd);
		
		figure.add(button);
		GridData gd1 = new GridData(SWT.CENTER);
		figure.setConstraint(button, gd1);
		
		add(figure);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.horizontalSpan = horizontalSpan;
		setConstraint(figure, gridData);
	}
	
	protected void addLabelWithTwoButtons(EditableLabel label, Color color, int horizontalSpan, Button button1, Button button2)
	{
		button1.setNonFocusColor(color);
		button2.setNonFocusColor(color);
		
		TheFigure figure = new TheFigure();
		figure.setLabel(label);
		figure.setOpaque(true);
		figure.setBackgroundColor(color);
		figure.setBorder(new LineBorder(ColorConstants.black,1));
		GridLayout layout = new GridLayout(3, false);
		figure.setLayoutManager(layout);
		//label.setColor(color);
		
		figure.add(button1);
		GridData gd1 = new GridData(SWT.CENTER);
		figure.setConstraint(button1, gd1);
		
		figure.add(label);
		GridData gd = new GridData(SWT.CENTER);
		figure.setConstraint(label, gd);
		
		figure.add(button2);
		GridData gd2 = new GridData(SWT.CENTER);
		figure.setConstraint(button2, gd2);
		
		add(figure);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.horizontalSpan = horizontalSpan;
		setConstraint(figure, gridData);
	}
	
	public void addCellWithButtonLeft(Color color, Button button, String text)
	{
		addCellWithButtonLeft(color, button, text, 1);
	}
	
	public void addCellWithButtonLeft(Color color, Button button, String text, int horizontalSpan)
	{
		button.setNonFocusColor(color);
		
		Figure figure = new Figure();
		figure.setOpaque(true);
		figure.setBackgroundColor(color);
		figure.setBorder(new LineBorder(ColorConstants.black,1));
		GridLayout layout = new GridLayout(2, false);
		figure.setLayoutManager(layout);
		
		figure.add(button);
		GridData gd2 = new GridData(SWT.CENTER);
		figure.setConstraint(button, gd2);
		
		Label label = new Label(text);
		figure.add(label);
		GridData gd1 = new GridData(SWT.CENTER);
		figure.setConstraint(label, gd1);
		
		add(figure);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, horizontalSpan, 1);
		setConstraint(figure, gridData);
	}
	
	public void addCellWithButtonBottom(Color color, Button button, String text, int horizontalSpan, int verticalSpan)
	{
		button.setNonFocusColor(color);
		
		Figure figure = new Figure();
		figure.setOpaque(true);
		figure.setBackgroundColor(color);
		figure.setBorder(new LineBorder(ColorConstants.black,1));
		GridLayout layout = new GridLayout(1, false);
		figure.setLayoutManager(layout);
		
		Label label = new Label(text);
		figure.add(label);
		GridData gd1 = new GridData(SWT.CENTER);
		figure.setConstraint(label, gd1);
		
		figure.add(button);
		GridData gd2 = new GridData(SWT.CENTER);
		figure.setConstraint(button, gd2);
		
		add(figure);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, horizontalSpan, verticalSpan);
		setConstraint(figure, gridData);
	}
	
	public void addCellWithButtonRight(Color color, Button button, String text)
	{
		addCellWithButtonRight(color, button, text, 1);
	}
	
	public void addCellWithButtonRight(Color color, Button button, String text, int horizontalSpan)
	{
		button.setNonFocusColor(color);
		
		Figure figure = new Figure();
		figure.setOpaque(true);
		figure.setBackgroundColor(color);
		figure.setBorder(new LineBorder(ColorConstants.black,1));
		GridLayout layout = new GridLayout(2, false);
		figure.setLayoutManager(layout);
		
		Label label = new Label(text);
		figure.add(label);
		GridData gd1 = new GridData(SWT.CENTER);
		figure.setConstraint(label, gd1);
		
		figure.add(button);
		GridData gd2 = new GridData(SWT.CENTER);
		figure.setConstraint(button, gd2);
		
		add(figure);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, horizontalSpan, 1);
		setConstraint(figure, gridData);
	}
	
	protected void addCellWithTwoButtons(Label label, Color color, int horizontalSpan, Button button1, Button button2)
	{
		button1.setNonFocusColor(color);
		button2.setNonFocusColor(color);
		
		Figure figure = new Figure();
		figure.setOpaque(true);
		figure.setBackgroundColor(color);
		figure.setBorder(new LineBorder(ColorConstants.black,1));
		GridLayout layout = new GridLayout(3, false);
		figure.setLayoutManager(layout);
		
		figure.add(button1);
		GridData gd1 = new GridData(SWT.CENTER);
		figure.setConstraint(button1, gd1);
		
		figure.add(label);
		GridData gd = new GridData(SWT.CENTER);
		figure.setConstraint(label, gd);
		
		figure.add(button2);
		GridData gd2 = new GridData(SWT.CENTER);
		figure.setConstraint(button2, gd2);
		
		add(figure);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.horizontalSpan = horizontalSpan;
		setConstraint(figure, gridData);
	}

	public Color getHeadingColor()
	{
		return headingColor;
	}

	public void setHeadingColor(Color color)
	{
		this.headingColor = color;
	}

	public Color getEditableColor() {
		return editableColor;
	}

	public void setEditableColor(Color color) {
		this.editableColor = color;
	}
	
	@Override
	public void mousePressed(MouseEvent me)
	{
	}
	
}
