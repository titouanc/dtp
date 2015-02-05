package be.ac.ulb.infof307.g03.controllers;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.sql.SQLException;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import be.ac.ulb.infof307.g03.models.*;
import be.ac.ulb.infof307.g03.utils.Log;
import be.ac.ulb.infof307.g03.views.ObjectListView;

/**
 * @author titou, pierre
 *
 */
public class ObjectListController implements MouseListener, Observer {
	private ObjectListView view = null;
	private Project project = null;
	private GeometryDAO dao = null;
	private Floor currentFloor = null;
	
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
		Geometric newFloor = this.dao.getByUID(project.config("floor.current"));
		if (newFloor != null)
			this.currentFloor = (Floor) newFloor;
		project.addObserver(this);
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
	
	/**
	 * @return The currently selected floor
	 */
	public Floor getCurrentFloor(){
		return this.currentFloor;
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

	@Override
	public void update(Observable arg0, Object arg1) {
		Config changed = (Config) arg1;
		if (changed.getName().equals("floor.current")){
			Geometric newFloor = this.dao.getByUID(changed.getValue());
			if (newFloor != null)
				this.currentFloor = (Floor) newFloor;
		}
	}
	
	public void exportAs(File fileToSave,String extension) {
		Log.info("Export as %s", fileToSave.getName());
		String filename = fileToSave.getAbsolutePath();
		
		if(!filename.endsWith(extension)){
			filename+=extension;
		}
		
		int dialogResult = JOptionPane.YES_OPTION;
		if(new File(filename).exists()){
			int dialogButton = JOptionPane.YES_NO_OPTION;
			dialogResult = JOptionPane.showConfirmDialog (null,
					"This project already exists! Would you like you replace it?","Warning",dialogButton);
		}
		if(dialogResult == JOptionPane.YES_OPTION){
				// OPTIONS D EXPORT ICI
				// TODO Export save the path of the current project to the BootController

		}
	}
}
