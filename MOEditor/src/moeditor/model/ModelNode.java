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

package moeditor.model;

import java.util.ArrayList;
import java.util.List;

import moeditor.MultiPageEditor;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ModelNode
{
	private ModelNode root = null;
	protected Node node;
	private ModelNode parent;
	private List<ModelNode> children;
	private int childNum;
	private MultiPageEditor mpe;
	private int aboutToBeEdited = 0;
	private int depth;
	
	public ModelNode(Node node, ModelNode parent, MultiPageEditor mpe)
	{
		if ( parent == null )
		{
			setRoot(this);
			depth = 0;
		}
		else
		{
			setRoot(parent.getRoot());
			depth = parent.getDepth() + 1;
		}
		//setParent(parent);
		
		this.children = new ArrayList<ModelNode>();
		this.parent = parent;
		this.node = node;
		this.childNum = 0;
		this.mpe = mpe;
	}
	
	public int getDepth()
	{
		return depth;
	}
	
	public void setMPE()
	{
		this.mpe = root.getMPE();
	}
	
	public MultiPageEditor getMPE()
	{
		return mpe;
	}
	
	public ModelNode getRoot() {
		return root;
	}

	private void setRoot(ModelNode root)
	{
		this.root = root;
	}

	public int getChildNum() {
		return this.childNum;
	}
	
	public void setChildNum(int childNum) {
		this.childNum = childNum;
	}
	
	public Node getNode() {
		return this.node;
	}
	
	public void setNode(Node node) {
		this.node = node;
	}
	
	public void setNodeValue(String nodeValue) {
		this.node.setNodeValue(nodeValue);
	}
	
	public void setPrefix(String prefix) {
		this.node.setPrefix(prefix);
	}
	
	public void setTextContent(String textContent) {
		this.node.setTextContent(textContent);
	}

	public NamedNodeMap getAttributes() {
		return this.node.getAttributes();
	}

	public String getBaseURI() {
		return this.node.getBaseURI();
	}

	public NodeList getChildNodes()
	{
		return node.getChildNodes();
	}
	
	public String getLocalName() {
		String name = this.node.getLocalName();
		return name;
	}
	
	public String getNamespaceURI() {
		return this.node.getNamespaceURI();
	}
	
	public String getNodeName() {
		return this.node.getNodeName();
	}
	
	public String getNodeValue() {
		return this.node.getNodeValue();
	}

	public ModelNode addChild(ModelNode child)
	{
		boolean ble = children.add(child);
		if ( ble )
		{
			child.setParent(this);
		}
		return ble ? child : null;
	}

	private void insertChild(ModelNode child, int arrayIndex)
	{
		children.add(arrayIndex, child);
		child.setParent(this);
	}
	
	public boolean removeChild(ModelNode child)
	{
		return children.remove(child);
	}
	
	public ModelNode replaceNode(ModelNode orig, Node by)
	{
		Node origNode = orig.getNode();
		this.getNode().replaceChild(by, origNode);
		orig.setNode(by);
		return orig;
	}
	
	public boolean deleteChild(ModelNode child)
	{
		Node lastbefore = null;
		for ( Node pepik = node.getFirstChild(); pepik != null; pepik = pepik.getNextSibling() )
		{
			if ( pepik.equals(child.getNode()) )
			{
				pepik.getNextSibling();
				break;
			}
			lastbefore = pepik;
		}
		
		if ( lastbefore != null )
		{
			if ( lastbefore.getNodeType() == Node.TEXT_NODE )
			{
				node.removeChild(lastbefore);
			}
		}
		node.removeChild(child.getNode());
		return removeChild(child);
	}
	
	public ModelNode appendChild(ModelNode child)
	{
		node.appendChild(child.getNode());
		child.setRoot(getRoot());
		child.setMPE();
		return addChild(child);
	}
	
	public ModelNode appendNode(Node childNode)
	{
		ModelNode child = new ModelNode(childNode, this, getMPE());
		/*
		 * 
		 */
		int num = node.getChildNodes().getLength();
		Document doc = (Document) getRoot().getNode();
		if ( num == 0 )
		{
			String border = "\n";
			for ( int i = 0; i < depth; i++ )
			{
				border += "\t";
			}
			Node enters = doc.createTextNode(border);
			node.appendChild(enters);
		}
		else
		{
			String border = "\t";
			Node tab = doc.createTextNode(border);
			node.appendChild(tab);
		}
		/*
		 * 
		 */
		node.appendChild(child.getNode());
		/*
		 * 
		 */
		String border = "\n";
		for ( int i = 0; i < depth - 1; i++ )
		{
			border += "\t";
		}
		Node enters = doc.createTextNode(border);
		node.appendChild(enters);
		/*
		 * 
		 */
		return addChild(child);
	}
	
	private void setParent(ModelNode parent) {
		this.parent = parent;
		//if ( this.parent != null ) this.parent.addChild(this);
		//if ( parent != null ) parent.addChild(this);
	}
	
	public ModelNode getParent() {
		return this.parent;
	}

	public List<ModelNode> getChildrenArray() {
		return this.children;
	}
		
	public String getAttribute(String name)
	{
		return ((Element) node).getAttribute(name);
		/*
		NamedNodeMap attributes = node.getAttributes();
		//if ( attributes == null ) return null;
		for ( int i = 0; i < attributes.getLength(); i++ )
		{
            Node att = attributes.item(i);
            if ( att.getLocalName().equals(name) )
            {
            	return att.getNodeValue();
            }
        }
		return null;
		*/
	}
	
	public boolean setAttribute(String name, String value)
	{
		((Element) node).setAttribute(name, value);
		return true;
		/*
		NamedNodeMap attributes = node.getAttributes();
		for ( int i = 0; i < attributes.getLength(); i++ )
		{
            Node att = attributes.item(i);
            if ( att.getLocalName().equals(name) )
            {
            	att.setNodeValue(value);
            	return true;
            }
        }
		return false;*/
	}
	
	public ModelNode getChildByLocalName(String localName)
	{
		for ( ModelNode child : getChildrenArray() )
		{
			if ( child.getLocalName().equals(localName) ) return child;
		}
		return null;
	}
	
	public ArrayList<ModelNode> getChildrenByLocalName(String localName)
	{
		ArrayList<ModelNode> retval = new ArrayList<ModelNode>();
		for ( ModelNode child : getChildrenArray() )
		{
			if ( child.getLocalName().equals(localName) )
			{
				retval.add(child);
			}
		}
		return retval;
	}

	public ModelNode insertNode(Element element, int arrayIndex, int listIndex)
	{
		NodeList list = node.getChildNodes();
		int listLength = list.getLength();
		if ( listIndex == listLength || listLength == 0 )
		{
			return appendNode(element);
		}
		
		ModelNode child = new ModelNode(element, this, getMPE());
		/*
		 * 
		 */
		String border = "\n";
		for ( int i = 0; i < depth; i++ )
		{
			border += "\t";
		}
		Document doc = (Document) getRoot().getNode();
		Node enters = doc.createTextNode(border);
		node.insertBefore(enters, list.item(listIndex));
		/*
		 * 
		 */
		
		node.insertBefore(child.getNode(), list.item(listIndex + 1));// + 1));
		insertChild(child, arrayIndex);
		return child;
	}

	public int isAboutToBeEdited()
	{
		return aboutToBeEdited;
	}

	public void incAboutToBeEdited()
	{
		aboutToBeEdited += 1;
	}
	
	public void decAboutToBeEdited()
	{
		aboutToBeEdited -= 1;
	}
	
	public void setAboutToBeEdited(int aboutToBeEdited)
	{
		this.aboutToBeEdited = aboutToBeEdited;
	}
	
	public int getMaxOf(String attribute)
	{
		int max = 0;
		for ( ModelNode child : getChildrenArray() )
		{
			if ( child.getAttribute(attribute) != null )
			if ( !child.getAttribute(attribute).isEmpty() )
			{
				try
				{
					int number = new Integer(child.getAttribute(attribute));
					max = ( max > number) ? max : number;
				}
				catch ( NumberFormatException e )
				{
					System.err.println("WTF? " + attribute + " is not an integer?!");
				}
			}
		}
		return max;
	}

	public int getMaxOf2(String attribute)
	{
		int max = 0;
		for ( ModelNode kid : getChildrenArray() )
		{
			for ( ModelNode child : kid.getChildrenArray() )
			{
				if ( child.getAttribute(attribute) != null )
				if ( !child.getAttribute(attribute).isEmpty() )
				{
					try
					{
						int number = new Integer(child.getAttribute(attribute));
						max = ( max > number) ? max : number;
					}
					catch ( NumberFormatException e )
					{
						System.err.println("WTF? " + attribute + " is not an integer?!");
					}
				}
			}
		} 
		return max;
	}
	
	public int getMaxOf8(String localname, String attribute)
	{
		NodeList list = ((Element) getRoot().getChildByLocalName("specification").getNode()).getElementsByTagName("mal:" + localname);
		int max = 0;
		for ( int i = 0; i < list.getLength(); i++ )
		{
			try
			{
				int number = new Integer(((Element) list.item(i)).getAttribute(attribute));
				max = ( max > number) ? max : number;
			}
			catch ( NumberFormatException e )
			{
				System.err.println("WTF? " + attribute + " is not an integer?!");
			}
		}
		return max;
	}

	public void delAttribute(String string)
	{
		((Element) getNode()).removeAttribute(string);
	}
	
	public static int getMax2(int a, int b)
	{
		if ( a > b ) return a;
		return b;
	}
	
	public static int getMax3(int a, int b, int c)
	{
		return getMax2(getMax2(a, b), getMax2(b, c));
	}
}
