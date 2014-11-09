/**
 * 
 */
package be.ac.ulb.infof307.g03.views;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.SQLException;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import be.ac.ulb.infof307.g03.controllers.ObjectTreeController;
import be.ac.ulb.infof307.g03.models.*;

/**
 * @author pierre, titou
 * 
 */
public class ObjectTreeView extends JPanel implements TreeSelectionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTree _tree;
	private GeometricTree _model;
	private ObjectTreeController _controller;
	
	private JPopupMenu _popupMenu;
	
	static private final String _RENAME  = "Rename" ;
	static private final String _DELETE = "Delete";
	
	/**
	 * This class implements a ActionListener to be 
	 * used with a popup menu
	 */
	class PopupListener implements ActionListener {

		/**
		 * This method is called when user click on a menu
		 * @param event click on menu
		 */
		@Override
		public void actionPerformed(ActionEvent event) {
			String cmd = event.getActionCommand();
			if (cmd.equals(_RENAME)) {
				System.out.println("[DEBUG] User clicked on rename");
				// the selected shape is _tree.getLastSelectedPathComponent()
				_controller.renameNode(_tree.getLastSelectedPathComponent(), "New Name");
			} else if (cmd.equals(_DELETE)) {
				System.out.println("[DEBUG] User clicked on delete");
				_controller.deleteNode(_tree.getLastSelectedPathComponent());
			
			}
		}

	}

	class GeometricRenderer extends DefaultTreeCellRenderer {
		public Component getTreeCellRendererComponent(
                JTree tree,
                Object value,
                boolean sel,
                boolean expanded,
                boolean leaf,
                int row,
                boolean hasFocus){
			if (value instanceof Grouped){
				Grouped item = (Grouped) value;
				sel = item.isSelected();
			}
			super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
			return this;
		}
	}
	
	/**
	 * Constructor of the main class ObjectTree
	 */
	public ObjectTreeView(ObjectTreeController newController, Project project) {
		super(new GridLayout(1, 0));
		_controller = newController;
		
		try {
			_model = new GeometricTree(project.getGeometryDAO());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Create a tree that allows one selection at a time.
		_tree = new JTree(_model);
		_tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		_tree.setCellRenderer(new GeometricRenderer());
		
		// Listen for when the selection changes
		_tree.addTreeSelectionListener(this);
		_tree.setRootVisible(false);
		
		// Add a menu popup to the tree
		_popupMenu = new JPopupMenu();
	    JMenuItem menuItem = new JMenuItem(_RENAME);
	    menuItem.addActionListener(new PopupListener());
	    menuItem.setActionCommand(_RENAME);
	    _popupMenu.add(menuItem);
	    menuItem = new JMenuItem(_DELETE);
	    menuItem.addActionListener(new PopupListener());
	    menuItem.setActionCommand(_DELETE);
	    _popupMenu.add(menuItem);
		
	    // create a mouse listener
		MouseListener ml = new MouseAdapter() {
		     public void mousePressed(MouseEvent e) {
		    	 // if right click
		    	 if (SwingUtilities.isRightMouseButton(e)) {
	    		 	// select the closest element near the click on the tree
	    	        int row = _tree.getClosestRowForLocation(e.getX(), e.getY());
	    	        _tree.setSelectionRow(row);
	    	        _popupMenu.show(e.getComponent(), e.getX(), e.getY());
	    	    }
		     }
		 };
		 // add the mouse listener to the tree
		 _tree.addMouseListener(ml);
		 
		 
		// Add the tree pane to this panel.
		add(_tree);
	}
	
	@Override
	public void valueChanged(TreeSelectionEvent event) {
		TreePath path = event.getOldLeadSelectionPath();
		if (path != null)
			_controller.deselectElement(path.getLastPathComponent());
		path = event.getNewLeadSelectionPath();
		if (path != null)
			_controller.selectElement(path.getLastPathComponent());
	}
}
