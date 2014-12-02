package be.ac.ulb.infof307.g03.controllers;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.SQLException;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;

import be.ac.ulb.infof307.g03.models.Entity;
import be.ac.ulb.infof307.g03.models.Geometric;
import be.ac.ulb.infof307.g03.models.GeometryDAO;
import be.ac.ulb.infof307.g03.models.Project;
import be.ac.ulb.infof307.g03.views.ObjectListView;
import be.ac.ulb.infof307.g03.views.ObjectTreeView;

public class ObjectListController implements MouseListener {
	private ObjectListView _view = null;
	private Project _project = null;
	private GeometryDAO _dao = null;
	
	public ObjectListController(Project project) {
		_view = new ObjectListView(this,project);
		_project = project;
		try {
			_dao = project.getGeometryDAO();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * @author fhennecker
	 * Run the ObjectTree GUI
	 */
	public void run(){
		initView(_project);
	}
	
	/**
	 * This method initiate the view
	 * @param project The main project
	 */
	public void initView(Project project){
		_view = new ObjectListView(this, project);
	}
	
	public ObjectListView getView() {
		return _view;
	}
	
	public void onNewAction(String name) {
		if (name != null) {
			Entity entity = new Entity(name);
			try {
				_dao.create(entity);
				_dao.notifyObservers();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			_project.config("entity.current", entity.getUID());
			_project.config("edition.mode", "object");
		}
	}
	
	public void onDeleteAction(Entity entity) {
		if (_project.config("edition.mode").equals("object")) {
			if (_project.config("entity.current").equals(entity.getUID())) {
				_project.config("edition.mode","world");
			}
		}
		try {
			_dao.delete(entity);
			_dao.notifyObservers();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void onRenameAction(Entity entity, String newName) {
		if (newName != null) {
			entity.setName(newName);
			try {
				_dao.update(entity);
				_dao.notifyObservers();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void onEditAction(Entity entity) {
		_project.config("entity.current", entity.getUID());
		_project.config("edition.mode", "object");
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
			JPopupMenu popupMenu = _view.createPopupMenu();
			// Select the item
			int row = _view.locationToIndex(new Point(e.getX(),e.getY()));
			_view.setSelectedIndex(row);

			popupMenu.show(e.getComponent(), e.getX(), e.getY());
		}
	}

}
