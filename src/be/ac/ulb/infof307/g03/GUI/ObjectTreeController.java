/**
 * 
 */
package be.ac.ulb.infof307.g03.GUI;

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
	
	/**
	 * Switch between the word tree and the object Tree
	 */
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
	 * Helper method to remove a floor's content
	 * @param deletingFloor The floor to remove
	 */
	private void deleteFloorContent(Floor deletingFloor){
		try {
			GeometricDAO<Floor> floorDao = this.daoFactory.getDao(Floor.class);
			/* If we delete the current floor, set current floor to previous, or next */
			if (deletingFloor.equals(this.project.getSelectionManager().currentFloor())){
				Floor previous = floorDao.queryForFirst(deletingFloor.getQueryForPreceeding(floorDao));
				if (previous != null){
					this.project.getSelectionManager().setCurrentFloor(previous);
				} else {
					Floor next = floorDao.queryForFirst(deletingFloor.getQueryForFollowing(floorDao));
					if (next != null){
						this.project.getSelectionManager().setCurrentFloor(next);
					} else {
						this.project.getSelectionManager().setCurrentFloor(null); // TODO define what to send as current floor
					}
				}
			}
			/* Adapt the base height of all floors above */
			for (Floor floor : floorDao.query(deletingFloor.getQueryForFollowing(floorDao))){
				floor.setBaseHeight(floor.getBaseHeight() - deletingFloor.getHeight());
				floor.setIndex(floor.getIndex() - 1);
				floorDao.modify(floor);
			}
			/* Remove all rooms on this floor */
			for (Room room : deletingFloor.getRooms()){
				for (Area area : room.getAreas()){
					this.daoFactory.getDao(area.getClass()).remove(area);
				}
				this.daoFactory.getDao(Room.class).remove(room);
			}
			/* Remove all items on this floor */
			GeometricDAO<Item> itemDao = this.daoFactory.getDao(Item.class);
			for (Item item : deletingFloor.getItems()){
				itemDao.remove(item);
			}
		} catch (SQLException err){
			Log.exception(err);
		}
	}
	
	/**
	 * Delete a Geometric node
	 * @param object
	 */
	public void deleteNode(Object object){
		if (object instanceof Geometric){
			Geometric item = (Geometric) object;
			// If object is a container (Floor, Room, ...), first delete all of its contents
			if (item instanceof Floor){
				try {
					this.daoFactory.getDao(Floor.class).remove((Floor) item);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				this.deleteFloorContent((Floor) item);
			} else if (item instanceof Room){
				Room room = (Room) item;
				try {
					/* Remove all the room's areas */
					for (Area area : room.getAreas()){
						this.daoFactory.getDao(area.getClass()).remove(area);
					}
					this.daoFactory.notifyObservers();
					this.daoFactory.getDao(Room.class).remove(room);
				} catch (SQLException e) {
					Log.exception(e);
				}
			} else {
				// Then delete the object itself
				try {
					Log.info("DELETE %s", item.toString());
					this.daoFactory.getDao(item.getClass()).remove(item);
				} catch (SQLException e) {
					Log.exception(e);
				}
			}
			this.daoFactory.notifyObservers();
		}
	}

	/**
	 * Unset the select flag of a Meshable element
	 * @param element
	 */
	public void deselectElement(Object element) {
		this.project.getSelectionManager().unselect(false);
	}

	/**
	 * Set the select flag of a Meshable element
	 * @param element
	 */
	public void selectElement(Object element) {
		if (element instanceof Floor){
			Floor current = (Floor) element;
			this.project.getSelectionManager().setCurrentFloor(current);
		} else if (element instanceof Area){
			Area area = (Area) element;
			this.project.getSelectionManager().select(area.getRoom());
		} else if (element instanceof Room){
			this.project.getSelectionManager().select((Room) element);
		} else if (element instanceof Item) {
			this.project.getSelectionManager().select((Item) element);
		}
	}

	/**
	 * Unset the visible flag of a Meshable item
	 * @param meshable
	 */
	public void hideMeshable(Meshable meshable){
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
	public void showMeshable(Meshable meshable){
		meshable.show();
		try {
			this.daoFactory.getDao(meshable.getClass()).modify(meshable);
			this.daoFactory.notifyObservers();
		} catch (SQLException ex) {
			Log.exception(ex);
		}
	}

	/**
	 * Change a wall width
	 * @param wall The wall
	 * @param userInput The with as a String (entered by user)
	 */
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
		double deltaHeight = height - floor.getHeight();
		floor.setHeight(height);
		try{
			GeometricDAO<Floor> floorDao = this.daoFactory.getDao(Floor.class);
			floorDao.modify(floor);
			for (Floor next : floorDao.query(floor.getQueryForFollowing(floorDao))){
				next.setBaseHeight(next.getBaseHeight() + deltaHeight);
				floorDao.update(next);
			}
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
		// Nothing need to be done here
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// Nothing need to be done here
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// Nothing need to be done here	
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// Nothing need to be done here	
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// if right click
		if (SwingUtilities.isRightMouseButton(e)) {
			// select the closest element near the click on the tree
			TreePath path = this.view.getPathForLocation(e.getX(), e.getY());
			if (path != null){
				DefaultMutableTreeNode clickedNode = (DefaultMutableTreeNode) path.getLastPathComponent();
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
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// Nothing need to be done here
		
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

	/**
	 * Called when user choose to duplicate primitive
	 * @param clickedItem The primitive to be duplicated
	 */
	public void duplicate(Geometric clickedItem) {
		if (clickedItem instanceof Primitive){
			Primitive original = (Primitive) clickedItem;
			try {
				daoFactory.getDao(Primitive.class).insert(original.clone());
				daoFactory.notifyObservers();
			} catch (SQLException e) {
				Log.exception(e);
			}
			
		}
	}
	
}
