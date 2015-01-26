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

package moeditor.xml;

import org.eclipse.swt.graphics.RGB;

public interface IXMLColorConstants
{

	RGB XML_COMMENT = new RGB(128, 0, 0);
	RGB PROC_INSTR = new RGB(200, 20, 200);
	RGB DOCTYPE = new RGB(0, 150, 150);
	RGB STRING = new RGB(0, 128, 0);
	RGB DEFAULT = new RGB(0, 0, 0);
	RGB TAG = new RGB(0, 0, 128);

	//enhancements
	RGB ESCAPED_CHAR = new RGB(128, 128, 0);
	RGB CDATA = new RGB(0, 128, 128);
	RGB CDATA_TEXT = new RGB(255, 0, 0);
}