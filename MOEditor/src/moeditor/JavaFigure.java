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
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import moeditor.draw2d.Button;
import moeditor.draw2d.ButtonClickHandler;
import moeditor.draw2d.ComboBox;
import moeditor.draw2d.ComboBoxPrinter;
import moeditor.draw2d.TableFigure;
import moeditor.draw2d.TheFigure;

import org.codehaus.plexus.util.FileUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.draw2d.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.ui.dialogs.IOverwriteQuery;
import org.eclipse.ui.wizards.datatransfer.FileSystemStructureProvider;
import org.eclipse.ui.wizards.datatransfer.ImportOperation;

import esa.mo.tools.stubgen.GeneratorJava;

public class JavaFigure extends TableFigure implements ButtonClickHandler, ComboBoxPrinter
{
	private MultiPageEditor editor;
	
	public JavaFigure(TheFigure parentFigure, MultiPageEditor editor)
	{
		super(parentFigure, 6);
		this.editor = editor;
		
		setFocusColor(getNonFocusColor());
		
		fillTable();
	}

	private void fillTable()
	{
		ComboBox proj = new ComboBox(this, "project");
		boolean match = false;
		for ( IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects() )
		{
			proj.addComboBoxEntry(project.getName(), project.getName());
			proj.setPrinter(this);
			proj.setText(project.getName());
			if ( editor.projectName != null )
			{
				if ( project.getName().equals(editor.projectName) )
				{
					match = true;
				}
			}
		}
		
		if ( match )
		{
			proj.setText(editor.projectName);
		}
		else
		{
			editor.projectName = proj.getText();
		}
		
		ComboBox combo = new ComboBox(this, "workspace");
		combo.addComboBoxEntry("ws", " to the \"src\" folder of the project ");
		combo.addComboBoxEntry("def", " to the external folder ");
		combo.setPrinter(this);
		
		Button button = new Button(this, "new.png", "new");
		button.setButtonClickHandler(this);
		
		add(new Label("Generate Java Code"));
		add(combo);
		if ( editor.ws )
		{
			combo.setText(" to the \"src\" folder of the project ");
			add(proj);
		}
		else
		{
			combo.setText(" to the external folder ");
			String text;
			if ( editor.outputFolder == null )
			{
				text = "Not set";
			}
			else
			{
				text = editor.outputFolder;
			}
			addCellWithButtonLeft(getEditableColor(), button, text);
		}
		
		Label space = new Label(" ");
		add(space);
		
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
				editor.outputFolder = folder;
			}
			removeAll();
			fillTable();
		}
		else if ( button.getId().equals("go") )
		{
			if ( editor.ws )
			{
				if ( editor.projectName != null )
				{
					generateTemp();
				}
			}
			else if ( editor.outputFolder != null )
			{
				MLog log = new MLog(editor.getConsoleStream(), editor.getConsoleStream(), editor.getConsoleStream(), editor.getConsoleStream(), editor.getConsoleStream());
				log.setHeading("Java Generation");
				((ExternalPage) getParentFigure()).generateGenerator(editor.outputFolder, new GeneratorJava(log));
				log.printStat();
			}
		}
	}
	
	protected void generateTemp()
	{
		File temp;
		try
		{
			temp = File.createTempFile("prefix", "suffix");
			if ( !temp.delete() )
		    {
		        throw new IOException("Could not delete temp file " + temp.getAbsolutePath());
		    }
			if ( !temp.mkdir() )
		    {
		        throw new IOException("Could not create temp directory " + temp.getAbsolutePath());
		    }

		}
		catch (IOException e)
		{
			editor.ShowException(e.getMessage());
			e.printStackTrace();
			return;
		}
		
		String outputFolder = temp.getAbsolutePath();

		MLog log = new MLog(editor.getConsoleStream(), editor.getConsoleStream(), editor.getConsoleStream(), editor.getConsoleStream(), editor.getConsoleStream());
		log.setHeading("JAVA Generation");
		((ExternalPage) getParentFigure()).generateGenerator(outputFolder, new GeneratorJava(log));
		log.printStat();
		
		for ( IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects() )
		{
			if ( !project.getName().equals(editor.projectName) )
			{
				continue;
			}
			
			IOverwriteQuery overwriteQuery = new IOverwriteQuery()
			{
				public String queryOverwrite(String file)
				{
					return ALL;
				}
			};
						
			String baseDir = outputFolder;// location of files to import
			IPath ipath = project.getFullPath();
			ipath = ipath.append("src");
			ImportOperation importOperation = new ImportOperation(ipath, new File(baseDir), FileSystemStructureProvider.INSTANCE, overwriteQuery);
			//importOperation.setOverwriteResources(false);
			importOperation.setCreateContainerStructure(false);
			try
			{
				importOperation.run(new NullProgressMonitor());
			}
			catch (InvocationTargetException e)
			{
				e.printStackTrace();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		
		try
		{
			FileUtils.deleteDirectory(temp);
		}
		catch (IOException e)
		{
			editor.ShowException("Could not delete temp directory " + temp.getAbsolutePath() + "\n" + e.getMessage());
	        e.printStackTrace();
	    }
	}

	@Override
	public void print(ComboBox comboBox, String text)
	{
		if ( comboBox.getId().equals("project") )
		{
			editor.projectName = text;
		}
		else if ( comboBox.getId().equals("workspace") )
		{
			if ( text.equals("ws") )
			{
				editor.ws = true;
			}
			else if ( text.equals("def") )
			{
				editor.ws = false;
			}
		}
		removeAll();
		fillTable();
	}
	
}