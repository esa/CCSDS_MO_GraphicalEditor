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
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import moeditor.draw2d.Button;
import moeditor.draw2d.ButtonClickHandler;
import moeditor.draw2d.CheckBox;
import moeditor.draw2d.EditableLabel;
import moeditor.draw2d.MultiLineEditableLabel;
import moeditor.draw2d.MultiLineEditableLabelPrinter;
import moeditor.draw2d.TableFigure;
import moeditor.draw2d.TheFigure;
import moeditor.model.ModelNode;

public class ErrorRefFigure extends TableFigure implements ButtonClickHandler, MultiLineEditableLabelPrinter
{
	private ModelNode errorRef;
	private DataTypesTable dataTypes;
	private ErrorsTable errorTypes;
	private ArrayList<TheFigure> allowedFigures = new ArrayList<TheFigure>();
	
	public ErrorRefFigure(TheFigure parentFigure, ModelNode item)
	{
		super(parentFigure, 2);
		errorRef = item;
		allowedFigures.add(parentFigure);
		setFocusColor(getNonFocusColor());
		setFigure();
	}
	
	private void setFigure()
	{
		EditableLabel comment = new EditableLabel(this, errorRef, "comment", allowedFigures, false);
		comment.setPrinter(this);
		
		addHeadingCell("Error Reference Comment");
		addLabel(comment);
		
		addHeadingCell("Referenced Error");
		ModelNode typ = errorRef.getChildByLocalName("type");
		Button newErrorType = new Button(this, "new.png", "newErrorType", typ, true);
		newErrorType.setButtonClickHandler(this);
		addCellWithButtonRight(getNonFocusColor(), newErrorType, typ.getAttribute("area") + "::" + typ.getAttribute("name"));
		
		addHeadingCell("Extra Info Type");
		Button newType = new Button(this, "new.png", "newType", errorRef, true);
		newType.setButtonClickHandler(this);
		ModelNode extraInformation = errorRef.getChildByLocalName("extraInformation");
		if ( extraInformation == null )
		{
			addCellWithButtonRight(getNonFocusColor(), newType, "Not Used");
		}
		else
		{
			Button delType = new Button(this, "red_cross.png", "delType", extraInformation, true);
			delType.setButtonClickHandler(this);
			ModelNode extraType = extraInformation.getChildByLocalName("type");
			String text = "";
			text += extraType.getAttribute("area");
			text += "::";
			text += extraType.getAttribute("name");
			if ( extraType.getAttribute("list").equals("true") )
			{
				text = "List<" + text + ">";
			}
			
			addCellWithTwoButtons(new Label(text), getNonFocusColor(), 1, delType, newType);
			addHeadingCell("Extra Info Comment");
			EditableLabel com = new EditableLabel(this, extraInformation, "comment", allowedFigures, false);
			com.setPrinter(this);
			addLabel(com);
		}
	}

	@Override
	public void ButtonClicked(Button button)
	{
		getRootFigure().closeComboBoxIfNotThis(getParentFigure());
		if ( button.getId().equals("newType") )
		{
			dataTypes = new DataTypesTable(errorRef, this, button.getData());
			dataTypes.setMessage(button.getData());
			getRootFigure().openComboBox(button.getMouseLocation(), dataTypes);
		}
		else if ( button.getId().equals("delType") )
		{
			errorRef.deleteChild(button.getData());
			removeAll();
			setFigure();
		}
		else if ( button.getId().equals("newErrorType") )
		{
			errorTypes = new ErrorsTable(errorRef, this, button.getData());
			errorTypes.setMessage(button.getData());
			getRootFigure().openComboBox(button.getMouseLocation(), errorTypes);
		}
		else
		{
			if ( button.getData().getLocalName().equals("type") )
			{
				Document doc = (Document) errorRef.getRoot().getNode();
				ModelNode typ = errorRef.getChildByLocalName("type");
				if ( typ != null ) errorRef.deleteChild(typ);
				
				Element elem = doc.createElementNS(errorRef.getNamespaceURI(), errorRef.getNode().getPrefix() + ":" + "type");
				
				int i = 0;
				for ( String item : button.getId().split("::") )
				{
					switch ( i )
					{
					case 0:
						elem.setAttribute("area", item);
						break;
					case 1:
						if ( !item.isEmpty() ) elem.setAttribute("service", item);
						break;
					case 2:
						elem.setAttribute("name", item);
						break;
					default:
						System.err.println("WTF?!");
					}
					i++;
				}
				
				elem.setAttribute("list", "false");
					
				errorRef.insertNode(elem, 0, 0);
				
				removeAll();
				setFigure();
			}
			else if ( button.getData().getLocalName().equals("errorRef") )
			{
				Document doc = (Document) errorRef.getRoot().getNode();
				
				ModelNode extraInformation = errorRef.getChildByLocalName("extraInformation");
				
				if ( extraInformation == null )
				{
					Element el = doc.createElementNS(errorRef.getNamespaceURI(), errorRef.getNode().getPrefix() + ":" + "extraInformation");
					el.setAttribute("comment", "");
					extraInformation = errorRef.appendNode(el);
				}
				
				ModelNode typ = extraInformation.getChildByLocalName("type");
				if ( typ != null ) extraInformation.deleteChild(typ);

				Element elem = doc.createElementNS(errorRef.getNamespaceURI(), errorRef.getNode().getPrefix() + ":" + "type");
				
				int i = 0;
				for ( String item : button.getId().split("::") )
				{
					switch ( i )
					{
					case 0:
						elem.setAttribute("area", item);
						break;
					case 1:
						if ( !item.isEmpty() ) elem.setAttribute("service", item);
						break;
					case 2:
						elem.setAttribute("name", item);
						break;
					default:
						System.err.println("WTF?!");
					}
					i++;
				}
				
				elem.setAttribute("list", ((CheckBox) button.getLink()).isChecked() ? "true" : "false");
					
				extraInformation.appendNode(elem);
				
				removeAll();
				setFigure();
			}
		}
		errorRef.getMPE().printDocument((Document) errorRef.getRoot().getNode());
	}

	@Override
	public void print(MultiLineEditableLabel label, String text)
	{
		label.getData().setAttribute(label.getAttribute(), text);
		errorRef.getMPE().printDocument((Document) errorRef.getRoot().getNode());
	}
}