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

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;

public class StartTagRule extends MultiLineRule
{

	public StartTagRule(IToken token)
	{
		this(token, false);
	}	
	
	protected StartTagRule(IToken token, boolean endAsWell)
	{
		super("<", endAsWell ? "/>" : ">", token);
	}

	protected boolean sequenceDetected(ICharacterScanner scanner, char[] sequence, boolean eofAllowed)
	{
		int c = scanner.read();
		if (sequence[0] == '<')
		{
			if (c == '?')
			{
				// processing instruction - abort
				scanner.unread();
				return false;
			}
			if (c == '!')
			{
				scanner.unread();
				// comment - abort
				return false;
			}
		}
		else if (sequence[0] == '>')
		{
			scanner.unread();
		}
		return super.sequenceDetected(scanner, sequence, eofAllowed);
	}
}