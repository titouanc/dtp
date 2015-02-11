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
	private ObjectTreeView view;
	private MasterDAO daoFactory;
	private Project project;
	private String currentEditionMode;
	
	static private final String WORLDMODE = "world";
	static private final String OBJECTMODE = "object";
	
	/**
	 * @param project Project object from model
	 */
	public ObjectTreeController(Project project) {
		this.project = project;
		try {
			this.daoFactory = project.getGeometryDAO();
		} catch (SQLException ex) {
			Log.exception(ex);
		}
		
		this.project.addObserver(this);
		
	}
	
	/**
	 * @author fhennecker
	 * Run the ObjectTree GUI
	 */
	public void run(){
		initView(this.project);
		updateEditionMode(this.project.config("edition.mode"));
	}
	
	/**
	 * This method initiate the view
	 * @param project The main project
	 */
	public void initView(Project project){
		this.view = new ObjectTreeView(this, project);
	}
	
	/**
	 * @return The controller's view
	 */
	public ObjectTreeView getView(){
		return this.view;
	}
	
	/**
	 * 
	 * @param mode
	 */
	private void updateEditionMode(String mode) {
		if (mode!=this.currentEditionMode) {
			this.currentEditionMode = mode;
			updateEditionMode();
		}
	}
	
	public void updateEditionMode() {
		if (this.currentEditionMode.equals(WORLDMODE)) {
			System.out.println("[DEBUG] ObjectTree switched to world edition mode.");
			this.view.clearTree();
			try {
				this.view.createTree();
			} catch (SQLException ex) {
				Log.exception(ex);
			}
		} else if (this.currentEditionMode.equals(OBJECTMODE)) {
			System.out.println("[DEBUG] ObjectTree switched to object edition mode.");
			this.view.clearTree();
			this.view.createObjectTree();
		}
		((DefaultTreeModel) this.view.getModel()).reload();
	}

	/**
	 * @param object 
	 * @param name 
	 */
	public void renameNode(Object object, String name){
		if (object instanceof Room) {
			Room room = (Room) object;
			room.setName(name);
			try {
				this.daoFactory.getDao(Room.class).modify(room);
				this.daoFactory.notifyObservers(object);
			} catch (SQLException ex) {
				Log.exception(ex);
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
				this.daoFactory.getDao(item.getClass()).remove(item);
				this.daoFactory.notifyObservers();
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
		if (element instanceof Area){
			Area area = (Area) element;
			Log.debug("Unselect %s", area.getUID());
			area.deselect();
			try {
				GeometricDAO<Point> pointDao = this.daoFactory.getDao(Point.class);
				for (Point p : area.getPoints()){
					p.deselect();
					pointDao.modify(p);
				}
				this.daoFactory.getDao(area.getClass()).modify(area);
				this.daoFactory.notifyObservers();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else if (element instanceof Room){
			Room room = (Room) element;
			try {
				GeometricDAO<Point> pointDao = this.daoFactory.getDao(Point.class);
				for (Point p : room.getPoints()){
					pointDao.refresh(p);
					p.deselect();
					pointDao.modify(p);
				}
				this.daoFactory.notifyObservers();
		
			} catch (SQLException err){
				// TODO Auto-generated catch block
				err.printStackTrace();
			}
		} else if (element instanceof Meshable) {
			Meshable meshable = (Meshable) element;
			meshable.deselect();
			try {
				GeometricDAO<? extends Meshable> meshDao = this.daoFactory.getDao(meshable.getClass());
				meshDao.modify(meshable);
				this.daoFactory.notifyObservers();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
			this.project.config("floor.current", current.getUID());
			System.out.println("CURRENT FLOOR "+this.project.config("floor.current", current.getUID()));
		} else if (element instanceof Area){
			Area area = (Area) element;
			Log.debug("Select %s", area.getUID());
			try {
				GeometricDAO<Point> pointDao = this.daoFactory.getDao(Point.class);
				GeometricDAO areaDao = this.daoFactory.getDao(area.getClass());
				areaDao.refresh(area);
				area.select();
				for (Point p : area.getPoints()){
					p.select();
					pointDao.modify(p);
				}
				areaDao.modify(area);
				this.daoFactory.notifyObservers();
			} catch (SQLException ex) {
				Log.exception(ex);
			}
		} else if (element instanceof Room){
			try {
				GeometricDAO<Point> pointDao = this.daoFactory.getDao(Point.class);
				Room room = (Room) element;
				for (Point p : room.getPoints()){
					pointDao.refresh(p);
					p.select();
					pointDao.modify(p);
				}
				this.daoFactory.notifyObservers();
		
			} catch (SQLException err){
				Log.exception(err);
			}
		} else if (element instanceof Meshable) {
			Meshable meshable = (Meshable) element;
			meshable.select();
			try {
				this.daoFactory.getDao(meshable.getClass()).modify(meshable);
				this.daoFactory.notifyObservers(meshable);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
			this.daoFactory.getDao(meshable.getClass()).modify(meshable);
			this.daoFactory.notifyObservers();
		} catch (SQLException ex) {
			Log.exception(ex);
		}
	}
	
	/**
	 * Set the visible flag of a Meshable item
	 * @param meshable
	 */
	public void showGrouped(Meshable meshable){
		meshable.show();
		try {
			this.daoFactory.getDao(meshable.getClass()).modify(meshable);
			this.daoFactory.notifyObservers();
		} catch (SQLException ex) {
			Log.exception(ex);
		}
	}

	public void setWidth(Wall wall, String userInput){
		double width = wall.getWidth();
		try {
			width = Double.parseDouble(userInput);
		} catch (NumberFormatException err){
			JOptionPane.showMessageDialog(this.view, "Invalid width " + err.getMessage());
			return;
		}
		if (width <= 0){
			JOptionPane.showMessageDialog(this.view, "Cannot set a non strictly positive width !");
			return;
		}
		wall.setWidth(width);
		try {
			this.daoFactory.getDao(Wall.class).modify(wall);
			this.daoFactory.notifyObservers();
		} catch (SQLException ex) {
			Log.exception(ex);
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
			JOptionPane.showMessageDialog(this.view, "Invalid height "+ err.getMessage());
			return;
		}
		if (height <= 0){
			JOptionPane.showMessageDialog(this.view, "A floor has to have a positive height!");
			return;
		}
		floor.setHeight(height);
		try{
			this.daoFactory.getDao(Floor.class).modify(floor);
			this.daoFactory.notifyObservers();
		} catch (SQLException ex){
			Log.exception(ex);
		}
	}
	
	@Override
	public void valueChanged(TreeSelectionEvent event) {
		TreePath path = event.getOldLeadSelectionPath();
		if (path != null)
			deselectElement(this.view.getGeometric(path));
		path = event.getNewLeadSelectionPath();
		if (path != null)
			selectElement(this.view.getGeometric(path));
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int keyCode = e.getKeyCode();
		if (keyCode==KeyEvent.VK_BACK_SPACE) {
			DefaultMutableTreeNode clickedNode = (DefaultMutableTreeNode) this.view.getLastSelectedPathComponent();
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
			int row = this.view.getClosestRowForLocation(e.getX(), e.getY());
			this.view.setSelectionRow(row);
			DefaultMutableTreeNode clickedNode = (DefaultMutableTreeNode) this.view.getLastSelectedPathComponent();
			if (clickedNode == null){
				Log.error("Right-clicked null node");
			} else {
				Geometric clickedItem = (Geometric) clickedNode.getUserObject();
				JPopupMenu menuForItem = this.view.createPopupMenu(clickedItem);
				if (menuForItem != null) 
					menuForItem.show(e.getComponent(), e.getX(), e.getY());
			}
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
			} else if (config.getName().equals("entity.current")) {
				updateEditionMode();
			}
		}		
	}

	/**
	 * @param clickedItem
	 * @param newTexture
	 * @throws SQLException 
	 */
	public void setTexture(Meshable clickedItem,String newTexture) throws SQLException {
		clickedItem.setTexture(newTexture);
		this.daoFactory.getDao(clickedItem.getClass()).modify(clickedItem);
		this.daoFactory.notifyObservers();
	}

	public void duplicate(Geometric clickedItem) {
		if (clickedItem instanceof Primitive){
			Primitive original = (Primitive) clickedItem;
			try {
				daoFactory.getDao(Primitive.class).insert(original.clone());
				daoFactory.notifyObservers();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		}
	}
	
}
