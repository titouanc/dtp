/**
 * 
 */
package be.ac.ulb.infof307.g03.views;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
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
public class ObjectTreeView extends JTree implements Observer {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// Attribute
	private ObjectTreeController _controller;
	private Project _project;
	private GeometryDAO _dao;
	private static DefaultMutableTreeNode _root = new DefaultMutableTreeNode("Root");
	private Map<String,DefaultMutableTreeNode> _nodes = new HashMap<String,DefaultMutableTreeNode>();
		
	// Action alias
	static private final String _RENAME  = "Rename" ;
	static private final String _DELETE  = "Delete";
	static private final String _HIDE    = "Hide";
	static private final String _SHOW    = "Show";
	static private final String _WIDTH   = "Width";
	static private final String _HEIGHT	 = "Height";
	
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
			DefaultMutableTreeNode clickedNode = (DefaultMutableTreeNode) getLastSelectedPathComponent();
	        Geometric clickedItem = (Geometric) clickedNode.getUserObject();
			if (cmd.equals(_RENAME)) {
				String name = JOptionPane.showInputDialog("New name ?");
				_controller.renameNode(clickedItem, name);
			} else if (cmd.equals(_DELETE)) {
				_controller.deselectElement(clickedItem);
				_controller.deleteNode(clickedItem);
			} else if (cmd.equals(_SHOW)){
				_controller.showGrouped((Grouped) clickedItem);
			} else if (cmd.equals(_HIDE)){
				_controller.hideGrouped((Grouped) clickedItem);
			} else if (cmd.equals(_WIDTH)){
				String userInput = JOptionPane.showInputDialog("Width ?");
				_controller.setWidth((Wall) clickedItem, userInput);
			} else if (cmd.equals(_HEIGHT)){
				String userInput = JOptionPane.showInputDialog("Height ?");
				_controller.setHeight((Floor) clickedItem, userInput);
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
			if (value instanceof DefaultMutableTreeNode)
				value = ((DefaultMutableTreeNode) value).getUserObject();
			if (value instanceof Grouped){
				Grouped item = (Grouped) value;
				sel = item.isSelected();
			} else if (value instanceof Floor){
				Floor fl = (Floor) value;
				sel = _project.config("floor.current").equals(fl.getUID());
			}
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
				if (shape instanceof Group)
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
	
	private JMenuItem createJMenuItem(String label, String action, PopupListener listener) {
		JMenuItem menuItem = new JMenuItem(label);
		menuItem.addActionListener(listener);
		menuItem.setActionCommand(action);
		return menuItem;
	}
	
	/**
	 * Build a contextual menu for a clicked item
	 * @param geo
	 * @return
	 */
	public JPopupMenu createPopupMenu(Geometric geo){
		if (geo instanceof Line)
			return null;
		
		PopupListener listener = new PopupListener();
		JPopupMenu res = new JPopupMenu();
		
		res.add(createJMenuItem(_DELETE, _DELETE, listener));
		if (geo instanceof Group){
			res.add(createJMenuItem(_RENAME, _RENAME, listener));
		} else if (geo instanceof Grouped){
			String action = ((Grouped) geo).isVisible() ? _HIDE : _SHOW;
			res.add(createJMenuItem(action, action, listener));
			if (geo instanceof Wall){
				res.add(createJMenuItem("Edit width", _WIDTH, listener));
			}
		} else if (geo instanceof Floor){
			res.add(createJMenuItem(_HEIGHT, _HEIGHT, listener));
		}
		
		return res;
	}
	
	/**
	 * Constructor of the main class ObjectTree
	 */
	public ObjectTreeView(ObjectTreeController newController, Project project) {
		super(_root);
		
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
		getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		setCellRenderer(new GeometricRenderer());
		
		// Listen for when the selection changes
		addTreeSelectionListener(_controller);
		setRootVisible(false);
		setShowsRootHandles(true);

		// add the mouse listener to the tree
		addMouseListener(_controller);
		// add key listener
		addKeyListener(_controller);
		
		updateUI();
	}
	
	public Geometric getGeometric(TreePath path){
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
		return (Geometric) node.getUserObject();
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
			updateUI();
	}
	

}
