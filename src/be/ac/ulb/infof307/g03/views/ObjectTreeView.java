/**
 * 
 */
package be.ac.ulb.infof307.g03.views;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.jme3.material.Material;

import be.ac.ulb.infof307.g03.controllers.ObjectTreeController;
import be.ac.ulb.infof307.g03.models.*;
import be.ac.ulb.infof307.g03.utils.Log;

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
	static private final String _RENAME  		 = "Rename" ;
	static private final String _DELETE 		 = "Delete";
	static private final String _HIDE   		 = "Hide";
	static private final String _SHOW   		 = "Show";
	static private final String _WIDTH   		 = "Width";
	static private final String _HEIGHT	 		 = "Height";
	static private final String _CHANGETEXTURE	 = "Change Texture";
	
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
				_controller.showGrouped((Meshable) clickedItem);
			} else if (cmd.equals(_HIDE)){
				_controller.hideGrouped((Meshable) clickedItem);
			} else if (cmd.equals(_WIDTH)){
				String userInput = JOptionPane.showInputDialog("Width ?");
				_controller.setWidth((Wall) clickedItem, userInput);
			} else if (cmd.equals(_HEIGHT)){
				String userInput = JOptionPane.showInputDialog("Height ?");
				_controller.setHeight((Floor) clickedItem, userInput);
			}
			else if (cmd.equals(_CHANGETEXTURE)){
				String currentTexture=_project.config("texture.selected");
				// On va assigner à l'objet cliqué la texture sélectionnée
				if (clickedItem instanceof Meshable){
					try {
						_controller.setTexture((Meshable)clickedItem,currentTexture);
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				
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
			if (value instanceof Meshable){
				Meshable item = (Meshable) value;
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
		boolean hasChildren = (item instanceof Room || item instanceof Floor);
		res.setAllowsChildren(hasChildren);
		_nodes.put(item.getUID(), res);
		return res;
	}
	
	private DefaultMutableTreeNode _createTree(Geometric root) throws SQLException{
		DefaultMutableTreeNode res = _createNode(root);
		if (root instanceof Room){
			Room room = (Room) root;
			for (Meshable meshable : room.getMeshables())
				res.add(_createNode(meshable));
		}
		return res;
	}
	
	public void createTree() throws SQLException{
		Log.debug("createTree");
		_root.removeAllChildren();
		for (Floor floor : _dao.getFloors()){
			DefaultMutableTreeNode floorNode = _createNode(floor);
			for (Room room : _dao.getRooms(floor))
				floorNode.add(_createTree(room));
			_root.add(floorNode);
		}
	}
	
	public void clearTree() {
		Log.debug("clearTree");
		for (DefaultMutableTreeNode node : _nodes.values()) {
			node.removeFromParent();
			_nodes.remove(node);
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
		if (geo instanceof Binding)
			return null;
		
		PopupListener listener = new PopupListener();
		JPopupMenu res = new JPopupMenu();
		
		res.add(createJMenuItem(_DELETE, _DELETE, listener));
		if (geo instanceof Room){
			res.add(createJMenuItem(_RENAME, _RENAME, listener));
		} else if (geo instanceof Meshable){
			String action = ((Meshable) geo).isVisible() ? _HIDE : _SHOW;
			res.add(createJMenuItem(action, action, listener));
			if (geo instanceof Wall){
				res.add(createJMenuItem("Edit width", _WIDTH, listener));
			}
			res.add(createJMenuItem("Change Texture",_CHANGETEXTURE,listener));
		} else if (geo instanceof Floor){
			res.add(createJMenuItem(_HEIGHT, _HEIGHT, listener));
		}
		
		return res;
	}
	
	/**
	 * Constructor of the main class ObjectTree
	 * @param newController 
	 * @param project 
	 */
	public ObjectTreeView(ObjectTreeController newController, Project project) {
		super(_root);
		
		_controller = newController;
		_project = project;
		
		try {
			_dao = project.getGeometryDAO();
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
		
	}
	
	/**
	 * @param path
	 * @return
	 */
	public Geometric getGeometric(TreePath path){
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
		return (Geometric) node.getUserObject();
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		/* Flag: do we need to update GUI ? */
		boolean mustUpdateUI = false;
		List<Change> changes = (List<Change>) arg1;
		
		for (Change change : changes){
			Geometric changed = change.getItem();
			
			try {
				_dao.refresh(changed);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			/* An object has been update: update the linked object in TreeView */
			if (change.isUpdate()){
				DefaultMutableTreeNode node = _nodes.get(changed.getUID());
				if (node != null){
					node.setUserObject(changed);
					mustUpdateUI = true;
				}
			}
			
			/* An object has been deleted: remove from tree */
			else if (change.isDeletion()){
				DefaultMutableTreeNode node = _nodes.get(changed.getUID());
				if (node != null){
					node.removeFromParent();
					_nodes.remove(node);
					mustUpdateUI = true;
				}
			}
			/* Creation: insert in right place in tree */
			else if (change.isCreation() && ! _nodes.containsKey(changed.getUID())){
				DefaultMutableTreeNode newNode = null;
				try {newNode = _createTree(changed);}
				catch (SQLException err){err.printStackTrace();}
				
				if (changed instanceof Floor){
					_root.add(newNode);
					mustUpdateUI = true;
				} else if (changed instanceof Meshable){
					_nodes.get(((Meshable) changed).getRoom().getUID()).add(newNode);
					mustUpdateUI = true;
				} else if (changed instanceof Room){
					Room room = (Room) changed;
					_nodes.get(room.getFloor().getUID()).add(newNode);
					mustUpdateUI = true;
				}
				
			}
		}
		if (mustUpdateUI){
			((DefaultTreeModel) treeModel).reload();
		}
	}
	

}
