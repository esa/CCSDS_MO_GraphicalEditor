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
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

public class NonMatchingRule implements IPredicateRule
{
	public NonMatchingRule()
	{
		super();
	}

	@Override
	public IToken evaluate(ICharacterScanner scanner)
	{
		return Token.UNDEFINED;
	}

	@Override
	public IToken getSuccessToken()
	{
		return Token.UNDEFINED;
	}

	@Override
	public IToken evaluate(ICharacterScanner scanner, boolean resume)
	{
		return Token.UNDEFINED;
	}

}
