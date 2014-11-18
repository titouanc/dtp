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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import be.ac.ulb.infof307.g03.controllers.ObjectTreeController;
import be.ac.ulb.infof307.g03.models.*;

/**
 * @author pierre, titou
 * 
 */
public class ObjectTreeView extends JPanel implements TreeSelectionListener, Observer {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTree _tree;
	private ObjectTreeController _controller;
	private Project _project;
	private GeometryDAO _dao;
	private DefaultMutableTreeNode _root = new DefaultMutableTreeNode("Root");
	private Map<String,DefaultMutableTreeNode> _nodes = new HashMap<String,DefaultMutableTreeNode>();
	
	private JPopupMenu _popupMenuRD;
	private JPopupMenu _popupMenuD;
	
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
			DefaultMutableTreeNode clickedNode = (DefaultMutableTreeNode) _tree.getLastSelectedPathComponent();
	        Geometric clickedItem = (Geometric) clickedNode.getUserObject();
			if (cmd.equals(_RENAME)) {
				String name = JOptionPane.showInputDialog("New name ?");
				_controller.renameNode(clickedItem, name);
			} else if (cmd.equals(_DELETE)) {
				_controller.deselectElement(clickedItem);
				_controller.deleteNode(clickedItem);
			}
		}

	}

	/**
	 * Node rendering in TreeView
	 * @author titou
	 */
	class GeometricRenderer extends DefaultTreeCellRenderer {
		/**
		 * Default serial version UID
		 */
		private static final long serialVersionUID = 1L;

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
			} else if (value instanceof Floor){
				Floor fl = (Floor) value;
				sel = _project.config("floor.current").equals(fl.getUID());
			}
			if (sel)
				System.out.println("SELECTED: " + value.toString());
			super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
			return this;
		}
	}
	
	private DefaultMutableTreeNode _createNode(Geometric item){
		DefaultMutableTreeNode res = new DefaultMutableTreeNode(item.toString());
		res.setUserObject(item);
		res.setAllowsChildren(! item.isLeaf());
		_nodes.put(item.getUID(), res);
		return res;
	}
	
	private DefaultMutableTreeNode _createTree(Geometric root) throws SQLException{
		DefaultMutableTreeNode res = _createNode(root);
		if (root instanceof Group){
			for (Grouped grouped : _dao.getGrouped((Group) root))
				res.add(_createNode(grouped));
			for (Shape shape : _dao.getShapesForGroup((Group) root))
				res.add(_createTree(shape));
		}
		return res;
	}
	
	private void _createTree() throws SQLException{
		for (Floor floor : _dao.getFloors()){
			DefaultMutableTreeNode floorNode = _createNode(floor);
			for (Group group : _dao.getGroups(floor))
				floorNode.add(_createTree(group));
			_root.add(floorNode);
		}
	}
	
	/**
	 * Constructor of the main class ObjectTree
	 */
	public ObjectTreeView(ObjectTreeController newController, Project project) {
		super(new GridLayout(1, 0));
		_controller = newController;
		_project = project;
		
		try {
			_dao = project.getGeometryDAO();
			_createTree();
			_dao.addObserver(this);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		// Create a tree that allows one selection at a time.
		_tree = new JTree(_root);
		_tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		_tree.setCellRenderer(new GeometricRenderer());
		
		// Listen for when the selection changes
		_tree.addTreeSelectionListener(this);
		_tree.setRootVisible(false);
		_tree.setShowsRootHandles(true);
		
		// Add a menu popup to the tree
		_popupMenuRD = new JPopupMenu();
		_popupMenuD = new JPopupMenu();
		
	    JMenuItem menuItem = new JMenuItem(_RENAME);
	    menuItem.addActionListener(new PopupListener());
	    menuItem.setActionCommand(_RENAME);
	    _popupMenuRD.add(menuItem);
	    menuItem = new JMenuItem(_DELETE);
	    menuItem.addActionListener(new PopupListener());
	    menuItem.setActionCommand(_DELETE);
	    _popupMenuRD.add(menuItem);
	    
	    
	    menuItem = new JMenuItem(_DELETE);
	    menuItem.addActionListener(new PopupListener());
	    menuItem.setActionCommand(_DELETE);
	    _popupMenuD.add(menuItem);
	    
		
	    // create a mouse listener
		MouseListener ml = new MouseAdapter() {
		     public void mousePressed(MouseEvent e) {
		    	 // if right click
		    	 if (SwingUtilities.isRightMouseButton(e)) {
	    		 	// select the closest element near the click on the tree
	    	        int row = _tree.getClosestRowForLocation(e.getX(), e.getY());
	    	        _tree.setSelectionRow(row);
	    	        DefaultMutableTreeNode clickedNode = (DefaultMutableTreeNode) _tree.getLastSelectedPathComponent();
	    	        Geometric clickedItem = (Geometric) clickedNode.getUserObject();
	    	        if (clickedItem instanceof Group) {
	    	        	 _popupMenuRD.show(e.getComponent(), e.getX(), e.getY());
	    	        }
	    	        else if (clickedItem instanceof Grouped) {
	    	        	 _popupMenuD.show(e.getComponent(), e.getX(), e.getY());
	    	        }
	    	    }
		     }
		 };
		 // add the mouse listener to the tree
		 _tree.addMouseListener(ml);
		 
		 
		// Add the tree pane to this panel.
		add(_tree);
	}
	
	private Geometric _getGeometric(TreePath path){
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
		return (Geometric) node.getUserObject();
	}
	
	@Override
	public void valueChanged(TreeSelectionEvent event) {
		TreePath path = event.getOldLeadSelectionPath();
		if (path != null)
			_controller.deselectElement(_getGeometric(path));
		path = event.getNewLeadSelectionPath();
		if (path != null)
			_controller.selectElement(_getGeometric(path));
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		/* Flag: do we need to update GUI ? */
		boolean updateUI = false;
		List<Change> changes = (List<Change>) arg1;
		
		for (Change change : changes){
			Geometric changed = change.getItem();
			
			/* An object has been update: update the linked object in TreeView */
			if (change.isUpdate()){
				DefaultMutableTreeNode node = _nodes.get(changed.getUID());
				if (node != null){
					node.setUserObject(changed);
					updateUI = true;
				}
				/* For groups, also update Grouped items (their toString() is different) */
				if (changed instanceof Group){
					Group grp = (Group) changed;
					try {
						for (Grouped grouped : _dao.getGrouped(grp)){
							node = _nodes.get(grouped.getUID());
							if (node != null){
								node.setUserObject(grouped);
								updateUI = true;
							}
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
			
			/* An object has been deleted: remove from tree */
			else if (change.isDeletion()){
				DefaultMutableTreeNode node = _nodes.get(changed.getUID());
				if (node != null){
					node.removeFromParent();
					_nodes.remove(node);
					updateUI = true;
				}
			}
			/* Creation: insert in right place in tree */
			else if (change.isCreation() && ! _nodes.containsKey(changed.getUID())){
				DefaultMutableTreeNode newNode = null;
				try {newNode = _createTree(changed);}
				catch (SQLException err){err.printStackTrace();}
				
				if (changed instanceof Floor){
					_root.add(newNode);
					updateUI = true;
				} else if (changed instanceof Grouped){
					_nodes.get(((Grouped) changed).getGroup().getUID()).add(newNode);
					updateUI = true;
				} else if (changed instanceof Group){
					Group grp = (Group) changed;
					if (grp.getGroup() != null)
						_nodes.get(grp.getGroup().getUID()).add(newNode);
					else if (grp.getFloor() != null)
						_nodes.get(grp.getFloor().getUID()).add(newNode);
					updateUI = true;
				}
				
			}
		}
		
		/* Update GUI if needed */
		if (updateUI)
			_tree.updateUI();
	}
}
