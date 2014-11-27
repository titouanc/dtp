package be.ac.ulb.infof307.g03.controllers;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import be.ac.ulb.infof307.g03.models.Project;
import be.ac.ulb.infof307.g03.views.ObjectListView;

public class ObjectListController implements MouseListener {
	private ObjectListView _view = null;
	private Project _project = null;
	
	public ObjectListController(Project project) {
		_view = new ObjectListView(this);
		_project = project;
	}
	
	public ObjectListView getView() {
		return _view;
	}
	
	public void onNewAction(String name) {
		// notity model
		_project.config("edition.mode", "object");
	}
	
	public void onDeleteAction() {
		// notify model
	}
	
	public void onRenameAction() {
		// notify model
	}

	public void onEditAction() {
		// notify model
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
