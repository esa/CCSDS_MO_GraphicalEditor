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

import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MouseEvent;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import moeditor.InternalResources;
import moeditor.MultiPageEditor;
import moeditor.draw2d.Arrow;
import moeditor.draw2d.Button;
import moeditor.draw2d.ButtonClickHandler;
import moeditor.draw2d.EditableLabel;
import moeditor.draw2d.MultiLineEditableLabel;
import moeditor.draw2d.MultiLineEditableLabelPrinter;
import moeditor.draw2d.TableFigure;
import moeditor.draw2d.TheFigure;
import moeditor.model.ModelNode;

public class FieldFigure extends TableFigure implements MultiLineEditableLabelPrinter, ButtonClickHandler
{
	private ModelNode field;
	private ModelNode type;
	//private DataTypesTable dataTypes;
	private ArrayList<TheFigure> allowedFigures = new ArrayList<TheFigure>();
	private int num;
	private String message;
	private OperationMessagesFigure omf;
	
	public FieldFigure(TheFigure parentFigure, ModelNode item, int num, OperationMessagesFigure omf, ArrayList<TheFigure> allowedFigures, String messagename)
	{
		super(parentFigure, 4);
		field = item;
		this.allowedFigures.add(this);
		setAllowedFigures(allowedFigures);
		this.num = num;
		this.omf = omf;
		this.message = messagename;
		setFocusColor(getNonFocusColor());
		setFigure();
	}
	
	private void setFigure()
	{
		Label fake;
		Arrow arrow = new Arrow(this);
		arrow.setProtectedCombos(allowedFigures);
		
		if ( field.getLocalName().equals("field") )
		{
			type = field.getChildByLocalName("type");
			
			Button button = new Button(this, "red_cross.png", "delete", field, true);
			button.setButtonClickHandler(this);
			addCellWithButtonBottom(getHeadingColor(), button, "Field", 1, 2);
			
			addHeadingCell("Name");
			EditableLabel name = new EditableLabel(this, field, "name", allowedFigures);
			name.setPrinter(this);
			addLabel(name);
			
			fake = new Label();
			add(fake);
			
			addHeadingCell("Comment");
			EditableLabel comment = new EditableLabel(this, field, "comment", allowedFigures, false);
			comment.setPrinter(this);
			addLabel(comment);
			
			fake = new Label();
			add(fake);
			
			addHeadingCell("Type");
			
			ModelNode type = field.getChildByLocalName("type");
			String area = type.getAttribute("area");
			String typeName = type.getAttribute("name");
			String temp = area + "::" + typeName;
			addCell(type.getAttribute("list").equals("true") ? "List<" + temp  + ">" : temp, 2);
		}
		else if ( field.getLocalName().equals("type") )
		{
			type = field;
			
			Button button = new Button(this, "new.png", "newField", field, true);
			button.setButtonClickHandler(this);
			addHeadingCell("Field");
			
			addCellWithButtonRight(getNonFocusColor(), button, "Not Used", 2);
			
			fake = new Label();
			add(fake);
			
			addHeadingCell("Type");
			
			String area = field.getAttribute("area");
			String typeName = field.getAttribute("name");
			String temp = area + "::" + typeName;
			addCell(field.getAttribute("list").equals("true") ? "List<" + temp  + ">" : temp, 2);
		}
		Node typ = getTypeNode(type);
		if ( typ != null )
		{
			if ( typ.getLocalName().equals("composite") )
			{
				arrow.setFigure(new ROCompositeFigure(typ, arrow));
			}
			else if ( typ.getLocalName().equals("enumeration") )
			{
				arrow.setFigure(new ROEnumerationFigure(typ, arrow));
			}
			else if ( typ.getLocalName().equals("attribute") )
			{
				arrow.setFigure(new ROAttributeFigure(typ, arrow));
			}
			else if ( typ.getLocalName().equals("fundamental") )
			{
				arrow.setFigure(new ROFundamentalFigure(typ, arrow));
			}
			else
			{
				arrow.setFigure(new WarnFigure(arrow, "Type " + type.getAttribute("area") + "::" + type.getAttribute("service") + "::" + type.getAttribute("name") + " was ignored."));
			}
		}
		else
		{
			arrow.setFigure(new WarnFigure(arrow, "Type " + type.getAttribute("area") + "::" + type.getAttribute("service") + "::" + type.getAttribute("name") + " was not found."));
		}
		add(arrow);
	}
	
	private Node getTypeNode(ModelNode type)
	{
		Node retval = null;
		ArrayList<Document> doc = new ArrayList<Document>();
		for ( Document document : type.getMPE().getCheckedResources() )
		{
			doc.add(document);
		}
		type.getMPE();
		for ( InternalResources ir : field.getMPE().malcom )
		{
			doc.add(ir.getDocument());
		}
		doc.add((Document) type.getRoot().getNode());
		for ( Document document : doc )
		{
			type.getMPE();
			//NodeList dataTypes = document.getElementsByTagName("mal:dataTypes");
			retval = MultiPageEditor.isLocalType(document, (Element) type.getNode());
			if ( retval != null ) return retval;
		}
		return retval;
	}

	public void setAllowedFigures(ArrayList<TheFigure> allowedFigures)
	{
		for ( TheFigure fig : allowedFigures )
		{
			this.allowedFigures.add(fig);
		}
	}
	
	@Override
	public void isAboutToBeClosed()
	{
		getParentFigure().isAboutToBeClosed();
	}

	@Override
	public void print(MultiLineEditableLabel label, String text)
	{
		label.getData().setAttribute(label.getAttribute(), text);
		field.getMPE().printDocument((Document) field.getRoot().getNode());
		omf.updateByChild(num, message, getLocation());
	}

	@Override
	public void ButtonClicked(Button button)
	{
		if ( button.getId().equals("delete") )
		{
			ModelNode parent = field.getParent();
			field = parent.replaceNode(field, type.getNode());
			type = field;
		}
		else if ( button.getId().equals("newField") )
		{
			ModelNode typ = field;
			Node typNode = typ.getNode();
			
			Document doc = (Document) field.getRoot().getNode();
			Element elem = doc.createElementNS(field.getNamespaceURI(), field.getNode().getPrefix() + ":" + "field");
			elem.setAttribute("name", "FieldName");
			elem.setAttribute("comment", "");
			
			ModelNode parent = field.getParent();
			
			field = parent.replaceNode(typ, elem);
			type = field.appendNode(typNode);
		}
		removeAll();
		setFigure();
		field.getMPE().printDocument((Document) field.getRoot().getNode());
		omf.updateByChild(num, message, getLocation());
	}
	
	@Override
	public void mousePressed(MouseEvent me)
	{
		getRootFigure().closeComboBoxIfNotThese(allowedFigures);
	}
}
