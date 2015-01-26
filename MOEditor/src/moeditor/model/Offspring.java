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

public class Offspring
{
	private String name;
	private Offspring parent;
	private ArrayList<Offspring> children = new ArrayList<Offspring>();
	private boolean root = false;
	private boolean expanded = false;
	private ArrayList<String> attribute = new ArrayList<String>();
	private String number = null;
	
	public Offspring()
	{
		setName(null);
		setParent(null);
	}
	
	public Offspring(String name)
	{
		setName(name);
		setParent(null);
	}
	
	public Offspring(String name, Offspring parent)
	{
		setName(name);
		setParent(parent);
	}
	
	public Offspring(String name, Offspring parent, ArrayList<String> attribute)
	{
		setName(name);
		setParent(parent);
		setAttribute(attribute);
	}
	
	public void addChild(Offspring child)
	{
		children.add(child);
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Offspring getParent()
	{
		return parent;
	}

	public void setParent(Offspring parent)
	{
		this.parent = parent;
		if ( parent != null )
		{
			this.parent.addChild(this);
		}
		else setRoot(true);
	}

	public ArrayList<Offspring> getChildren()
	{
		return children;
	}

	public void setChildren(ArrayList<Offspring> children) 
	{
		this.children = children;
	}

	public boolean isRoot()
	{
		return root;
	}

	public void setRoot(boolean root)
	{
		this.root = root;
	}

	public boolean isExpanded()
	{
		return expanded;
	}

	public void setExpanded(boolean expanded)
	{
		this.expanded = expanded;
	}

	public ArrayList<String> getAttribute()
	{
		return attribute;
	}

	public void setAttribute(ArrayList<String> attribute)
	{
		this.attribute = attribute;
	}
	
	public void removeChild(Offspring child)
	{
		children.remove(child);
	}

	public String getNumber()
	{
		return number;
	}

	public void setNumber(String number)
	{
		this.number = number;
	}

}
