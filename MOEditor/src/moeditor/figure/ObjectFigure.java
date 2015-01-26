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

import moeditor.MultiPageEditor;
import moeditor.draw2d.Arrow;
import moeditor.draw2d.Button;
import moeditor.draw2d.ButtonClickHandler;
import moeditor.draw2d.CheckBox;
import moeditor.draw2d.EditableLabel;
import moeditor.draw2d.MultiLineEditableLabel;
import moeditor.draw2d.MultiLineEditableLabelPrinter;
import moeditor.draw2d.TableFigure;
import moeditor.draw2d.TheFigure;
import moeditor.model.ModelNode;

import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MouseEvent;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ObjectFigure extends TableFigure implements ButtonClickHandler, MultiLineEditableLabelPrinter
{
	private ModelNode objects;
	private ArrayList<TheFigure> allowedFigures = new ArrayList<TheFigure>();
	private DataTypesTable dataTypes = null;
	private ObjectsTable objectsTypes = null;
	private String objectName;
	private String Object = "ERROR";
	
	public ObjectFigure(TheFigure parentFigure, ModelNode objects, ArrayList<TheFigure> allowedFigures, String objectName)
	{
		super(parentFigure, 7);
		setFocusColor(getNonFocusColor());
		this.objects = objects;
		this.allowedFigures = allowedFigures;
		this.objectName = objectName;
		
		if ( objectName.equals("object") ) Object = "object";
		else if ( objectName.equals("event") ) Object = "event";
		
		fillTable();
	}

	private void fillTable()
	{
		setHeading();
		setObjectsTable();
	}
	
	private void setObjectsTable()
	{
		if ( getObjects() == null )
		{
			return;
		}
		for ( ModelNode object : getObjects().getChildrenArray() )
		{
			Button cross = new Button(this, "red_cross.png", "Delete", object, true);
			cross.setButtonClickHandler(this);
			EditableLabel name = new EditableLabel(this, object, "name", allowedFigures);
			name.setPrinter(this);
			EditableLabel number = new EditableLabel(this, object, "number", allowedFigures);
			number.setPrinter(this);
			
			add(cross);
			addLabel(name);
			addLabel(number);
			
			ModelNode objectType = object.getChildByLocalName("objectType");
			if ( objectType != null )
			{
				ModelNode type = objectType.getChildByLocalName("type");
				if ( type != null )
				{
					Button tdelete = new Button(this, "red_cross.png", "Delete", objectType, true);
					tdelete.setButtonClickHandler(this);;
					Button tnew = new Button(this, "new.png", "New objectType", type, true);
					tnew.setButtonClickHandler(this);
					
					addCellWithTwoButtons(new Label(type.getAttribute("name")), getEditableColor(), 1, tdelete, tnew);
				}
				else
				{
					Button button = new Button(this, "new.png", "New objectType", objectType, true);
					button.setButtonClickHandler(this);
					addCellWithButtonRight(getEditableColor(), button, "Not used");
				}
			}
			else
			{
				Button button = new Button(this, "new.png", "New objectType", object, true);
				button.setButtonClickHandler(this);
				addCellWithButtonRight(getEditableColor(), button, "Not used");
			}
			
			addObjectPointer(object, "relatedObject");
			addObjectPointer(object, "sourceObject");
			
			Arrow arrow = new Arrow(this);
			ObjectCommentsFigure ocf = new ObjectCommentsFigure(arrow, object, objectName);
			ocf.setAllowedFigures(allowedFigures);
			arrow.setFigure(ocf);
			arrow.setProtectedCombos(allowedFigures);
			add(arrow);
		}
	}
	
	private void addObjectPointer(ModelNode object, String localname)
	{
		ModelNode relatedObject = object.getChildByLocalName(localname);
		if ( relatedObject != null )
		{
			ModelNode optionalObjectType = relatedObject.getChildByLocalName("objectType");
			if ( optionalObjectType != null )
			{
				EditableLabel tnumber = new EditableLabel(this, optionalObjectType, "number", allowedFigures);
				tnumber.setPrinter(this);
				
				Button tdelete = new Button(this, "red_cross.png", "Delete", optionalObjectType, true);
				tdelete.setButtonClickHandler(this);;
				Button tnew = new Button(this, "new.png", "NewObject", optionalObjectType, true, localname);
				tnew.setButtonClickHandler(this);
				
				addLabelWithTwoButtons(tnumber, getEditableColor(), 1, tdelete, tnew);
			}
			else
			{
				Button button = new Button(this, "new.png", "NewObject", relatedObject, true, localname);
				button.setButtonClickHandler(this);
				addCellWithButtonRight(getEditableColor(), button, "Not used");
			}
		}
		else
		{
			//Button button = new Button(this, "new.png", "New " + localname, object, true);
			Button button = new Button(this, "new.png", "NewObject", object, true, localname);
			button.setButtonClickHandler(this);
			addCellWithButtonRight(getEditableColor(), button, "Not used");
		}
	}

	private void setHeading()
	{
		Label fake;
		fake = new Label();
		add(fake);
		
		Button button = new Button(this, "new.png", "New object", objects, true);
		button.setButtonClickHandler(this);
		
		String Object = "ERROR";
		if ( objectName.equals("object") ) Object = "Object";
		else if ( objectName.equals("event") ) Object = "Event";
		
		addCellWithButtonRight(getHeadingColor(), button, Object + " Name");
		addHeadingCell(Object + "\nNumber");
		addHeadingCell(Object + " Body Type");
		addHeadingCell("Related points to");
		addHeadingCell("Source Object");
		
		fake = new Label();
		add(fake);
	}

	@Override
	public void ButtonClicked(Button button)
	{
		getRootFigure().closeComboBoxIfNotThese(allowedFigures);
		if ( button.getId().equals("New object") )
		{
			addObject();
		}
		else if ( button.getId().equals("Delete") )
		{
			button.getData().getParent().deleteChild(button.getData());
			updateTable();
		}
		else if ( button.getId().equals("New objectType") )
		{
			objectsTypes = null;
			dataTypes = new DataTypesTable(objects, this);
			dataTypes.setMessage(button.getData());
			getRootFigure().openComboBox(button.getMouseLocation(), dataTypes);
		}
		else if ( button.getId().equals("NewObject") )
		{
			dataTypes = null;
			objectsTypes = new ObjectsTable(objects, this, button.getInfo());
			objectsTypes.setMessage(button.getData());
			getRootFigure().openComboBox(button.getMouseLocation(), objectsTypes);
		}
		else {
			if ( dataTypes != null ) addDataType(dataTypes.getMessage(), button.getId(), ((CheckBox) button.getLink()).isChecked());
			if ( objectsTypes != null ) 
			{
				//if ( button.getInfo().equals("relatedObject") )
				//{
					addObjectType(objectsTypes.getMessage(), button.getId(), button.getInfo());
				//}
				//if ( button.getInfo().equals("sourceObject") )
				//{
				//	addObjectType(objectsTypes.getMessage(), button.getId());
				//}

			}
			updateTable();
		}
	}
	
	private void addObjectType(ModelNode message, String text, String object)
	{
		if ( message.getLocalName().equals(Object) ) message = addOptionalObjectType(addObjectChild(message, object));//addRelatedObject(message));
		else if ( message.getLocalName().equals(object/*"relatedObject"*/) ) message = addOptionalObjectType(message);
		
		int i = 0;
		for ( String item : text.split("::") )
		{
			switch ( i )
			{
			case 0:
				message.setAttribute("area", item);
				break;
			case 1:
				if ( !item.isEmpty() ) message.setAttribute("service", item);
				else message.delAttribute("service");
				break;
			case 2:
				break;
			case 3:
				message.setAttribute("number", item);
				break;
			default:
				MultiPageEditor.getRedStream().println("ObjectFigure.java: addObjectType: object = " + object + ": The ID is invalid: " + text);
			}
			i++;
		}
	}

	private void addDataType(ModelNode message, String text, boolean list)
	{	
		if ( message.getLocalName().equals(Object) ) message = addType(addObjectChild(message, "objectType"));
		else if ( message.getLocalName().equals("objectType") ) message = addType(message);
		
		int i = 0;
		for ( String item : text.split("::") )
		{
			switch ( i )
			{
			case 0:
				message.setAttribute("area", item);
				break;
			case 1:
				if ( !item.isEmpty() ) message.setAttribute("service", item);
				else message.delAttribute("service");
				break;
			case 2:
				message.setAttribute("name", item);
				break;
			default:
				MultiPageEditor.getRedStream().println("ObjectFigure.java: addDataType: The ID is invalid: " + text);
			}
			i++;
		}
	}

	private ModelNode addOptionalObjectType(ModelNode relatedObject)
	{
		Document doc = (Document) objects.getRoot().getNode();
		ModelNode service = objects.getParent().getParent();
		Element elem = doc.createElementNS("http://www.ccsds.org/schema/COMSchema", "com:objectType");
		elem.setAttribute("area", service.getParent().getAttribute("name"));
		elem.setAttribute("number", "1");
		elem.setAttribute("service", service.getAttribute("name"));
		return relatedObject.appendNode(elem);
	}

	//private ModelNode addRelatedObject(ModelNode object)
	//{
		/*
		Document doc = (Document) objects.getRoot().getNode();
		Element elem = doc.createElementNS("http://www.ccsds.org/schema/COMSchema", "com:relatedObject");
		elem.setAttribute("comment", "");
		return object.appendNode(elem);
		*/
		//return addObjectChild(object, "relatedObject");
	//}

	private ModelNode addType(ModelNode objectType)
	{
		Document doc = (Document) objects.getRoot().getNode();
		ModelNode service = objects.getParent().getParent();
		Element elem = doc.createElementNS(service.getNamespaceURI(), "mal:type");
		elem.setAttribute("area", service.getParent().getAttribute("name"));
		elem.setAttribute("list", "false");
		elem.setAttribute("name", "typeName");
		elem.setAttribute("service", service.getAttribute("name"));
		return objectType.appendNode(elem);
	}

	//private ModelNode addObjectType(ModelNode object)
	//{
		/*
		Document doc = (Document) objects.getRoot().getNode();
		Element elem = doc.createElementNS("http://www.ccsds.org/schema/COMSchema", "com:objectType");
		return object.insertNode(elem, 0, 0);
		*/
		//return addObjectChild(object, "objectType");
	//}
	
	public ModelNode addObjectChild(ModelNode object, String localname)
	{
		NodeList list;
		int nnum;
		
		int numobjectType = 0;
		for ( ModelNode objectType : object.getChildrenArray() )
		{
			if ( objectType.getLocalName().equals("objectType") )
			{
				numobjectType++;
			}
		}
		//there's numobjectType objectTypes
		list = object.getChildNodes();
		nnum = 0;
		int lastobjectType = 0;
		for ( int i = 0; i < list.getLength(); i++ )
		{
			nnum++;
			if ( list.item(i).getNodeType() == Node.ELEMENT_NODE )
			if ( list.item(i).getLocalName().equals("objectType") ) lastobjectType = nnum;
		}
		//index of last objectType is nnum
		
		int numrelatedObject = 0;
		for ( ModelNode relatedObject : object.getChildrenArray() )
		{
			if ( relatedObject.getLocalName().equals("relatedObject") )
			{
				numrelatedObject++;
			}
		}
		//there's numrelatedObject relatedObjects
		list = object.getChildNodes();
		nnum = 0;
		int lastrelatedObject = 0;
		for ( int i = 0; i < list.getLength(); i++ )
		{
			nnum++;
			if ( list.item(i).getNodeType() == Node.ELEMENT_NODE )
			if ( list.item(i).getLocalName().equals("relatedObject") ) lastrelatedObject = nnum;
		}
		//index of last relatedObject is nnum
		
		Document doc = (Document) objects.getRoot().getNode();
		Element elem = doc.createElementNS("http://www.ccsds.org/schema/COMSchema", "com:" + localname);
		
		int num = 0;
		int last = 0;
		if ( localname.equals("relatedObject") )
		{
			num = numobjectType;
			last = lastobjectType;
			elem.setAttribute("comment", "");
		}
		else if ( localname.equals("sourceObject") )
		{
			num = getmax2(numobjectType, numrelatedObject);
			last = getmax2(lastobjectType, lastrelatedObject);
			elem.setAttribute("comment", "");
		}
		
		return object.insertNode(elem, num, last);
	}
	
	int getmax2(int a, int b)
	{
		if ( a > b ) return a;
		return b;
	}

	private void addObject()
	{
		Document doc = (Document) objects.getRoot().getNode();
		Element elem = doc.createElementNS("http://www.ccsds.org/schema/COMSchema", "com:" + Object);
		String number = new Integer(objects.getParent().getMaxOf2("number") + 1).toString();
		elem.setAttribute("name", "objectName" + number);
		elem.setAttribute("number", number);
		elem.setAttribute("comment", "");
		ModelNode object = objects.appendNode(elem);
		addObjectChild(object, "relatedObject");
		addObjectChild(object, "sourceObject");
		updateTable();
	}
	
	private void updateTable()
	{
		removeAll();
		fillTable();
		updateGround();
	}
	
	private void updateGround()
	{
		objects.getMPE().printDocument((Document) objects.getRoot().getNode());
	}

	public ModelNode getObjects()
	{
		return objects;
	}
	
	@Override
	public void print(MultiLineEditableLabel label, String text)
	{
		label.getData().setAttribute(label.getAttribute(), text);
		updateGround();
	}
	
	@Override
	public void mousePressed(MouseEvent me)
	{
		getRootFigure().closeComboBoxIfNotThese(allowedFigures);
	}
}
