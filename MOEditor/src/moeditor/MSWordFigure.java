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

package moeditor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;

import esa.mo.tools.stubgen.GeneratorDocx;
import moeditor.draw2d.Button;
import moeditor.draw2d.ButtonClickHandler;
import moeditor.draw2d.TableFigure;
import moeditor.draw2d.TheFigure;

public class MSWordFigure extends TableFigure implements ButtonClickHandler
{
	private MultiPageEditor editor;
	
	public MSWordFigure(TheFigure parentFigure, MultiPageEditor editor)
	{
		super(parentFigure, 3);
		this.editor = editor;
		
		setFocusColor(getNonFocusColor());
		
		fillTable();
	}

	private void fillTable()
	{
		Button button = new Button(this, "new.png", "new");
		button.setButtonClickHandler(this);
		addHeadingCell("Output Folder for the MS Word document");
		if ( editor.docxFolder == null )
		{
			addCellWithButtonLeft(getEditableColor(), button, "Not set");
		}
		else
		{
			addCellWithButtonLeft(getEditableColor(), button, "Set to: " + editor.docxFolder);
		}
		Button go = new Button(this, "go.png", "go");
		go.setButtonClickHandler(this);
		add(go);
	}

	@Override
	public void ButtonClicked(Button button)
	{
		if ( button.getId().equals("new") )
		{
			DirectoryDialog dialog = new DirectoryDialog(getDisplay().getActiveShell(), SWT.SAVE);
			dialog.setText("Select Output Folder");
			String folder = dialog.open();
			if ( folder != null )
			{
				setOutputFolder(folder);
			}
			removeAll();
			fillTable();
		}
		else if ( button.getId().equals("go") )
		{
			if ( editor.docxFolder != null )
			{
				MLog log = new MLog(editor.getConsoleStream(), editor.getConsoleStream(), editor.getConsoleStream(), editor.getConsoleStream(), editor.getConsoleStream());
				log.setHeading("DOCX Generation");
				((ExternalPage) getParentFigure()).generateGenerator(editor.docxFolder, new GeneratorDocx(log));
				log.printStat();
			}
		}
	}

	private void setOutputFolder(String outputFolder)
	{
		editor.docxFolder = outputFolder;
	}

}
