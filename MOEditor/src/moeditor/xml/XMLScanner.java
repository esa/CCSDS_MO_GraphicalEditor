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

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;

public class XMLScanner extends RuleBasedScanner {

	public XMLScanner(ColorManager manager) {
		IToken procInstr =
			new Token(
				new TextAttribute(
					manager.getColor(IXMLColorConstants.PROC_INSTR)));
		IToken docType =
			new Token(
				new TextAttribute(
					manager.getColor(IXMLColorConstants.DOCTYPE)));

		IRule[] rules = new IRule[3];
		//Add rule for processing instructions and doctype
		rules[0] = new MultiLineRule("<?", "?>", procInstr);
		rules[1] = new MultiLineRule("<!DOCTYPE", ">", docType);
		// Add generic whitespace rule.
		rules[2] = new WhitespaceRule(new XMLWhitespaceDetector());

		setRules(rules);
	}
}