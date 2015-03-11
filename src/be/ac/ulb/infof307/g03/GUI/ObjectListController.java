package be.ac.ulb.infof307.g03.GUI;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.SQLException;

import javax.swing.JPopupMenu;

import be.ac.ulb.infof307.g03.models.*;
import be.ac.ulb.infof307.g03.utils.Log;

/**
 * @author titou, pierre
 *
 */
public class ObjectListController implements MouseListener {
	private ObjectListView view = null;
	private Project project = null;
	private MasterDAO daoFactory = null;
	
	private FileChooserController fileController;
	
	/**
	 * @param project the main project
	 */
	public ObjectListController(Project project) {
		this.view = new ObjectListView(this,project);
		this.project = project;
		try {
			this.daoFactory = project.getGeometryDAO();
		} catch (SQLException ex) {
			Log.exception(ex);
		}
	}
	
	/**
	 * @author fhennecker
	 * Run the ObjectTree GUI
	 */
	public void run(){
		initView(this.project);
		this.fileController = new FileChooserController(this.view, this.project, null); //TODO remplacer null
		this.fileController.run();
	}
	
	/**
	 * This method initiate the view
	 * @param project The main project
	 */
	public void initView(Project project){
		this.view = new ObjectListView(this, project);
	}
	
	/**
	 * @return The controller's view
	 */
	public ObjectListView getView() {
		return this.view;
	}
	
	/**
	 * Called when user want to create a new object
	 * @param name The name of the new object
	 */
	public void onNewAction(String name) {
		if (name != null) {
			Entity entity = new Entity(name);
			try {
				this.daoFactory.getDao(Entity.class).insert(entity);
				this.daoFactory.notifyObservers();
			} catch (SQLException ex) {
				Log.exception(ex);
			}
			this.project.config("entity.current", entity.getUID());
			this.project.config("edition.mode", "object");
		}
	}
	
	/**
	 * When user click on delete on an object
	 * @param entity The entity to delete.
	 */
	public void onDeleteAction(Entity entity) {
		if (this.project.config("edition.mode").equals("object")) {
			if (this.project.config("entity.current").equals(entity.getUID())) {
				this.project.config("mouse.mode", "dragSelect");
				this.project.config("edition.mode","world");
			}
		}
		try {
			this.daoFactory.getDao(Entity.class).remove(entity);
			this.daoFactory.notifyObservers();
		} catch (SQLException ex) {
			Log.exception(ex);
		}
	}
	
	/**
	 * Rename an object
	 * @param entity The entity to rename
	 * @param newName The new name
	 */
	public void onRenameAction(Entity entity, String newName) {
		if (newName != null) {
			entity.setName(newName);
			try {
				this.daoFactory.getDao(Entity.class).modify(entity);
				this.daoFactory.notifyObservers();
			} catch (SQLException ex) {
				Log.exception(ex);
			}
		}
	}

	/**
	 * Called when user edit an object. Switch to object mode.
	 * @param entity The entity to edit
	 */
	public void onEditAction(Entity entity) {
		this.project.config("entity.current", entity.getUID());
		this.project.config("edition.mode", "object");
	}
	
	/**
	 * Called when the user select the "Insert on floor" 
	 * option in contextual menu
	 * @param selectedEntity The clicked entity
	 */
	public void onInsertAction(Entity selectedEntity) {
		this.project.config("edition.mode", "world");
		Item newItem = new Item(this.project.getSelectionManager().currentFloor(), selectedEntity);
		try {
			this.daoFactory.getDao(Item.class).insert(newItem);
			this.daoFactory.notifyObservers();
 		} catch (SQLException e) {
 			Log.exception(e);
		}
	}
	
	
	/**
	 * Create the dialog when the user click on export
	 * @param selectedEntity The entity to export
	 */
	public void onExport(Entity selectedEntity) {
		this.fileController.notifyDisplayExport(selectedEntity);
		
	}
	
	
	/**
	 * Create the dialog when user click on import
	 */
	public void onImport() {		
		this.fileController.notifyDisplayImport();
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {		
	}

	@Override
	public void mouseEntered(MouseEvent e) {		
	}

	@Override
	public void mouseExited(MouseEvent e) {		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		int proachestIndex = this.view.locationToIndex(e.getPoint());
		Point proachest = this.view.indexToLocation(proachestIndex);
		if (proachest!=null) {
			if (Math.abs(e.getY()-proachest.getY())<20) {
				if (! this.view.isSelectedIndex(proachestIndex)) {
					this.view.setSelectedIndex(proachestIndex);
				}
			} else {
				this.view.clearSelection();
			}
		}
		if (e.getButton()==MouseEvent.BUTTON1) {
			Entity selectedEntity = (Entity) view.getSelectedValue();
			if (selectedEntity != null){
				this.onEditAction(selectedEntity);
			}
		}
		if (e.getButton()==MouseEvent.BUTTON3) {
			JPopupMenu popupMenu = this.view.createPopupMenu();
			popupMenu.show(e.getComponent(), e.getX(), e.getY());
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}
	
	/**
	 * @return The current floor
	 */
	public Floor getCurrentFloor(){
		return this.project.getSelectionManager().currentFloor();
	}
}
