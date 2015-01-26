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

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class XMLParseErrorHandler  implements ErrorHandler {
	
	public String message = "AllOK";
	
	public XMLParseErrorHandler()
	{	
	}
	
	private String getParseExceptionInfo(SAXParseException spe)
	{
        String systemId = spe.getSystemId();
        if (systemId == null)
        {
            systemId = "null";
        }
        String info = "URI = " + systemId + "; Line = " + spe.getLineNumber() + "; Column = " + spe.getColumnNumber() + ":\n" + spe.getMessage();
        return info;
    }

	@Override
	public void error(SAXParseException arg0) throws SAXException
	{
		this.message = "ERROR " + getParseExceptionInfo(arg0);
		throw new SAXException(message);
	}

	@Override
	public void fatalError(SAXParseException arg0) throws SAXException
	{
		this.message = "FATAL " + getParseExceptionInfo(arg0);
		throw new SAXException(message);
	}

	@Override
	public void warning(SAXParseException arg0) throws SAXException
	{
		this.message = "WARNING " + getParseExceptionInfo(arg0);
		throw new SAXException(message);
	}

}
