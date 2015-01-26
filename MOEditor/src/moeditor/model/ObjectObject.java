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

import java.io.StringWriter;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import moeditor.MultiPageEditor;
import moeditor.ObjectBox;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.PolylineConnection;

public class ObjectObject
{
	private ModelNode area;
	private ModelNode service;
	private ModelNode object;
	private ObjectObject related = null;
	private ObjectObject source = null;
	private int numOfRelations = 0;
	private int numOfSourcations = 0;
	public PolylineConnection connection = null;
	public ChopboxAnchor anchor;
	private ObjectBox objectBox = null;
	private String huml;
	
	public ObjectObject(ModelNode area, ModelNode service, ModelNode object)
	{
		setArea(area);
		setService(service);
		setObject(object);
		setHuml(createHuml());
	}
	
	private String createHuml()
	{
		String retval = getLongName();
		StringWriter writer = new StringWriter();
		Transformer transformer;
		try
		{
			transformer = TransformerFactory.newInstance().newTransformer();
			transformer.transform(new DOMSource(getObject().getNode()), new StreamResult(writer));
		}
		catch (TransformerConfigurationException e)
		{
			e.printStackTrace(MultiPageEditor.getRedStream());
			retval += e.getMessage();
		}
		catch (TransformerFactoryConfigurationError e)
		{
			e.printStackTrace(MultiPageEditor.getRedStream());
			retval += e.getMessage();
		}
		catch (TransformerException e)
		{
			e.printStackTrace(MultiPageEditor.getRedStream());
			retval += e.getMessage();
		}
		retval += writer.toString();
		return retval;
	}

	public String getName()
	{
		return getObject().getAttribute("name");
	}
	
	public String getNumber()
	{
		return getObject().getAttribute("number");
	}
	
	public String getLongName()
	{
		return getArea().getAttribute("name") + ":"  + getService().getAttribute("name") + ":" + getObject().getAttribute("name");
	}

	public ModelNode getArea()
	{
		return area;
	}

	public void setArea(ModelNode area)
	{
		this.area = area;
	}

	public ModelNode getObject()
	{
		return object;
	}

	public void setObject(ModelNode object)
	{
		this.object = object;
	}

	public ModelNode getService()
	{
		return service;
	}

	public void setService(ModelNode service)
	{
		this.service = service;
	}

	public ObjectObject getRelated()
	{
		return related;
	}

	public void setRelated(ObjectObject related)
	{
		this.related = related;
		related.incNumOfRelations();
	}

	public ObjectObject getSource()
	{
		return source;
	}

	public void setSource(ObjectObject source)
	{
		this.source = source;
		source.incNumOfSourcations();
	}
	
	public void incNumOfRelations()
	{
		numOfRelations++;
	}
	
	public void incNumOfSourcations()
	{
		numOfSourcations++;
	}

	public int getNumOfRelations()
	{
		return numOfRelations;
	}
	
	public int getNumOfSourcations()
	{
		return numOfSourcations;
	}

	public String getAreaName()
	{
		return getArea().getAttribute("name");
	}
	
	public String getServiceName()
	{
		return getService().getAttribute("name");
	}

	public PolylineConnection getConnection()
	{
		return connection;
	}

	public void setConnection(PolylineConnection connection)
	{
		this.connection = connection;
	}

	public ObjectBox getObjectBox()
	{
		return objectBox;
	}

	public void setObjectBox(ObjectBox objectBox)
	{
		this.objectBox = objectBox;
	}

	public ChopboxAnchor getAnchor()
	{
		return anchor;
	}

	public void setAnchor(ChopboxAnchor anchor)
	{
		this.anchor = anchor;
	}

	public String getHuml()
	{
		return huml;
	}

	public void setHuml(String huml)
	{
		this.huml = huml;
	}
}
