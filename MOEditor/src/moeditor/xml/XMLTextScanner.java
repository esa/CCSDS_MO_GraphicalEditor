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

import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.Token;


public class XMLTextScanner extends RuleBasedScanner
{

	public IToken ESCAPED_CHAR;
	public IToken CDATA_START;
	public IToken CDATA_END;
	public IToken CDATA_TEXT;

	IToken currentToken;

	public XMLTextScanner(ColorManager colorManager)
	{

		ESCAPED_CHAR = new Token(new TextAttribute(colorManager.getColor(IXMLColorConstants.ESCAPED_CHAR)));
		CDATA_START = new Token(new TextAttribute(colorManager.getColor(IXMLColorConstants.CDATA)));
		CDATA_END = new Token(new TextAttribute(colorManager.getColor(IXMLColorConstants.CDATA)));
		CDATA_TEXT = new Token(new TextAttribute(colorManager.getColor(IXMLColorConstants.CDATA_TEXT)));
	}

	public IToken nextToken()
	{
		IToken token = super.nextToken();
		if (currentToken == CDATA_START || currentToken == CDATA_TEXT && token != CDATA_END)
		{
			this.currentToken = CDATA_TEXT;
			return CDATA_TEXT;
		}
		this.currentToken = token;
		return token;
	}
}