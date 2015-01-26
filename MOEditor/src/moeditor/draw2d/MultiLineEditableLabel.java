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

import org.eclipse.draw2d.ChangeListener;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.swt.graphics.Color;

public class MultiLineEditableLabel extends Figure
{
	
	//data
	private ArrayList<MultiLineOneLine> lineLabelList;
	
	//displaying
	private Color defaultBGColor;
	private Color bgColor;
	private Color selColor;
	
	//writing and selecting
	private int cursorX_symbol;
	private int cursorY_line;
	private int selectionStartX_symbol;
	private int selectionStartY_line;
	
	private boolean editing;
	
	private MultiLineEditableLabelPrinter printer = null;
	private ModelNode data = null;
	private String attribute = null;
	private boolean restricted = false;
	private ArrayList<TheFigure> allowedFigures = new ArrayList<TheFigure>();
	private TheFigure parent;
	private MultiLineLabelHandler handler;
	
	public MultiLineEditableLabel(int minW, int minH, Color bgColor, Color selColor, String someText, TheFigure parentFigure, boolean restricted)
	{
		this.defaultBGColor = bgColor;
		this.bgColor = bgColor;
		this.selColor = selColor;
		this.initializeEmpty(minW, minH);
		setRestricted(restricted);
		setParentFigure(parentFigure);
		this.pasteStringToCursor(someText);
		this.setInactive(); //constructor must end on this line
	}
	
	private void initializeEmpty(int minW, int minH)
	{
		setRequestFocusEnabled(true);
		setFocusTraversable(true);
		setOpaque(true);
		
		//Handler construction
		setHandler(new MultiLineLabelHandler(this));
		
		ToolbarLayout layout = new ToolbarLayout();
		layout.setHorizontal(false);
		setLayoutManager(layout);
		layout.setSpacing(0);
		
		this.minSize = new Dimension(minW, minH);
		this.lineLabelList = new ArrayList<MultiLineOneLine>();
		
		//MLEL must have at least one line (even if empty)
		this.lineLabelList.add(new MultiLineOneLine(bgColor, selColor));
		this.add(lineLabelList.get(0));
		
		//cursor initial values (must exist)
		this.cursorX_symbol = 0;
		this.cursorY_line = 0;
		this.selectionStartX_symbol = 0;
		this.selectionStartY_line = 0;
		
		this.setInactive();
	}
	
	void addChangeListener(MultiLineLabelHandler handler)
	{
		addListener(ChangeListener.class, handler);
	}
	
	/**
	 * Change state to inactive... all actions can still be preformed, but cursor is set to beginning and made invisible.
	 */
	public void setInactive()
	{
		changeCursorPosition(0, 0, false);//force default text cursor position
		lineLabelList.get(0).thisLineSetCursor(0, false);//hide text cursor
		editing = false;
	}
	
	public void setInactiveAndWrite()
	{
		setInactive();
		resetBGColor();
		if ( getPrinter() != null )
		{
			getPrinter().print(this, getContents());
		}
	}
	
	/**
	 * Change state to active. Making cursor visible and placing it to specified coordinates.
	 * 
	 * @param mouseXY Point with screen coordinates where to set text cursor (for example derived from mouse position)
	 */
	public void setActive(Point mouseXY)
	{
		changeCursorByMouse(mouseXY, false);//set cursor to the place clicked and display it
		editing = true;
	}
	
	public void setActive()
	{
		changeCursorToEndOfNote(false);
		editing = true;
		setBGColor(ColorConstants.lightGray);
	}
	
	/**
	 * 
	 * @return current state of this MultiLineEditableLabel
	 */
	public boolean isActive()
	{
		return editing;
	}
	
	/**
	 * Sets a new position for the cursor, if specified a selection to the last point of cursor will be made
	 * 
	 * @param newCursorX
	 * @param newCursorY
	 * @param selection true if this is not just a new location but a selection
	 */
	private void changeCursorPosition(int newCursorX, int newCursorY, boolean selection)
	{
		this.cursorX_symbol = newCursorX;
		this.cursorY_line = newCursorY;
		if(!selection)
		{
			this.selectionStartX_symbol = newCursorX;
			this.selectionStartY_line = newCursorY;
		}
		reapplyCursorPosition();
	}
	
	public void resetBGColor()
	{
		this.setBGColor(defaultBGColor);
	}
	
	public void setBGColor(Color bgColor)
	{
		this.bgColor = bgColor;
		for(int i = 0; i < lineLabelList.size(); i++)
		{
			lineLabelList.get(i).setBGColor(this.bgColor);
		}
	}
	public void setSelColor(Color selColor)
	{
		this.selColor = selColor;
		for(int i = 0; i < lineLabelList.size(); i++)
		{
			lineLabelList.get(i).setSelColor(this.selColor);
		}
	}
	
	/**
	 * Jump to beginning of line
	 * @param selection
	 */
	public void changeCursorLineStart(boolean selection)
	{
		int charIndex = 0;
		int lineIndex = cursorY_line;
		changeCursorPosition(charIndex, lineIndex, selection);
	}
	
	/**
	 * Jump to end of line
	 * @param selection
	 */
	public void changeCursorLineEnd(boolean selection)
	{
		int lineIndex = cursorY_line;
		int charIndex = lineLabelList.get(lineIndex).getLineText().length();
		changeCursorPosition(charIndex, lineIndex, selection);
	}
	
	/**
	 * End of Note
	 * @param selection
	 */
	public void changeCursorToEndOfNote(boolean selection)
	{
		int lineIndex = lineLabelList.size()-1;
		int charIndex = lineLabelList.get(lineIndex).getLineText().length();
		changeCursorPosition(charIndex, lineIndex, selection);
	}
	
	/**
	 * Beginning of Note
	 * @param selection
	 */
	public void changeCursorToBeginningOfNote(boolean selection)
	{
		int lineIndex = 0;
		int charIndex = 0;
		changeCursorPosition(charIndex, lineIndex, selection);
	}
	
	/**
	 * Get and apply new cursor position (or selection) based on direction and current state
	 * 
	 * @param direction 1-UP; 2-DOWN; 3-LEFT; 4-RIGHT; else void
	 * @param selection pass true if this is involved in creating a selection, false if just a cursor move
	 */
	public void changeCursorByArrow(int direction, boolean selection)
	{
		int charIndex = cursorX_symbol;
		int lineIndex = cursorY_line;
		//determine new current location in case of unexpected shift
		if(!selection)
		{
			charIndex = lineLabelList.get(lineIndex).undoSelection();
		}
		if(direction == 1)
		{			// UP ARROW
			if(lineIndex > 0)
			{
				charIndex = lineLabelList.get(lineIndex).convertCursorXToMouseX(charIndex);
				lineIndex--;
				charIndex = lineLabelList.get(lineIndex).convertMouseXToCursorX(charIndex);
			}
		}else if(direction == 2)
		{	// DOWN-ARROW
			if(lineIndex < lineLabelList.size()-1)
			{
				charIndex = lineLabelList.get(lineIndex).convertCursorXToMouseX(charIndex);
				lineIndex++;
				charIndex = lineLabelList.get(lineIndex).convertMouseXToCursorX(charIndex);
			}
		}else if(direction == 3)
		{	// LEFT-ARROW
			if(charIndex > 0)
			{
				charIndex--;
			}else
			{
				if(lineIndex > 0)
				{//beginning of line
					lineIndex--;
					charIndex = lineLabelList.get(lineIndex).getLineText().length();
				}
			}
		}else if(direction == 4)
		{	// RIGHT-ARROW
			if(charIndex < lineLabelList.get(lineIndex).getLineText().length())
			{
				charIndex++;
			}else
			{
				if(lineIndex < lineLabelList.size()-1)
				{//end of line
					charIndex = 0;
					lineIndex++;
				}
			}
		}else
		{
			//invalid 'direction' argument
		}
		changeCursorPosition(charIndex, lineIndex, selection);
	}
	
	/**
	 * Sets a new position for the cursor (based on mouse location), if specified a selection to the last point of cursor will be made.
	 * 
	 * @param mouseXY a Point giving the mouse location
	 * @param selection selection true if this is not just a new location but a selection
	 */
	public void changeCursorByMouse(Point mouseXY, boolean selection)
	{
		int lineIndex = converMouseYToLineNumberY(mouseXY.y());
		int charIndex = lineLabelList.get(lineIndex).convertMouseXToCursorX(mouseXY.x());
		changeCursorPosition(charIndex, lineIndex, selection);
	}
	
	/**
	 * Convert a coordinateY from Mouse into line number;
	 * 
	 * @param mouseY mouse coordinateY
	 * @return integer form line number
	 */
	private int converMouseYToLineNumberY(int mouseY)
	{
		int lineIndex = 0; //zero line always exists (default value)
		for(int i=lineLabelList.size()-1; i >= 0; i--)
		{
			if(lineLabelList.get(i).getLocation().y() < mouseY)
			{
				lineIndex = i;
				break;
			}
		}
		return(lineIndex);
	}
	
	/**
	 * Take current saved cursor position (or selection) and ensure it is applied properly
	 */
	private void reapplyCursorPosition()
	{
		if(selectionStartY_line == cursorY_line)
		{
			if(selectionStartX_symbol == cursorX_symbol)
			{//simple cursor (no selection)
				placeCursor();
			}else
			{//a selection within one line
				selectInSingleLine();
			}
		}else
		{//multiple-line selection
			selectInMultipleLines();
		}
	}
	
	/**
	 * Position a cursor in proper place if no text is selected. (cancel selection)
	 */
	private void placeCursor()
	{
		//cancel selection
		selectionStartX_symbol = cursorX_symbol;
		selectionStartY_line = cursorY_line;
		//place the cursor
		for(int lineIndex = 0; lineIndex < lineLabelList.size(); lineIndex++)
		{
			if(lineIndex == cursorY_line)
			{
				lineLabelList.get(lineIndex).thisLineSetCursor(cursorX_symbol, true);
			}else
			{
				lineLabelList.get(lineIndex).thisLineSetCursor(0, false);
			}
		}
	}
	
	/**
	 * Make a selection depending on the cursor if a text within a single line is selected.
	 */
	private void selectInSingleLine()
	{
		for(int lineIndex = 0; lineIndex < lineLabelList.size(); lineIndex++)
		{
			if(lineIndex == selectionStartY_line)
			{
				lineLabelList.get(lineIndex).selectLinePart(selectionStartX_symbol, cursorX_symbol);
			}else
			{
				lineLabelList.get(lineIndex).thisLineSetCursor(0, false);
			}
		}
	}
	
	/**
	 * Make a selection depending on the cursor if a text within a multiple lines is selected.
	 */
	private void selectInMultipleLines()
	{
		//determine first and last coordinates in selection (sorted)
		int startLineIndex;
		int startCharIndex;
		int stopLineIndex;
		int stopCharIndex;
		if(selectionStartY_line < cursorY_line)
		{
			startLineIndex = selectionStartY_line;
			startCharIndex = selectionStartX_symbol;
			stopLineIndex = cursorY_line;
			stopCharIndex = cursorX_symbol;
		}else
		{
			startLineIndex = cursorY_line;
			startCharIndex = cursorX_symbol;
			stopLineIndex = selectionStartY_line;
			stopCharIndex = selectionStartX_symbol;
		}
		//apply correct selection to all lines
		for(int lineIndex = 0; lineIndex < lineLabelList.size(); lineIndex++)
		{
			if(lineIndex == startLineIndex)
			{
				lineLabelList.get(lineIndex).selectLineFrom(startCharIndex, startLineIndex == cursorY_line);
			}else if(lineIndex == stopLineIndex)
			{
				lineLabelList.get(lineIndex).selectLineUntil(stopCharIndex, stopLineIndex == cursorY_line);
			}else if(lineIndex > startLineIndex && lineIndex < stopLineIndex)
			{
				lineLabelList.get(lineIndex).selectLineAll();
			}else
			{
				lineLabelList.get(lineIndex).thisLineSetCursor(0, false);
			}
		}
	}
	
	/**
	 * select all text
	 */
	public void selectAll()
	{
		changeCursorToBeginningOfNote(false);
		changeCursorToEndOfNote(true);
	}
	
	/**
	 * select the line that is in contact with the target coordinates
	 * @param mouseXY
	 */
	public void selectLine(Point mouseXY)
	{
		changeCursorByMouse(mouseXY, false);
		int newCursorX = lineLabelList.get(cursorY_line).selectLine();
		changeCursorPosition(0, cursorY_line, false);
		changeCursorPosition(newCursorX, cursorY_line, true);
	}
	
	/**
	 * select the word that is in contact with the target coordinates
	 * @param mouseXY
	 */
	public void selectTargetWord(Point mouseXY)
	{
		changeCursorByMouse(mouseXY, false);
		int[] selStartStop = lineLabelList.get(cursorY_line).selectWord(cursorX_symbol);
		changeCursorPosition(selStartStop[0], cursorY_line, false);
		changeCursorPosition(selStartStop[1], cursorY_line, true);
	}
	
	/**
	 * Add a new line at cursor location managing text properly.
	 * 
	 */
	public void addLine()
	{
		//delete replaced selection
		deleteSelection();
		//create and insert empty line
		int insertindex = this.cursorY_line + 1;
		if(insertindex < this.lineLabelList.size())
		{
			lineLabelList.add(insertindex, new MultiLineOneLine(bgColor, selColor));
			add(lineLabelList.get(insertindex),insertindex);
		}else
		{
			lineLabelList.add(new MultiLineOneLine(bgColor, selColor));
			add(lineLabelList.get(insertindex));
		}
		//take the text after cursor in current line and move it into the new one
		String remainingText = lineLabelList.get(cursorY_line).cutTextAfterCursor();
		lineLabelList.get(insertindex).appendTextToEnd(remainingText);
		//set the new cursor position at the beginning of the new line
		changeCursorPosition(0, insertindex, false);
	}
	
	/**
	 * Standard delete-like effect on the labels text.
	 * 
	 */
	public void deleteCharAtCursorRight()
	{
		if(cursorX_symbol == selectionStartX_symbol && cursorY_line == selectionStartY_line)
		{//nothing selected
			changeCursorByArrow(4, true);
			deleteSelection();
		}else
		{
			deleteSelection();
		}
	}
	
	/**
	 * Standard backspace-like effect on the labels text.
	 * 
	 */
	public void deleteCharAtCursorLeft()
	{
		if(cursorX_symbol == selectionStartX_symbol && cursorY_line == selectionStartY_line)
		{//nothing selected
			changeCursorByArrow(3, true);
			deleteSelection();
		}else
		{
			deleteSelection();
		}
	}
	
	/**
	 * Remove all selected text including the lines.
	 * This includes handling cursor position properly.
	 */
	public void deleteSelection()
	{
		String appendString = "";
		int newCursorY = cursorY_line;
		int newCursorX = cursorX_symbol;
		//remove selected data in lines and determine new cursor location
		ArrayList<MultiLineOneLine> newLineList = new ArrayList<MultiLineOneLine>();
		for(int i = 0; i < lineLabelList.size(); i++)
		{
			int clearingFlag = lineLabelList.get(i).clearSelection();
			if(clearingFlag >= 0)
			{//get new position
				newCursorY = i;
				newCursorX = clearingFlag;
			}
			//create updated line-list
			if(clearingFlag != -1)
			{
				newLineList.add(lineLabelList.get(i));
			}else
			{//collect not-selected text and remove line
				remove(lineLabelList.get(i));
				appendString += lineLabelList.get(i).cutTextAfterCursor();
			}
		}
		//replace out-dated line-list with a new one
		this.lineLabelList = newLineList;
		//add data appendix of last line in selection to the remnants of first line in selection
		lineLabelList.get(newCursorY).appendTextToEnd(appendString);
		//finally: set cursor to proper position
		changeCursorPosition(newCursorX, newCursorY, false);
	}
	
	/**
	 * Paste in selections place a given string and set text cursor to it's end.
	 * Use to write letters (faster than {@code pasteStringToCursor(String someText)} ).
	 * Must NOT contain any newline characters.
	 * 
	 * @param someText text to paste (String... !No newline characters!)
	 */
	public void addOneLineTextToCursor(String someText)
	{
		deleteSelection();
		int newCursorX = lineLabelList.get(cursorY_line).writeOneLineString(someText, true);
		changeCursorPosition(newCursorX, cursorY_line, false);
	}
	
	/**
	 * Paste in selections place any given string and set text cursor to it's end.
	 * 
	 * @param someText text to paste (String)
	 */
	public void pasteStringToCursor(String someText)
	{
		//clear selection
		deleteSelection();
		if ( isRestricted() )
		{
			someText = someText.replace('\n', ' ');
			someText = someText.replace('\r', ' ');
		}
		//paste string line-by-line (cursor is already handled by used methods)
		String[] tempLineStrArray = someText.split("\n");
		for(int i = 0; i < tempLineStrArray.length; i++)
		{
			if(i == 0)
			{
				if(someText.startsWith("\n"))
				{
					addLine();
				}
				addOneLineTextToCursor(tempLineStrArray[i]);
			}else if(i >= tempLineStrArray.length - 1)
			{
				addLine();
				addOneLineTextToCursor(tempLineStrArray[i]);
				if(someText.endsWith("\n"))
				{
					addLine();
				}
			}else
			{
				addLine();
				addOneLineTextToCursor(tempLineStrArray[i]);
			}
		}
	}
	
	/**
	 * Acquire String with all selected text.
	 * 
	 * @return String, sum of selected text from all lines with active selection.
	 */
	public String getSelected()
	{ 
		String resultString = "";
		for(int i = 0; i < lineLabelList.size(); i++)
		{
			if(lineLabelList.get(i).hasActiveSelection())
			{
				resultString += lineLabelList.get(i).getLineSelection();
				if(i < lineLabelList.size()-1)
				{
					resultString += "\n";
				}
			}
		}
		return resultString;
	}
	
	/**
	 * Acquire String with all text in this label.
	 * 
	 * @return String, sum of all text from all lines in this object.
	 */
	public String getContents()
	{ 
		String resultString = "";
		for(int i = 0; i < lineLabelList.size(); i++)
		{
			resultString += lineLabelList.get(i).getLineText();
			if(i < lineLabelList.size()-1)
			{
				resultString += "\n";
			}
		}
		return resultString;
	}

	public MultiLineEditableLabelPrinter getPrinter() {
		return printer;
	}

	public void setPrinter(MultiLineEditableLabelPrinter printer) {
		this.printer = printer;
	}

	public ModelNode getData() {
		return data;
	}

	public void setData(ModelNode data) {
		this.data = data;
	}

	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	public boolean isRestricted()
	{
		return restricted;
	}

	public void setRestricted(boolean restricted)
	{
		this.restricted = restricted;
	}

	public ArrayList<TheFigure> getAllowedFigures()
	{
		return allowedFigures;
	}

	public void setAllowedFigures(ArrayList<TheFigure> allowedFigures)
	{
		this.allowedFigures = allowedFigures;
	}

	public TheFigure getParentFigure()
	{
		return parent;
	}

	public void setParentFigure(TheFigure parent)
	{
		this.parent = parent;
	}

	public MultiLineLabelHandler getHandler()
	{
		return handler;
	}

	private void setHandler(MultiLineLabelHandler handler)
	{
		this.handler = handler;
	}
}