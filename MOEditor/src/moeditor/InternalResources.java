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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

import moeditor.xml.XMLParser;

import org.w3c.dom.Document;

public class InternalResources
{
	private Document doc = null;
	private MultiPageEditor mpe;
	private String filename;
	private File file;
	
	public InternalResources(String filename, MultiPageEditor mpe)
	{
		this.mpe = mpe;
		this.filename = filename;
	}
	
	public void prepareDoc()
	{
		File temp;
		try {
			temp = File.createTempFile("pattern", ".suffix");
		    temp.deleteOnExit();
		    BufferedWriter out = new BufferedWriter(new FileWriter(temp));
		    out.write(getFileContent(filename));
		    out.close();
		    setFile(temp);
		    XMLParser parser = new XMLParser(mpe, temp);
			parser.setErrorHandler(new XMLParseErrorHandler());
			doc = parser.parse();

		} catch (IOException e) {
			mpe.ShowException(e.getMessage());
		}
	}
	
	public Document getDocument()
	{
		return doc;
	}
	
	public String getFileName()
	{
		return filename.substring(1);
	}
	
	private String getFileContent(String filename) {
		InputStream is = MultiPageEditor.class.getResourceAsStream(filename);
		
		StringWriter writer = new StringWriter();
				
		BufferedReader in = new BufferedReader(new InputStreamReader(is));
		String line = null;
		try
		{
			while( ( line = in.readLine() ) != null )
			{
				writer.write(line);
			}
		}
		catch (IOException e)
		{
			mpe.ShowException(e.getMessage());
		}
		return  writer.toString();
	}

	public File getFile()
	{
		return file;
	}

	private void setFile(File file)
	{
		this.file = file;
	}

}
