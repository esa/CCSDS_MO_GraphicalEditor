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

import moeditor.MultiPageEditor;
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
import org.eclipse.swt.graphics.Image;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class OperationFigure extends TheFigure implements ButtonClickHandler
{
	private ModelNode operation;
	private OperationMessagesFigure operationMessagesFigure;
	//private ArrayList<OperationErrorFigure> operationErrorsFigure = new ArrayList<OperationErrorFigure>();
	
	public OperationFigure(TheFigure parentFigure, ModelNode node)
	{
		super(parentFigure);
		operation = node;
		operationMessagesFigure = new OperationMessagesFigure(operation, this);
		
		ToolbarLayout layout = new ToolbarLayout();
		layout.setSpacing(10);
		setLayoutManager(layout);
		setFocusColor(getNonFocusColor());
		
		fillTable();
	}
	
	private void fillTable()
	{
		add(operationMessagesFigure);
		addOperationErrorsFigures();
	}
	

	private void addOperationErrorsFigures()
	{
		Font font = new Font(null, "Arial", 12, SWT.BOLD);
		Figure figure = new Figure();
		ToolbarLayout layout = new ToolbarLayout(true);
		figure.setLayoutManager(layout);
		Label label;
		
		if ( operation.getLocalName().equals("sendIP") )
		{
			label = new Label("This operation does not return any error");
			label.setFont(font);
			figure.add(label);
			add(figure);
			return;
		}
		
		addDefinitionHeader();
		addReferenceHeader();
	}
	
	private void addDefinitionHeader()
	{
		Font font = new Font(null, "Arial", 12, SWT.BOLD);
		Figure figure = new Figure();
		ToolbarLayout layout = new ToolbarLayout(true);
		figure.setLayoutManager(layout);
		Label label = new Label("Error Definitions");
		label.setFont(font);
		
		Button newDef = new Button(this, "new.png", "newErrorDefinition", operation, true);
		newDef.setButtonClickHandler(this);
		
		figure.add(label);
		figure.add(newDef);
		add(figure);
		
		addDefinitions();
	}

	private void addDefinitions()
	{
		ModelNode errors = operation.getChildByLocalName("errors");
		if ( errors == null ) return;
		
		for ( ModelNode error : errors.getChildrenArray() )
		{
			if ( !error.getLocalName().equals("error") ) continue;
				
			Figure figure = new Figure();
			ToolbarLayout layout = new ToolbarLayout(true);
			figure.setLayoutManager(layout);
			
			Label label = new Label(new Image(getDisplay(), MultiPageEditor.class.getResourceAsStream("/rightarrow.png")));
			
			Button delete = new Button(this, "red_cross.png", "deleteErr", error, true);
			delete.setButtonClickHandler(this);
			
			figure.add(label);
			figure.add(delete);
			figure.add(new Label(error.getAttribute("name")));
			add(figure);
			
			add(new ErrorFigure(this, error));
		}
	}
	
	private void addReferenceHeader()
	{
		Font font = new Font(null, "Arial", 12, SWT.BOLD);
		Figure figure = new Figure();
		ToolbarLayout layout = new ToolbarLayout(true);
		figure.setLayoutManager(layout);
		Label label = new Label("Error References");
		label.setFont(font);
		
		Button newDef = new Button(this, "new.png", "newErrorReference", operation, true);
		newDef.setButtonClickHandler(this);
		
		figure.add(label);
		figure.add(newDef);
		add(figure);
		
		addReferences();
	}

	private void addReferences()
	{
		ModelNode errors = operation.getChildByLocalName("errors");
		if ( errors == null ) return;
		
		for ( ModelNode errorRef : errors.getChildrenArray() )
		{
			if ( !errorRef.getLocalName().equals("errorRef") ) continue;
			Figure figure = new Figure();
			ToolbarLayout layout = new ToolbarLayout(true);
			figure.setLayoutManager(layout);
			
			Label label = new Label(new Image(getDisplay(), MultiPageEditor.class.getResourceAsStream("/rightarrow.png")));
			
			Button delete = new Button(this, "red_cross.png", "deleteErr", errorRef, true);
			delete.setButtonClickHandler(this);
			
			figure.add(label);
			figure.add(delete);
			add(figure);
			
			add(new ErrorRefFigure(this, errorRef));
		}
	}

	@Override
	public void focusGained()
	{
	}
	
	@Override
	public void mousePressed(MouseEvent me)
	{
		getRootFigure().closeComboBoxIfNotThis(this);
	}
	
	@Override
	public void isAboutToBeClosed()
	{
		getParentFigure().isAboutToBeClosed();
	}


	@Override
	public void ButtonClicked(Button button)
	{
		getRootFigure().closeComboBoxIfNotThis(this);
		if ( button.getId().equals("newErrorReference") )
		{
			ErrorsTable table  = new ErrorsTable(operation, this);
			getRootFigure().openComboBox(button.getLocation(), table);
		}
		else if ( button.getId().equals("newErrorDefinition") )
		{
			Document doc = (Document) operation.getRoot().getNode();
			
			ModelNode errors = operation.getChildByLocalName("errors");
			if ( errors == null )
			{
				Element elem = doc.createElementNS(operation.getNamespaceURI(), operation.getNode().getPrefix() + ":" + "errors");
				errors = operation.appendNode(elem);
			}
			
			Element elem = doc.createElementNS(operation.getNamespaceURI(), operation.getNode().getPrefix() + ":" + "error");
			String number = new Integer(errors.getMaxOf8("error", "number") + 1).toString();
			elem.setAttribute("name", "ErrorName" + number);
			elem.setAttribute("number", number);
			elem.setAttribute("comment", "");
			
			errors.appendNode(elem);
			
			removeAll();
			fillTable();
			updateGround();
		}
		else if ( button.getId().equals("deleteErr") )
		{
			ModelNode errors = operation.getChildByLocalName("errors");
			errors.deleteChild(button.getData());
			if ( errors.getChildrenArray().size() == 0 )
			{
				operation.deleteChild(errors);
			}
			removeAll();
			fillTable();
			updateGround();
		}
		else
		{
			Document doc = (Document) operation.getRoot().getNode();
			
			ModelNode errors = operation.getChildByLocalName("errors");
			if ( errors == null )
			{
				Element elem = doc.createElementNS(operation.getNamespaceURI(), operation.getNode().getPrefix() + ":" + "errors");
				errors = operation.appendNode(elem);
			}
			
			Element elem = doc.createElementNS(operation.getNamespaceURI(), operation.getNode().getPrefix() + ":" + "errorRef");
			elem.setAttribute("comment", "");
			
			ModelNode errorRef = errors.appendNode(elem);
			
			elem = doc.createElementNS(operation.getNamespaceURI(), operation.getNode().getPrefix() + ":" + "type");
			
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
			
			errorRef.appendNode(elem);
			
			removeAll();
			fillTable();
			updateGround();
		}
	}
	
	private void updateGround()
	{
		operation.getMPE().printDocument((Document) operation.getRoot().getNode());
	}
	
	@Override
	public void updateFigure()
	{
		operationMessagesFigure.updateFigure();
		removeAll();
		fillTable();
	}
}
