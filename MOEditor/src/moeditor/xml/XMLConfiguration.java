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

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

public class XMLConfiguration extends SourceViewerConfiguration
{
    private XMLTagScanner tagScanner;
    private XMLScanner scanner;
    private XMLTextScanner textScanner;
    //private CDataScanner cdataScanner;

	private ColorManager colorManager;

    public XMLConfiguration(ColorManager colorManager)
    {
    	super();
        this.colorManager = colorManager;
    }
    
    public String[] getConfiguredContentTypes(ISourceViewer sourceViewer)
    {
        return new String[]
        {
                IDocument.DEFAULT_CONTENT_TYPE,
                XMLPartitionScanner.XML_COMMENT,
                XMLPartitionScanner.XML_PI,
                XMLPartitionScanner.XML_DOCTYPE,
                XMLPartitionScanner.XML_START_TAG,
                XMLPartitionScanner.XML_END_TAG,
                XMLPartitionScanner.XML_TEXT
        };
    }
    
    protected XMLScanner getXMLScanner()
    {
        if (scanner == null)
        {
            scanner = new XMLScanner(colorManager);
            scanner.setDefaultReturnToken(new Token(
                    new TextAttribute(colorManager.getColor(IXMLColorConstants.DEFAULT))));
        }
        return scanner;
    }

    protected XMLTextScanner getXMLTextScanner()
    {
        if (textScanner == null)
        {
            textScanner = new XMLTextScanner(colorManager);
            textScanner.setDefaultReturnToken(new Token(new TextAttribute(colorManager
                    .getColor(IXMLColorConstants.DEFAULT))));
        }
        return textScanner;
    }

    /*
    protected CDataScanner getCDataScanner()
    {
        if (cdataScanner == null)
        {
            cdataScanner = new CDataScanner(colorManager);
            cdataScanner.setDefaultReturnToken(new Token(new TextAttribute(colorManager
                    .getColor(IXMLColorConstants.CDATA_TEXT))));
        }
        return cdataScanner;
    }*/

    protected XMLTagScanner getXMLTagScanner()
    {
        if (tagScanner == null)
        {
            tagScanner = new XMLTagScanner(colorManager);
            tagScanner
                    .setDefaultReturnToken(new Token(new TextAttribute(colorManager.getColor(IXMLColorConstants.TAG))));
        }
        return tagScanner;
    }

    public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer)
    {
        PresentationReconciler reconciler = new PresentationReconciler();

        DefaultDamagerRepairer dr = new DefaultDamagerRepairer(getXMLTagScanner());
        reconciler.setDamager(dr, XMLPartitionScanner.XML_START_TAG);
        reconciler.setRepairer(dr, XMLPartitionScanner.XML_START_TAG);

        dr = new DefaultDamagerRepairer(getXMLTagScanner());
        reconciler.setDamager(dr, XMLPartitionScanner.XML_END_TAG);
        reconciler.setRepairer(dr, XMLPartitionScanner.XML_END_TAG);

        dr = new DefaultDamagerRepairer(getXMLScanner());
        reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
        reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

        dr = new DefaultDamagerRepairer(getXMLScanner());
        reconciler.setDamager(dr, XMLPartitionScanner.XML_DOCTYPE);
        reconciler.setRepairer(dr, XMLPartitionScanner.XML_DOCTYPE);

        dr = new DefaultDamagerRepairer(getXMLScanner());
        reconciler.setDamager(dr, XMLPartitionScanner.XML_PI);
        reconciler.setRepairer(dr, XMLPartitionScanner.XML_PI);

        dr = new DefaultDamagerRepairer(getXMLTextScanner());
        reconciler.setDamager(dr, XMLPartitionScanner.XML_TEXT);
        reconciler.setRepairer(dr, XMLPartitionScanner.XML_TEXT);

        /*dr = new DefaultDamagerRepairer(getCDataScanner());
        reconciler.setDamager(dr, XMLPartitionScanner.XML_CDATA);
        reconciler.setRepairer(dr, XMLPartitionScanner.XML_CDATA);*/

        /*TextAttribute textAttribute = new TextAttribute(colorManager.getColor(IXMLColorConstants.XML_COMMENT));
        NonRuleBasedDamagerRepairer ndr = new NonRuleBasedDamagerRepairer(textAttribute);
        reconciler.setDamager(ndr, XMLPartitionScanner.XML_COMMENT);
        reconciler.setRepairer(ndr, XMLPartitionScanner.XML_COMMENT);*/

        return reconciler;
    }

}
