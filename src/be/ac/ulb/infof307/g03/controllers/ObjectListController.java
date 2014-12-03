package be.ac.ulb.infof307.g03.controllers;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.SQLException;

import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import be.ac.ulb.infof307.g03.models.Entity;
import be.ac.ulb.infof307.g03.models.Floor;
import be.ac.ulb.infof307.g03.models.GeometryDAO;
import be.ac.ulb.infof307.g03.models.Item;
import be.ac.ulb.infof307.g03.models.Project;
import be.ac.ulb.infof307.g03.utils.Log;
import be.ac.ulb.infof307.g03.views.ObjectListView;

/**
 * @author titou
 *
 */
public class ObjectListController implements MouseListener {
	private ObjectListView view = null;
	private Project project = null;
	private GeometryDAO dao = null;
	
	/**
	 * @param project the main project
	 */
	public ObjectListController(Project project) {
		this.view = new ObjectListView(this,project);
		this.project = project;
		try {
			this.dao = project.getGeometryDAO();
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
	}
	
	/**
	 * This method initiate the view
	 * @param project The main project
	 */
	public void initView(Project project){
		this.view = new ObjectListView(this, project);
	}
	
	public ObjectListView getView() {
		return this.view;
	}
	
	public void onNewAction(String name) {
		if (name != null) {
			Entity entity = new Entity(name);
			try {
				this.dao.create(entity);
				this.dao.notifyObservers();
			} catch (SQLException ex) {
				Log.exception(ex);
			}
			this.project.config("entity.current", entity.getUID());
			this.project.config("edition.mode", "object");
		}
	}
	
	public void onDeleteAction(Entity entity) {
		if (this.project.config("edition.mode").equals("object")) {
			if (this.project.config("entity.current").equals(entity.getUID())) {
				this.project.config("mouse.mode", "dragSelect");
				this.project.config("edition.mode","world");
			}
		}
		try {
			this.dao.delete(entity);
			this.dao.notifyObservers();
		} catch (SQLException ex) {
			Log.exception(ex);
		}
	}
	
	public void onRenameAction(Entity entity, String newName) {
		if (newName != null) {
			entity.setName(newName);
			try {
				this.dao.update(entity);
				this.dao.notifyObservers();
			} catch (SQLException ex) {
				Log.exception(ex);
			}
		}
	}

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
		String currentFloorUID = this.project.config("floor.current");
		Floor currentFloor = (Floor) this.dao.getByUID(currentFloorUID);
		Item newItem = new Item(currentFloor, selectedEntity);
		try {
			this.dao.create(newItem);
			this.dao.notifyObservers();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
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
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (SwingUtilities.isRightMouseButton(e)) {
			JPopupMenu popupMenu = this.view.createPopupMenu();
			// Select the item
			int row = this.view.locationToIndex(new Point(e.getX(),e.getY()));
			this.view.setSelectedIndex(row);

			popupMenu.show(e.getComponent(), e.getX(), e.getY());
		}
	}
}
