package be.ac.ulb.infof307.g03.controllers;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFileChooser;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

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
	private MasterDAO daoFactory = null;

	private Floor currentFloor = null;
	
	// Supported file type
	private static final String FILE_TYPE_OBJ = ".obj";
	private static final String FILE_TYPE_DAE = ".dae";
	private static final String FILE_TYPE_3DS = ".3ds";
	private static final String FILE_TYPE_KMZ = ".kmz";
	
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
		Geometric newFloor = this.daoFactory.getByUID(project.config("floor.current"));
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
				this.daoFactory.getDao(Entity.class).insert(entity);
				this.daoFactory.notifyObservers();
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
			this.daoFactory.getDao(Entity.class).remove(entity);
			this.daoFactory.notifyObservers();
		} catch (SQLException ex) {
			Log.exception(ex);
		}
	}
	
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
		Floor currentFloor = (Floor) this.daoFactory.getByUID(currentFloorUID);
		Item newItem = new Item(currentFloor, selectedEntity);
		try {
			this.daoFactory.getDao(Item.class).insert(newItem);
			this.daoFactory.notifyObservers();
 		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private FileFilter exportFileFilter(final String extention) {
		return new FileFilter() {
			@Override
			public String getDescription() {
				return extention;
			}
			@Override
			public boolean accept(File file) {
				return file.getName().endsWith(extention);
			}
		};
	}
	
	private String getFileName(String fileName) {
		int pos = fileName.lastIndexOf(".");
		if (pos > 0) {
		    return fileName.substring(0, pos);
		}
		return fileName;
	}
	
	private String formatFileName(String fileName, String extention) {
		if (fileName.endsWith(extention)) return fileName;
		return fileName+extention;
	}
	
	public void onExport(Entity selectedEntity) {
		JFileChooser fileChooser = new JFileChooser(); 
		fileChooser.setSelectedFile(new File(selectedEntity.getName()+FILE_TYPE_OBJ));
		fileChooser.setAcceptAllFileFilterUsed(false);
		
		fileChooser.addChoosableFileFilter(exportFileFilter(FILE_TYPE_DAE));
		fileChooser.addChoosableFileFilter(exportFileFilter(FILE_TYPE_3DS));
		fileChooser.addChoosableFileFilter(exportFileFilter(FILE_TYPE_KMZ));
		fileChooser.addChoosableFileFilter(exportFileFilter(FILE_TYPE_OBJ));
		
		PropertyChangeListener propertyChangeListener = new PropertyChangeListener() {	
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
		        if (JFileChooser.FILE_FILTER_CHANGED_PROPERTY.equals(evt.getPropertyName())) {
		            JFileChooser fileChooser = (JFileChooser) evt.getSource();
		        	String fileName = fileChooser.getSelectedFile().getName();
		        	fileChooser.setSelectedFile(new File(getFileName(fileName)+fileChooser.getFileFilter().getDescription()));
		        }
		    }
		};
		
		fileChooser.addPropertyChangeListener(propertyChangeListener);
		int rVal = fileChooser.showSaveDialog(view);
		
		if (rVal == JFileChooser.APPROVE_OPTION) {
			// THIS IS ALL YOU NEED TO SAVE THE FILE
			System.out.println(fileChooser.getSelectedFile().getName());
			System.out.println(fileChooser.getFileFilter().getDescription());
			// USE "formatFileName" TO PREVENT EXTENTION DELETION BY USER
			System.out.println(formatFileName(fileChooser.getSelectedFile().getName(),fileChooser.getFileFilter().getDescription()));
			System.out.println(fileChooser.getCurrentDirectory().toString());
		}
		
	}
	
	private FileFilter importFileFilter(final ArrayList<String> extentions) {
		return new FileFilter() {
			@Override
			public String getDescription() {
				String res = "";
				for (String extention : extentions) {
					res += "*"+extention+", ";
				}
				return res.substring(0, res.length()-2);
			}
			@Override
			public boolean accept(File file) {
				for (String extention : extentions) {
					if (file.getName().endsWith(extention)) {
						return true;
					}
				}
				return false;
			}
		};
	}
	
	public void onImport() {
		FileFilter fileFilter = importFileFilter(new ArrayList<String>(Arrays.asList(FILE_TYPE_3DS,FILE_TYPE_DAE,FILE_TYPE_KMZ,FILE_TYPE_OBJ)));

		JFileChooser fileChooser = new JFileChooser(); 
		fileChooser.setAcceptAllFileFilterUsed(false);
		
		fileChooser.addChoosableFileFilter(fileFilter);
		
		int rVal = fileChooser.showOpenDialog(view);
		
		if (rVal == JFileChooser.APPROVE_OPTION) {
			// THIS IS ALL YOU NEED TO OPEN THE FILE
			System.out.println(fileChooser.getSelectedFile().getName());
			System.out.println(fileChooser.getCurrentDirectory().toString());
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
		if (e.getButton()==MouseEvent.BUTTON3) {
			JPopupMenu popupMenu = this.view.createPopupMenu();
			popupMenu.show(e.getComponent(), e.getX(), e.getY());
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		Config changed = (Config) arg1;
		if (changed.getName().equals("floor.current")){
			Geometric newFloor = this.daoFactory.getByUID(changed.getValue());
			if (newFloor != null)
				this.currentFloor = (Floor) newFloor;
		}
	}
	
}
