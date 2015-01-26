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

import java.util.ArrayList;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;

import moeditor.draw2d.TheFigure;
import moeditor.model.ModelNode;
import moeditor.model.ObjectObject;

public class HumlDiagram
{
	private ModelNode specification;
	private ModelNode root;
	private MultiPageEditor editor;
	private ArrayList<ObjectObject> objectList = new ArrayList<ObjectObject>();
	private ArrayList<ObjectBox> objectBoxList = new ArrayList<ObjectBox>();
	private MLog log;
	private TheFigure humlFigure;
	private String huml;
	
	public HumlDiagram(TheFigure humlFigure, MultiPageEditor multiPageEditor, ModelNode root)
	{
		setHumlFigure(humlFigure);
		
		setEditor(multiPageEditor);
		log = new MLog(getEditor().getConsoleStream(), getEditor().getConsoleStream(), getEditor().getConsoleStream(), getEditor().getConsoleStream(), getEditor().getConsoleStream());
		setSpecification(root);
		
		initialize();
	}
	
	private void initialize()
	{
		getObjects("events");
		getObjects("objects");
		setHuml(createHuml());
	}
	
	public boolean fillTable()
	{
		boolean retval = true;
		log = new MLog(getEditor().getConsoleStream(), getEditor().getConsoleStream(), getEditor().getConsoleStream(), getEditor().getConsoleStream(), getEditor().getConsoleStream());
		log.setHeading("Generating Object Links");
		retval = retval && doLinks();
		retval = retval && checkLinks();
		doFigures();
		log.printStat();
		return retval;
	}

	private void getObjects(String name)
	{
		for ( ModelNode area : specification.getChildrenByLocalName("area") )
		{
			for ( ModelNode service : area.getChildrenByLocalName("service") )
			{
				for ( ModelNode features : service.getChildrenByLocalName("features") )
				{
					for ( ModelNode objects : features.getChildrenByLocalName(name) )
					{
						for ( ModelNode object : objects.getChildrenArray() )
						{
							objectList.add(new ObjectObject(area, service, object));
						}
					}
				}
			}
		}
	}
	
	private String createHuml()
	{
		String retval = "";
		for ( ObjectObject object : objectList )
		{
			retval += object.getHuml();
		}
		return retval;
	}

	private boolean doLinks()
	{
		boolean retval = true;
		for ( ObjectObject object : objectList )
		{
			ModelNode sourceObject = object.getObject().getChildByLocalName("sourceObject");
			if ( sourceObject != null )
			{
				ModelNode objectType = sourceObject.getChildByLocalName("objectType");
				if ( objectType != null )
				{
					ObjectObject src = findObject(objectType.getAttribute("area"), objectType.getAttribute("service"), objectType.getAttribute("number"));
					if ( src != null )
					{
						object.setSource(src);
					}
					else
					{
						retval = false;
						log.error("Object " + objectType.getAttribute("area") + ":" + objectType.getAttribute("service") + ":" + objectType.getAttribute("number")
								+ " referenced (using sourceObject) by Object " + object.getLongName()
								+ " was not found.");
					}
				}
			}
			
			ModelNode relatedObject = object.getObject().getChildByLocalName("relatedObject");
			if ( relatedObject != null )
			{
				ModelNode objectType = relatedObject.getChildByLocalName("objectType");
				if ( objectType != null )
				{
					ObjectObject dst = findObject(objectType.getAttribute("area"), objectType.getAttribute("service"), objectType.getAttribute("number"));
					if ( dst != null )
					{
						object.setRelated(dst);
					}
					else
					{
						retval = false;
						log.error("Object " + objectType.getAttribute("area") + ":" + objectType.getAttribute("service") + ":" + objectType.getAttribute("number")
								+ " referenced (using relatedObject) by Object " + object.getLongName()
								+ " was not found.");
					}
				}
			}
		}
		return retval;
	}
	
	private ObjectObject findObject(String area, String service, String number)
	{
		for ( ObjectObject object : objectList )
		{
			if ( object.getArea().getAttribute("name").equals(area) && object.getService().getAttribute("name").equals(service) && object.getObject().getAttribute("number").equals(number) )
			{
				return object;
			}
		}
		return null;
	}
	
	private boolean checkLinks()
	{
		boolean retval = true;
		for ( ObjectObject object : objectList )
		{
			log.debug(object.getLongName() + " is pointed (using relatedObject) by " + object.getNumOfRelations()
					+ (object.getNumOfRelations() == 1 ? " other object." : " other objects."));
			if ( object.getRelated() != null )
			{
				if ( object.getRelated().getSource() != null )
				{
					if ( !object.getRelated().getSource().equals(object) )
					{
						log.warn("relatedObject: " + object.getLongName() + " -> " + object.getRelated().getLongName()
								+ " <- " + object.getRelated().getSource().getLongName());
					}
				}
				else
				{
					log.warn("relatedObject: " + object.getLongName() + " -> " + object.getRelated().getLongName() + " <- null");
				}
				
				if ( object.equals(object.getRelated()) )
				{
					retval = false;
					log.error("Object " + object.getLongName() + " is pointing to itsefl (using relatedObject).");
				}
			}
		}
		
		for ( ObjectObject object : objectList )
		{
			log.debug(object.getLongName() + " is pointed (using sourceObject) by " + object.getNumOfSourcations()
					+ (object.getNumOfSourcations() == 1 ? " other object." : " other objects."));
			if ( object.getSource() != null )
			{
				if ( object.getSource().getRelated() != null )
				{
					if ( !object.getSource().getRelated().equals(object) )
					{
						log.warn("sourceObject: " + object.getLongName() + " -> " + object.getSource().getLongName()
								+ " <- " + object.getSource().getRelated().getLongName());
					}
				}
				else
				{
					log.warn("sourceObject: " + object.getLongName() + " -> " + object.getSource().getLongName() + " <- null");
				}
				
				if ( object.equals(object.getSource()) )
				{
					retval = false;
					log.error("Object " + object.getLongName() + " is pointing to itsefl (using sourceObject).");
				}
			}
		}
		return retval;
	}
	
	private void doFigures()
	{
		int i = 0;
		for ( ObjectObject object : objectList )
		{
			ObjectBox ob = new ObjectBox(getHumlFigure(), object);
			object.anchor = new ChopboxAnchor(ob);
			objectBoxList.add(ob);
			getHumlFigure().add(ob);
			int width = 210;
			int num = 4;
			int space = 10;
			int x = space + ( ( width * i ) % ( width * num ) ) + space * ( i % num );
			int y = space + ( width + space ) * ( i / num );
			getHumlFigure().setConstraint(ob, new Rectangle(x, y, -1, -1));
			i++;
		}
		for ( ObjectObject object : objectList )
		{
			if ( object.getRelated() != null )
			{
				object.connection = new PolylineConnection();
				object.connection.setSourceAnchor(object.anchor);
				object.connection.setTargetAnchor(object.getRelated().anchor);
				
				PolygonDecoration decoration = new PolygonDecoration();
				PointList decorationPointList = new PointList();
				decorationPointList.addPoint(0,0);
				decorationPointList.addPoint(-2,2);
				decorationPointList.addPoint(-2,0);
				decorationPointList.addPoint(-2,-2);
				decoration.setTemplate(decorationPointList);
				object.connection.setTargetDecoration(decoration);
				
				getHumlFigure().add(object.connection);
			}
			
			if ( object.getSource() != null )
			{
				object.connection = new PolylineConnection();
				object.connection.setSourceAnchor(object.anchor);
				object.connection.setTargetAnchor(object.getSource().anchor);
				
				PolygonDecoration decoration = new PolygonDecoration();
				PointList decorationPointList = new PointList();
				decorationPointList.addPoint(-2,0);
				decorationPointList.addPoint(-2,2);
				decorationPointList.addPoint(-4,0);
				decorationPointList.addPoint(-2,-2);
				decoration.setTemplate(decorationPointList);
				object.connection.setBackgroundColor(ColorConstants.red);
				object.connection.setForegroundColor(ColorConstants.red);
				object.connection.setTargetDecoration(decoration);
				
				getHumlFigure().add(object.connection);
			}
		}
	}

	public MultiPageEditor getEditor()
	{
		return editor;
	}

	public void setEditor(MultiPageEditor editor)
	{
		this.editor = editor;
	}

	public TheFigure getHumlFigure()
	{
		return humlFigure;
	}

	private void setHumlFigure(TheFigure humlFigure)
	{
		this.humlFigure = humlFigure;
	}
	
	private void setSpecification(ModelNode root)
	{
		this.root = root;
		specification = this.root.getChildByLocalName("specification");
	}

	public ModelNode getSpecification()
	{
		return specification;
	}

	public ModelNode getRoot() {
		return root;
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
