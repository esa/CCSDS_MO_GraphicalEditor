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

import java.io.File;
import java.util.ArrayList;

import moeditor.draw2d.Button;
import moeditor.draw2d.ButtonClickHandler;
import moeditor.draw2d.TableFigure;
import moeditor.draw2d.TheFigure;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.part.FileEditorInput;

public class ExternalResourcesFigure extends TableFigure implements ButtonClickHandler
{
	private MultiPageEditor editor;
	public ExternalResourcesFigure(TheFigure parentFigure, MultiPageEditor editor)
	{
		super(parentFigure, 2);
		this.editor = editor;
		
		setFocusColor(getNonFocusColor());
		
		fillTable();
	}

	private void fillTable()
	{
		String currFile = ((FileEditorInput)editor.getEditorInput()).getFile().getRawLocation().toString();
		
		for ( ExternalResources resource : editor.checkBox )
		{
			resource.setProcessed(false);
		}
		
		for ( IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects() )
		{
			File dir = project.getLocation().toFile();
			for ( File file : getFiles(dir) )
			{
				if ( !file.getAbsolutePath().equals(currFile) && file.getName().contains(".mo") )
				{
					if ( !isThere(file) )
					{
						ExternalResources resource = new ExternalResources(file, getParentFigure(), editor);
						editor.checkBox.add(resource);
					}
				}
			}
		}
		
		for ( int i = editor.checkBox.size() - 1; i >=0; i-- )
		{
			if ( !editor.checkBox.get(i).isProcessed() && !editor.checkBox.get(i).isExternal() )
			{
				editor.checkBox.remove(i);
			}
		}
		
		addHeadingCell("File");
		addHeadingCell("Use");
		for ( ExternalResources resource : editor.checkBox )
		{
			addCell(resource.getFile().getAbsolutePath());
			addCell(resource.getCheckBox());
		}
		
		Button button = new Button(getParentFigure(), "new.png", "new");
		button.setButtonClickHandler(this);
		add(button);
	}
	
	private boolean isThere(File file)
	{
		for ( ExternalResources resource : editor.checkBox )
		{
			if ( resource.getFile().equals(file) )
			{
				resource.setProcessed(true);
				return true;
			}
		}
		return false;
	}
	
	private ArrayList<File> getFiles(File dir)
	{
		ArrayList<File> result = new ArrayList<File>();
		for ( File file : dir.listFiles() )
		{
			if ( file.isFile() )
			{
				result.add(file);
			}
			else if ( file.isDirectory() )
			{
				for ( File sub : getFiles(file) )
				{
					result.add(sub);
				}
			}
		}
		return result;
	}

	@Override
	public void ButtonClicked(Button button)
	{
		FileDialog dialog = new FileDialog(MultiPageEditor.getDisplay().getActiveShell(), SWT.OPEN);
		dialog.setText("Select File");
		String filename = dialog.open();
		if ( filename != null )
		{
			ExternalResources resource = new ExternalResources(new File(filename), getParentFigure(), editor);
			resource.setExternal(true);
			editor.checkBox.add(resource);
		}
		removeAll();
		fillTable();
	}
}
