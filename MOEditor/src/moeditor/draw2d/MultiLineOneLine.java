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

import org.eclipse.draw2d.AbstractBorder;
import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.TextUtilities;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;

/**
 * Representation of a single line specifically intended to be used by MultiLineEditableLabel.
 * Do NOT use any functions in this class unless fully considering their effect.
 *
 */
public class MultiLineOneLine extends Figure
{
	
	// CLASS VARIABLES
	private Label label; // for the text to the left of the cursor
	private Label cursor; //for selected text if idle is invisible, different background color
	private Label rabel; //for the text to the right of the cursor (is empty if cursor not active)
	private Label dummyLastSymbol;
	//user manipulation flags
	private boolean selectionActive; //true if selection was made and this is involved
	private boolean newlineSelected; //true if \n character starting this line is selected
	
	
	// CONSTRUCTORS
	public MultiLineOneLine(Color bgColor, Color selColor)
	{
		this.initialize(bgColor, selColor);
	}
	
	
	// CLASS METHODS - miscellaneous, settings
	private void initialize (Color bgColor, Color selColor)
	{
		//setRequestFocusEnabled(false);
		//setFocusTraversable(false);
		setOpaque(true);
		
		newlineSelected = false;
		selectionActive = false;
		
		label = new Label();
		cursor = new Label();
		rabel = new Label();
		dummyLastSymbol= new Label();
		
		label.setBackgroundColor(bgColor);
		label.setText("");
		label.setOpaque(true);
		cursor.setBackgroundColor(selColor);
		cursor.setText("");
		cursor.setOpaque(true);
		rabel.setBackgroundColor(bgColor);
		rabel.setText("");
		rabel.setOpaque(true);
		dummyLastSymbol.setText(" ");
		
		
		ToolbarLayout layout = new ToolbarLayout();
		layout.setHorizontal(true);
		setLayoutManager(layout);
		layout.setSpacing(0);
		
		add(label);
		add(cursor);
		add(rabel);
		add(dummyLastSymbol);
	}
	
	/**
	 * Set text of this line to a single line String throwing away original value.
	 * Please note that this method manipulates cursor on this line and it's state should be always enforced after calling this.
	 * 
	 * @param text a String with new value, must fit into one line (it won't throw exception... it simply won't work like it's supposed to)
	 */
	public void setLinetext(String text)
	{
		label.setText("");
		cursor.setText("");
		rabel.setText(text);
	}
	
	/**
	 * Get font used by this line.
	 * 
	 * @return usedFont
	 */
	public Font getTextFont()
	{
		return (label.getFont());
	}
	
	/**
	 * Set background color
	 * 
	 * @param bgColor a Color object
	 */
	public void setBGColor(Color bgColor)
	{
		label.setBackgroundColor(bgColor);
		rabel.setBackgroundColor(bgColor);
	}
	
	/**
	 * Set color that marks the selected substring.
	 * 
	 * @param selColora Color object
	 */
	public void setSelColor(Color selColor)
	{
		cursor.setBackgroundColor(selColor);
	}
	
	
	// CLASS METHODS - text cursor handling
	/**
	 * Set if and how to display the cursor on this line.
	 * 
	 * @param cursorVisible cursor is made visible if true
	 * @param isLeft determines on which side of selection is the cursor being shown. (true = left)
	 */
	private void setCursorVisibility(boolean cursorVisible, boolean isLeft)
	{
		if(cursorVisible)
		{
			if(isLeft)
			{//cursor on the left side of the selection
				if(getLineText().equalsIgnoreCase(""))
				{
					cursor.setBorder(null);
					rabel.setBorder(null);
					dummyLastSymbol.setBorder((Border) new LeftCursorBorder());
				}else
				{
					dummyLastSymbol.setBorder(null);
					rabel.setBorder(null);
					cursor.setBorder((Border) new LeftCursorBorder());
				}
				
			}else
			{//cursor on the right side of selection
				cursor.setBorder(null);
				if(rabel.getText().equalsIgnoreCase(""))
				{
					rabel.setBorder(null);
					dummyLastSymbol.setBorder((Border) new LeftCursorBorder());
				}else
				{
					rabel.setBorder((Border) new LeftCursorBorder());
					dummyLastSymbol.setBorder(null);
				}
			}
		}else
		{
			//make cursor invisible
			cursor.setBorder(null);
			dummyLastSymbol.setBorder(null);
			rabel.setBorder(null);
		}
	}
	
	/**
	 * Place a visible cursor on target position if on this line, un-selects all selections.
	 * To be used by calling for every line in label or independently to make cursor invisible.
	 * 
	 * @param charindex position of the cursor, number of characters left of cursor
	 * @param onThisLine pass true if cursor is on this line else false (to make other cursors invisible)
	 */
	public void thisLineSetCursor(int charindex, boolean onThisLine)
	{
		undoSelection();
		if(onThisLine)
		{
			String lineText = getLineText();
			label.setText(lineText.substring(0, charindex));
			cursor.setText("");
			rabel.setText(lineText.substring(charindex, lineText.length()));
			setCursorVisibility(true, false);
		}else
		{
			setCursorVisibility(false, false);
			label.setText(getLineText());
			cursor.setText("");
			rabel.setText("");
		}
	}
	
	/**
	 * Calculate a position in characters for the text cursor based on mouse position on X axis.
	 * 
	 * @param mouseX integer representation of mouse position on X axis.
	 * @return X index of the cursor (as in number of characters left of cursor within line) - (integer) 
	 */
	public int convertMouseXToCursorX(int mouseX)
	{
		int availableWidth = mouseX - label.getLocation().x();
		if(availableWidth < 0)availableWidth = 0;
		int charactersLeftOfCursor;
		charactersLeftOfCursor = TextUtilities.INSTANCE.getLargestSubstringConfinedTo(getLineText(), getTextFont(), availableWidth);
		return(charactersLeftOfCursor);
	}
	
	/**
	 * Calculate a mouse X axis position derived from location of current text cursor (characterIndex).
	 * 
	 * @param cursorX index of the cursor (as in number of characters left of cursor within line) - (integer)
	 * @return integer representation of mouse position on X axis.
	 */
	public int convertCursorXToMouseX(int cursorX)
	{
		String lineText = getLineText();
		lineText = lineText.substring(0, cursorX);
		return(TextUtilities.INSTANCE.getStringExtents(lineText, getTextFont()).width() + label.getLocation().x()); 
	}
	
	/**
	 * Set cursor to a correct position within this line based on X location of mouse event.
	 * To be used for all lines within label setting the correct one and clearing cursor elsewhere.
	 * 
	 * @param mouseX integer representation of mouse position on X axis.
	 * @param onThisLine pass true if this is the line where the cursor is to be set.
	 * @return X index of the cursor (as in number of characters left of cursor within line) - (integer) 
	 */
	public int thisLineSetCursorMouse(int mouseX, boolean onThisLine)
	{
		int charindex = convertMouseXToCursorX(mouseX);
		thisLineSetCursor(charindex, onThisLine);
		return(charindex);
	}
		
	
	// CLASS METHODS - selection and text contents modification 
	/**
	 * 
	 * @return true if this line is included in the current selection
	 */
	public boolean hasActiveSelection()
	{
		return selectionActive;
	}
	
	/**
	 * Remove a portion of text to the right form the cursor position (selection) in this line and return it
	 * 
	 * @return
	 */
	public String cutTextAfterCursor()
	{
		String resultString = rabel.getText();
		rabel.setText("");
		return resultString;
	}
	
	/**
	 * Append text in argument to end of this line.
	 * 
	 * @param someText String to append that must fit in a single line (no \n characters)
	 */
	public void appendTextToEnd(String someText)
	{
		rabel.setText(rabel.getText() + someText);
	}
	
	/**
	 * 
	 * @return complete text of this line (String)
	 */
	public String getLineText()
	{
		return(label.getText() + cursor.getText() + rabel.getText());
	}
	
	/**
	 * 
	 * @return selected part of text on this line (String)
	 */
	public String getLineSelection()
	{
		return cursor.getText();
	}
	
	/**
	 * Use to select text within the first line of multiple-line-selection.
	 * !When cursor line changes {@code thisLineSetCursor(0, false)} should be called for all lines and selection reformed!
	 * 
	 * @param selectionStart the position of the text cursor where the selection starts.
	 * @param cursorOnLine pass 'true' if this is the line where cursor should end up (be shown at).
	 */
	public void selectLineFrom(int selectionStart, boolean cursorOnLine)
	{
		String lineText = getLineText();
		label.setText(lineText.substring(0, selectionStart));
		cursor.setText(lineText.substring(selectionStart, lineText.length()));
		rabel.setText("");
		newlineSelected = false;
		selectionActive = true;
		if(cursorOnLine)
		{
			setCursorVisibility(true, true);
		}else
		{
			setCursorVisibility(false, true);
		}
	}
	
	/**
	 * Use to select text within the last line of multiple-line-selection.
	 * !When cursor line changes {@code thisLineSetCursor(0, false)} should be called for all lines and selection reformed!
	 * 
	 * @param selectionEnd the position of the text cursor where the selection ends.
	 * @param cursorOnLine pass 'true' if this is the line where cursor should end up (be shown at).
	 */
	public void selectLineUntil(int selectionEnd, boolean cursorOnLine)
	{
		String lineText = getLineText();
		label.setText("");
		cursor.setText(lineText.substring(0, selectionEnd));
		rabel.setText(lineText.substring(selectionEnd, lineText.length()));
		newlineSelected = true;
		selectionActive = true;
		if(cursorOnLine)
		{
			setCursorVisibility(true, false);
		}else
		{
			setCursorVisibility(false, false);
		}
	}
	
	/**
	 * Use to select lines in the middle of multiple-line-selection.
	 * !When cursor line changes {@code thisLineSetCursor(0, false)} should be called for all lines and selection reformed!
	 */
	public void selectLineAll()
	{
		cursor.setText(getLineText());
		label.setText("");
		rabel.setText("");
		newlineSelected = true;
		selectionActive = true;
		setCursorVisibility(false, false);
	}
	
	/**
	 * Select text from this line determined by indexes given as arguments.
	 * {@code thisLineSetCursor(0, false)} should be called on all other lines before using this.
	 * 
	 * @param selectionStart the older index (where user started the selection) - char index...
	 * @param selectionEnd the latest known position of text cursor (current end of user selection) - char index...
	 */
	public void selectLinePart(int selectionStart, int selectionEnd)
	{
		if(selectionStart > selectionEnd)
		{//cursor is on the left side of selection
			setCursorVisibility(true, true);
			//switch argument values so that start comes first or are equal...
			int tempNumber = selectionStart;
			selectionStart = selectionEnd;
			selectionEnd = tempNumber;
		}else
		{//cursor is on the right side of the selection
			setCursorVisibility(true, false);
		}
		String lineText = getLineText();
		label.setText(lineText.substring(0, selectionStart));
		cursor.setText(lineText.substring(selectionStart, selectionEnd));
		rabel.setText(lineText.substring(selectionEnd, lineText.length()));
		newlineSelected = false;
		selectionActive = true;
	}
	
	/**
	 * Select the word charindex of cursor is in contact with and move cursor to it's end.
	 * {@code thisLineSetCursor(0, false)} should be called on other lines before doing this.
	 * 
	 * @param charindex current position of the text cursor
	 * @return new positions of the cursor
	 */
	public int[] selectWord(int charindex)
	{
		String lineText = getLineText();
		//get all chars left:
		int leftindex = charindex;
		while(leftindex-1 >= 0 && Character.isLetterOrDigit(lineText.charAt(leftindex-1)))
		{
			leftindex--;
		}
		//get all chars right:
		int rightindex = charindex;
		while(rightindex < lineText.length() && Character.isLetterOrDigit(lineText.charAt(rightindex)))
		{
			rightindex++;
		}
		this.selectLinePart(leftindex, rightindex);
		return (new int[]{leftindex, rightindex});
	}
	/**
	 * Select this entire line (but not the '\n' symbol)
	 * {@code thisLineSetCursor(0, false)} should be called on other lines before doing this.
	 * 
	 * @return new position of the cursor
	 */
	public int selectLine()
	{
		int leftindex = 0;
		int rightindex = getLineText().length();
		this.selectLinePart(leftindex, rightindex);
		return rightindex;
	}
	
	/**
	 * Un-select selected substring within this line.
	 * Setting of cursor visibility should follow if needed
	 * 
	 * @return new cursor position on this line (integer)
	 */
	public int undoSelection()
	{
		label.setText(label.getText() + cursor.getText());
		cursor.setText("");
		newlineSelected = false;
		selectionActive = false;
		return label.getText().length();
	}
	
	/**
	 * Remove all characters selected.
	 * To be called for every line with post-analysis of return values.
	 * Cursor location needs to be properly set afterwards based on the return-values-processing. 
	 * 
	 * @return An integer is returned with following meaning:
	 * 				(>=0)	--> a new position of cursor on this line, 
	 * 				(=-1)	--> This line is being concatenated to the previous one (to do manually...), 
	 * 				(=-2)	--> This line was not involved in the current selection (to be ignored...)
	 */
	public int clearSelection()
	{
		cursor.setText("");
		//return new cursor position, 
		//-1 is a delete line flag (remaining text must be transfered to line above)
		//-2 if selection is not active
		int newCursorX = -2;
		if(selectionActive)
		{	
			if(newlineSelected)
			{
				newCursorX = -1;
			}else
			{
				newCursorX = label.getText().length();
			}
		//else return -2... no active selection flag
		}
		//clear flags
		selectionActive = false;
		newlineSelected = false;
		
		return(newCursorX);
	}
	
	/**
	 * Paste the String from argument to a cursor location (from the left)
	 * To be called on every line involved in current selection... and post-process OR handle clearing selection beforehand...
	 * 
	 * @param newStrFragment a String to paste (!! MUST NOT contain any '\n', '\r' ... !!; MUST BE a plain one-line String)
	 * @param cursorOnLine pass true if this line is the one with an active cursor
	 * @return see: {@code clearSelection()} (identical return values)
	 */
	public int writeOneLineString(String newStrFragment, boolean cursorOnLine)
	{
		if(cursorOnLine)
		{
			cursor.setText(newStrFragment);
			label.setText(label.getText() + cursor.getText());
		}
		int newXposition = clearSelection();
		if(newXposition < 0)
		{
			newXposition = label.getText().length();
		}
		return(newXposition);
	}
	
	/**
	 * Use: get entire selection, if empty call this on correct line and post-process return value, else use clearSelection
	 * 
	 * @return -1 if a line is to be removed (concatenated with previous line if exists ignored if first line)
	 */
	public int typeBackspace(int charindex)
	{
		if(charindex == 0)
		{
			return -1;
		}else
		{
			label.setText(label.getText().substring(0, label.getText().length()-1));
			return(charindex-1);
		}
	}
	
	/**
	 * Use: get entire selection, if empty call this on correct line and post-process return value, else use clearSelection
	 * 
	 * @return -1 if the next line is to be removed (appended to this line if exists ignored if last line)
	 */
	public int typeDelete(int charindex)
	{
		if(charindex >= getLineText().length())
		{
			return -1;
		}else
		{
			rabel.setText(rabel.getText().substring(1, rabel.getText().length()));
			return(charindex);
		}
	}
	
	// SUBCLASSES
	/**
	 * Border (left) for cursor visualization.
	 *
	 */
	private class LeftCursorBorder extends AbstractBorder
	{
	    //used to display cursor when no selection or selection proceeds to the left
	    @Override
		public Insets getInsets(IFigure figure)
	    {
			return new Insets(1,0,0,0);
		}
	    
		@Override
		public void paint(IFigure figure, Graphics graphics, Insets insets)
		{
			graphics.drawLine(getPaintRectangle(figure, insets).getTopLeft(), tempRect.getBottomLeft());			
		}
	}
	
}