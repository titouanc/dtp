/**
 * 
 */
package be.ac.ulb.infof307.g03.controllers;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.SQLException;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import be.ac.ulb.infof307.g03.models.*;
import be.ac.ulb.infof307.g03.utils.Log;
import be.ac.ulb.infof307.g03.views.ObjectTreeView;

/**
 * @author pierre
 *
 */
public class ObjectTreeController implements TreeSelectionListener, MouseListener, KeyListener, Observer {
	private ObjectTreeView _view;
	private GeometryDAO _dao;
	private Project _project;
	private String _currentEditionMode;
	
	static private final String _WORLDMODE = "world";
	static private final String _OBJECTMODE = "object";
	
	/**
	 * @param project Project object from model
	 */
	public ObjectTreeController(Project project) {
		_project = project;
		try {
			_dao = project.getGeometryDAO();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		_project.addObserver(this);
		
	}
	
	/**
	 * @author fhennecker
	 * Run the ObjectTree GUI
	 */
	public void run(){
		initView(_project);
		_currentEditionMode = _project.config("edition.mode");
		if (_currentEditionMode.equals(""))
			_project.config("edition.mode",_WORLDMODE);
		else 
			updateEditionMode();
	}
	
	/**
	 * This method initiate the view
	 * @param project The main project
	 */
	public void initView(Project project){
		_view = new ObjectTreeView(this, project);
	}
	
	/**
	 * @return The controller's view
	 */
	public ObjectTreeView getView(){
		return _view;
	}
	
	/**
	 * 
	 * @param mode
	 */
	private void updateEditionMode(String mode) {
		if (mode!=_currentEditionMode) {
			_currentEditionMode = mode;
			updateEditionMode();
		}
	}
	
	public void updateEditionMode() {
		if (_currentEditionMode.equals(_WORLDMODE)) {
			System.out.println("[DEBUG] ObjectTree switched to world edition mode.");
			_view.clearTree();
			try {
				_view.createTree();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (_currentEditionMode.equals(_OBJECTMODE)) {
			System.out.println("[DEBUG] ObjectTree switched to object edition mode.");
			_view.clearTree();
		}
		((DefaultTreeModel) _view.getModel()).reload();
	}

	/**
	 * @param object 
	 * @param name 
	 */
	public void renameNode(Object object, String name){
		if (object instanceof Room) {
			Room grp = (Room) object;
			grp.setName(name);
			try {
				_dao.update(grp);
				_dao.notifyObservers(object);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Delete a Geometric node
	 * @param object
	 */
	public void deleteNode(Object object){
		if (object instanceof Geometric){
			Geometric item = (Geometric) object;
			try {
				Log.info("DELETE %s", item.toString());
				_dao.delete(item);
				_dao.notifyObservers(item);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Unset the select flag of a Meshable element
	 * @param element
	 */
	public void deselectElement(Object element) {
		if (element instanceof Meshable){
			Meshable meshable = (Meshable) element;
			Log.debug("Unselect %s", meshable.getUID());
			meshable.deselect();
			try {
				for (Point p : meshable.getPoints()){
					p.deselect();
					_dao.update(p);
				}
				_dao.update(meshable);
				_dao.notifyObservers(meshable);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else if (element instanceof Room){
			Room room = (Room) element;
			try {
				for (Point p : room.getPoints()){
					_dao.refresh(p);
					p.deselect();
					_dao.update(p);
				}
				_dao.notifyObservers();
		
			} catch (SQLException err){
				// TODO Auto-generated catch block
				err.printStackTrace();
			}
		}
	}

	/**
	 * Set the select flag of a Meshable element
	 * @param element
	 */
	public void selectElement(Object element) {
		if (element instanceof Floor){
			Floor current = (Floor) element;
			_project.config("floor.current", current.getUID());
		} else if (element instanceof Meshable){
			Meshable meshable = (Meshable) element;
			Log.debug("Select %s", meshable.getUID());
			try {
				_dao.refresh(meshable);
				meshable.select();
				for (Point p : meshable.getPoints()){
					p.select();
					_dao.update(p);
				}
				_dao.update(meshable);
				_dao.notifyObservers(meshable);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (element instanceof Room){
			try {
				Room room = (Room) element;
				for (Point p : room.getPoints()){
					_dao.refresh(p);
					p.select();
					_dao.update(p);
				}
				_dao.notifyObservers();
		
			} catch (SQLException err){
				// TODO Auto-generated catch block
				err.printStackTrace();
			}
		}
	}

	/**
	 * Unset the visible flag of a Meshable item
	 * @param meshable
	 */
	public void hideGrouped(Meshable meshable){
		meshable.hide();
		try {
			_dao.update(meshable);
			_dao.notifyObservers();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Set the visible flag of a Meshable item
	 * @param meshable
	 */
	public void showGrouped(Meshable meshable){
		meshable.show();
		try {
			_dao.update(meshable);
			_dao.notifyObservers();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void setWidth(Wall wall, String userInput){
		double width = wall.getWidth();
		try {
			width = Double.parseDouble(userInput);
		} catch (NumberFormatException err){
			JOptionPane.showMessageDialog(_view, "Invalid width " + err.getMessage());
			return;
		}
		if (width <= 0){
			JOptionPane.showMessageDialog(_view, "Cannot set a non strictly positive width !");
			return;
		}
		wall.setWidth(width);
		try {
			_dao.update(wall);
			_dao.notifyObservers();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Handles the input of a user who entered a height for a floor, changes the height
	 * if the input is valid.
	 * @param floor the floor that will be updated
	 * @param userInput
	 */
	public void setHeight(Floor floor, String userInput){
		double height = floor.getHeight();
		try{
			height = Double.parseDouble(userInput);
		} catch (NumberFormatException err){
			JOptionPane.showMessageDialog(_view, "Invalid height "+ err.getMessage());
			return;
		}
		if (height <= 0){
			JOptionPane.showMessageDialog(_view, "A floor has to have a positive height!");
			return;
		}
		floor.setHeight(height);
		try{
			_dao.update(floor);
			_dao.notifyObservers();
		} catch (SQLException e){
			e.printStackTrace();
		}
	}
	
	@Override
	public void valueChanged(TreeSelectionEvent event) {
		TreePath path = event.getOldLeadSelectionPath();
		if (path != null)
			deselectElement(_view.getGeometric(path));
		path = event.getNewLeadSelectionPath();
		if (path != null)
			selectElement(_view.getGeometric(path));
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int keyCode = e.getKeyCode();
		if (keyCode==KeyEvent.VK_BACK_SPACE) {
			DefaultMutableTreeNode clickedNode = (DefaultMutableTreeNode) _view.getLastSelectedPathComponent();
			Geometric clickedItem = (Geometric) clickedNode.getUserObject();
			deselectElement(clickedItem);
			deleteNode(clickedItem);
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// if right click
		if (SwingUtilities.isRightMouseButton(e)) {
			// select the closest element near the click on the tree
			int row = _view.getClosestRowForLocation(e.getX(), e.getY());
			_view.setSelectionRow(row);
			DefaultMutableTreeNode clickedNode = (DefaultMutableTreeNode) _view.getLastSelectedPathComponent();
			Geometric clickedItem = (Geometric) clickedNode.getUserObject();
			JPopupMenu menuForItem = _view.createPopupMenu(clickedItem);
			if (menuForItem != null) 
				menuForItem.show(e.getComponent(), e.getX(), e.getY());
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(Observable o, Object arg) {
		if (o instanceof Project) {
			Config config = (Config) arg;
			if (config.getName().equals("edition.mode")) {
				updateEditionMode(config.getValue());
			}
		}		
	}
	
}
