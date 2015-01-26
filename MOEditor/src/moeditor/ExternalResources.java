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

import moeditor.draw2d.CheckBoxCheckedHandler;
import moeditor.draw2d.InteractiveCheckBox;
import moeditor.draw2d.TheFigure;
import moeditor.xml.XMLParser;

import org.w3c.dom.Document;

public class ExternalResources implements CheckBoxCheckedHandler 
{
	private File file;
	private boolean checked = false;
	private Document doc = null;
	private InteractiveCheckBox checkBox;
	private boolean processed = true;
	private boolean external = false;
	private MultiPageEditor editor;
	
	public ExternalResources(File file, TheFigure parentFigure, MultiPageEditor editor)
	{
		this.editor = editor;
		setFile(file);
		setCheckBox(new InteractiveCheckBox(parentFigure));
		getCheckBox().setCheckBoxCheckedHandler(this);
		setChecked(getCheckBox().isChecked());
	}

	public File getFile()
	{
		return file;
	}

	private void setFile(File file)
	{
		this.file = file;
	}

	public boolean isChecked()
	{
		checked = getCheckBox().isChecked();
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public InteractiveCheckBox getCheckBox()
	{
		return checkBox;
	}

	public void setCheckBox(InteractiveCheckBox checkBox)
	{
		this.checkBox = checkBox;
	}

	@Override
	public void CheckBoxChecked(boolean check)
	{
		setChecked(check);
		if ( check )
		{
			XMLParser parser = new XMLParser(editor, file);
			parser.setErrorHandler(new XMLParseErrorHandler());
			setDoc(parser.parse());
		}
	}

	public Document getDoc()
	{
		return doc;
	}

	public void setDoc(Document doc)
	{
		this.doc = doc;
	}

	public boolean isProcessed()
	{
		return processed;
	}

	public void setProcessed(boolean processed)
	{
		this.processed = processed;
	}

	public boolean isExternal()
	{
		return external;
	}

	public void setExternal(boolean external)
	{
		this.external = external;
	}

}
