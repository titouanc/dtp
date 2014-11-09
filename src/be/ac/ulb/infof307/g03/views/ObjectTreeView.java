/**
 * 
 */
package be.ac.ulb.infof307.g03.views;

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
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeSelectionModel;

import be.ac.ulb.infof307.g03.controllers.ObjectTreeController;
import be.ac.ulb.infof307.g03.models.*;

/**
 * @author pierre, titou
 * 
 */
public class ObjectTreeView extends JPanel implements TreeSelectionListener {
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
		_tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
        _tree.setCellRenderer(renderer);
		
		// Listen for when the selection changes
		_tree.addTreeSelectionListener(this);
		
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
	public void valueChanged(TreeSelectionEvent arg0) {
		// TODO Auto-generated method stub
		if (_tree.getLastSelectedPathComponent() instanceof Group) {
			System.out.println("[DEBUG] Group selected");
			//Group selectedGroup = (Group) _tree.getLastSelectedPathComponent();
		}
		else if (_tree.getLastSelectedPathComponent() instanceof Line) {
			System.out.println("[DEBUG] Line selected");
			//Line selectedLine = (Line) _tree.getLastSelectedPathComponent();
			}
		else
			return;

	}

}
