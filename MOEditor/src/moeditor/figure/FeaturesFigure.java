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

package moeditor.figure;

import java.util.ArrayList;

import moeditor.draw2d.Button;
import moeditor.draw2d.ButtonClickHandler;
import moeditor.draw2d.TheFigure;
import moeditor.model.ModelNode;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class FeaturesFigure  extends TheFigure implements ButtonClickHandler
{
	private ModelNode service;
	private ModelNode features;
	private ArrayList<TheFigure> allowedFigures = new ArrayList<TheFigure>();

	public FeaturesFigure(TheFigure parentFigure, ModelNode modelNode)
	{
		super(parentFigure);
		service = modelNode;
		features = modelNode.getChildByLocalName("features");
		
		ToolbarLayout layout = new ToolbarLayout();
		setLayoutManager(layout);
		setFocusColor(getNonFocusColor());
		
		allowedFigures.add(this);
		
		setFigure();
	}

	private void setFigure()
	{
		if ( features == null )
		{
			addFeatures();
		}
		
		setTable();
	}
	
	private void setTable()
	{
		addUsage(addHeading("archiveUsage", "Archive Usage"));
		addUsage(addHeading("activityUsage", "Activity Usage"));
		ModelNode objects = addHeading("objects", "COM Objects");
		if ( objects != null ) {
			addUsage(objects);
			add(new ObjectFigure(this, objects, allowedFigures, "object"));
		}
		ModelNode events = addHeading("events", "COM Events");
		if ( events != null ) {
			addUsage(events);
			add(new ObjectFigure(this, events, allowedFigures, "event"));
		}
	}

	private ModelNode addHeading(String localname, String labelname)
	{
		ModelNode node = features.getChildByLocalName(localname);
		Font font = new Font(null, "Arial", 12, SWT.BOLD);
		
		Figure figure = new Figure();
		ToolbarLayout layout = new ToolbarLayout(true);
		figure.setLayoutManager(layout);
		
		Label label = new Label(labelname);
		label.setFont(font);
		
		Button button;
		if ( node == null )
		{
			button = new Button(this, "new.png", "new" + localname, features, true);
			button.setButtonClickHandler(this);
		}
		else
		{
			button = new Button(this, "red_cross.png", "delete", node, true);
			button.setButtonClickHandler(this);
		}
		
		figure.add(label);
		figure.add(button);
		add(figure);
		return node;
	}
	
	/*
	private ModelNode addArchiveUsageHeading()
	{
		ModelNode archiveUsage = features.getChildByLocalName("archiveUsage");
		Font font = new Font(null, "Arial", 12, SWT.BOLD);
		
		Figure figure = new Figure();
		ToolbarLayout layout = new ToolbarLayout(true);
		figure.setLayoutManager(layout);
		
		Label label = new Label("Archive Usage");
		label.setFont(font);
		
		Button archiveUsageButton;
		if ( archiveUsage == null )
		{
			archiveUsageButton = new Button(this, "new.png", "newarchiveUsage", features, true);
			archiveUsageButton.setButtonClickHandler(this);
		}
		else
		{
			archiveUsageButton = new Button(this, "red_cross.png", "delete", archiveUsage, true);
			archiveUsageButton.setButtonClickHandler(this);
		}
		
		figure.add(label);
		figure.add(archiveUsageButton);
		add(figure);
		return archiveUsage;
	}*/
	
	private void addUsage(ModelNode usage)
	{
		if ( usage == null ) return;
		
		CommentFigure comment = new CommentFigure(this, usage);
		comment.setAllowedFigures(allowedFigures);
		add(comment);
	}
	
	/*
	private ModelNode addActivityUsageHeading()
	{
		ModelNode activityUsage = features.getChildByLocalName("activityUsage");
		Font font = new Font(null, "Arial", 12, SWT.BOLD);
		
		Figure figure = new Figure();
		ToolbarLayout layout = new ToolbarLayout(true);
		figure.setLayoutManager(layout);
		
		Label label = new Label("Activity Usage");
		label.setFont(font);
		
		Button activityUsageButton;
		if ( activityUsage == null )
		{
			activityUsageButton = new Button(this, "new.png", "newactivityUsage", features, true);
			activityUsageButton.setButtonClickHandler(this);
		}
		else
		{
			activityUsageButton = new Button(this, "red_cross.png", "delete", activityUsage, true);
			activityUsageButton.setButtonClickHandler(this);
		}
		
		figure.add(label);
		figure.add(activityUsageButton);
		add(figure);
		return activityUsage;
	}*/
	
	private void addFeatures()
	{
		Document doc = (Document) service.getRoot().getNode();				
		Element elem = doc.createElementNS("http://www.ccsds.org/schema/COMSchema", "com" + ":" + "features");
		features = service.appendNode(elem);
	}
	
	
	private void updateGround()
	{
		service.getMPE().printDocument((Document) service.getRoot().getNode());
	}

	@Override
	public void ButtonClicked(Button button)
	{
		if ( button.getId().startsWith("new")  )
		{
			NodeList list;
			int nnum;
			
			int numobjects = 0;
			for ( ModelNode objects : features.getChildrenArray() )
			{
				if ( objects.getLocalName().equals("objects") )
				{
					numobjects++;
				}
			}
			//there's numobjects objectss
			list = features.getChildNodes();
			nnum = 0;
			int lastobjects = 0;
			for ( int i = 0; i < list.getLength(); i++ )
			{
				nnum++;
				if ( list.item(i).getNodeType() == Node.ELEMENT_NODE )
				if ( list.item(i).getLocalName().equals("objects") ) lastobjects = nnum;
			}
			//index of last objects is nnum
			
			int numevents = 0;
			for ( ModelNode events : features.getChildrenArray() )
			{
				if ( events.getLocalName().equals("events") )
				{
					numevents++;
				}
			}
			//there's numevents eventss
			list = features.getChildNodes();
			nnum = 0;
			int lastevents = 0;
			for ( int i = 0; i < list.getLength(); i++ )
			{
				nnum++;
				if ( list.item(i).getNodeType() == Node.ELEMENT_NODE )
				if ( list.item(i).getLocalName().equals("events") ) lastevents = nnum;
			}
			//index of last events is nnum
			
			int numarchiveUsage = 0;
			for ( ModelNode archiveUsage : features.getChildrenArray() )
			{
				if ( archiveUsage.getLocalName().equals("archiveUsage") )
				{
					numarchiveUsage++;
				}
			}
			//there's numarchiveUsage archiveUsages
			list = features.getChildNodes();
			nnum = 0;
			int lastarchiveUsage = 0;
			for ( int i = 0; i < list.getLength(); i++ )
			{
				nnum++;
				if ( list.item(i).getNodeType() == Node.ELEMENT_NODE )
				if ( list.item(i).getLocalName().equals("archiveUsage") ) lastarchiveUsage = nnum;
			}
			//index of last archiveUsage is nnum
			
			Document doc = (Document) features.getRoot().getNode();
			Element elem;
			/*= doc.createElementNS(service.getNamespaceURI(), service.getNode().getPrefix() + ":" + "capabilitySet");
			elem.setAttribute("comment", "");
			elem.setAttribute("number", (new Integer(service.getMaxOf("number") + 1)).toString());*/
			
			//service.insertNode(elem, numCS + numdocumentation, getmax2(lastdocumentation, lastCS));
			//repaintFigure();
			
			if ( button.getId().equals("newarchiveUsage") )
			{
				elem = doc.createElementNS(features.getNamespaceURI(), features.getNode().getPrefix() + ":" + "archiveUsage");
				elem.setAttribute("comment", "");
				features.insertNode(elem, numobjects + numevents, getmax2(lastobjects, lastevents));
			}
			else if ( button.getId().equals("newactivityUsage") )
			{
				elem = doc.createElementNS(features.getNamespaceURI(), features.getNode().getPrefix() + ":" + "activityUsage");
				elem.setAttribute("comment", "");
				features.insertNode(elem, numobjects + numevents + numarchiveUsage, getmax3(lastobjects, lastevents, lastarchiveUsage));
			}
			else if ( button.getId().equals("newobjects") )
			{
				elem = doc.createElementNS(features.getNamespaceURI(), features.getNode().getPrefix() + ":" + "objects");
				elem.setAttribute("comment", "");
				features.insertNode(elem, numobjects, lastobjects);
			}
			else if ( button.getId().equals("newevents") )
			{
				elem = doc.createElementNS(features.getNamespaceURI(), features.getNode().getPrefix() + ":" + "events");
				elem.setAttribute("comment", "");
				features.insertNode(elem, numobjects + numevents, getmax2(lastobjects, lastevents));
			}
		}
		else if ( button.getId().equals("delete") )
		{
			features.deleteChild(button.getData());
		}
		updateGround();
		removeAll();
		setFigure();
	}
	
	int getmax2(int a, int b)
	{
		if ( a > b ) return a;
		return b;
	}
	
	int getmax3(int a, int b, int c)
	{
		return getmax2(getmax2(a, b), getmax2(b, c));
	}
	
	@Override
	public void mousePressed(MouseEvent me)
	{
		getRootFigure().closeComboBoxIfNotThese(allowedFigures);
	}
}