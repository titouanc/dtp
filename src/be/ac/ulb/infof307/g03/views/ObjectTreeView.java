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
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import be.ac.ulb.infof307.g03.controllers.ObjectTreeController;
import be.ac.ulb.infof307.g03.models.*;
import be.ac.ulb.infof307.g03.utils.Log;

/**
 * @author pierre, titou
 * 
 */
public class ObjectTreeView extends JTree implements Observer {
	private static final long serialVersionUID = 1L;
	
	// Attribute
	private ObjectTreeController controller;
	private Project project;
	private MasterDAO daoFactory;
	private static DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
	private Map<String,DefaultMutableTreeNode> nodes = new HashMap<String,DefaultMutableTreeNode>();
		
	// Action alias
	static private final String RENAME        = "Rename" ;
	static private final String DELETE        = "Delete";
	static private final String HIDE          = "Hide";
	static private final String SHOW          = "Show";
	static private final String WIDTH         = "Width";
	static private final String HEIGHT        = "Height";
	static private final String CHANGETEXTURE = "Change Texture";
	static private final String MODIFY        = "Modify";
	static private final String DUPLICATE     = "Duplicate";
	
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
			if (cmd.equals(RENAME)) {
				String name = JOptionPane.showInputDialog("New name ?");
				controller.renameNode(clickedItem, name);
			} else if (cmd.equals(DELETE)) {
				controller.deselectElement(clickedItem);
				controller.deleteNode(clickedItem);
			} else if (cmd.equals(SHOW)){
				controller.showMeshable((Meshable) clickedItem);
			} else if (cmd.equals(HIDE)){
				controller.hideMeshable((Meshable) clickedItem);
			} else if (cmd.equals(WIDTH)){
				String userInput = JOptionPane.showInputDialog("Width ?");
				controller.setWidth((Wall) clickedItem, userInput);
			} else if (cmd.equals(HEIGHT)){
				String userInput = JOptionPane.showInputDialog("Height ?");
				controller.setHeight((Floor) clickedItem, userInput);
			} else if (cmd.equals(CHANGETEXTURE)){
				String currentTexture=project.config("texture.selected");
				// On va assigner à l'objet cliqué la texture sélectionnée
				if (clickedItem instanceof Meshable){
					try {
						controller.setTexture((Meshable)clickedItem,currentTexture);
					} catch (SQLException ex) {
						Log.exception(ex);
					}
				}
				
			} else if (cmd.equals(MODIFY)) {
				ModificationFrame mf = new ModificationFrame((Primitive) clickedItem, daoFactory);
				mf.pack();
				mf.setVisible(true);
			} else if (cmd.equals(DUPLICATE)){
				controller.duplicate(clickedItem);
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
			if (value instanceof Area){
				Meshable item = (Meshable) value;
				sel = item.isSelected();
			} else if (value instanceof Floor){
				Floor fl = (Floor) value;
				sel = project.config("floor.current").equals(fl.getUID());
			}
			super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
			return this;
		}
	}
	
	private DefaultMutableTreeNode createNode(Geometric item){
		DefaultMutableTreeNode res = new DefaultMutableTreeNode(item.toString());
		res.setUserObject(item);
		boolean hasChildren = (item instanceof Room || item instanceof Floor);
		res.setAllowsChildren(hasChildren);
		this.nodes.put(item.getUID(), res);
		return res;
	}
	
	private DefaultMutableTreeNode createTree(Geometric root) throws SQLException{
		DefaultMutableTreeNode res = createNode(root);
		if (root instanceof Room){
			Room room = (Room) root;
			for (Meshable meshable : room.getAreas())
				res.add(createNode(meshable));
		}
		return res;
	}
	
	/**
	 * Create a world tree from the DAO
	 * @throws SQLException
	 */
	public void createTree() throws SQLException{
		Log.debug("createTree");
		root.removeAllChildren();
		for (Floor floor : this.daoFactory.getDao(Floor.class).queryForAll()){
			DefaultMutableTreeNode floorNode = createNode(floor);
			for (Room room : floor.getRooms()){
				floor.getRooms().refresh(room);
				floorNode.add(createTree(room));
			}
			for (Item item : floor.getItems()){
				floor.getItems().refresh(item);
				floorNode.add(createNode(item));
			}
			root.add(floorNode);
		}
	}
	
	/**
	 *  Create an object tree from the DAO
	 */
	public void createObjectTree() {
		root.removeAllChildren();
		Entity entity = (Entity) daoFactory.getByUID(project.config("entity.current"));
		for (Primitive primitive : entity.getPrimitives()) {
			DefaultMutableTreeNode primitiveNode = new DefaultMutableTreeNode(primitive.toString());
			primitiveNode.setUserObject(primitive);
			primitiveNode.setAllowsChildren(false);
			this.nodes.put(primitive.getUID(), primitiveNode);
			root.add(primitiveNode);
		}
	}
	
	/**
	 * Empty the tree
	 */
	public void clearTree() {
		Log.debug("clearTree");
		for (DefaultMutableTreeNode node : this.nodes.values()) {
			node.removeFromParent();
			this.nodes.remove(node);
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
	 * @return What the user click on
	 */
	public JPopupMenu createPopupMenu(Geometric geo){
		if (geo instanceof Binding)
			return null;
		
		PopupListener listener = new PopupListener();
		JPopupMenu res = new JPopupMenu();
		
		res.add(createJMenuItem(DELETE, DELETE, listener));
		if (geo instanceof Room){
			res.add(createJMenuItem(RENAME, RENAME, listener));
		} else if (geo instanceof Meshable){
			String action = ((Meshable) geo).isVisible() ? HIDE : SHOW;
			res.add(createJMenuItem(action, action, listener));
			if (geo instanceof Wall){
				res.add(createJMenuItem("Edit width", WIDTH, listener));
			}
			res.add(createJMenuItem("Change Texture",CHANGETEXTURE,listener));
		} else if (geo instanceof Floor){
			res.add(createJMenuItem(HEIGHT, HEIGHT, listener));
		}
		
		if (geo instanceof Primitive) {
			res.add(createJMenuItem("Modify", MODIFY, listener));
			res.add(createJMenuItem("Duplicate", DUPLICATE, listener));
		}
		
		return res;
	}
	
	/**
	 * Constructor of the main class ObjectTree
	 * @param newController 
	 * @param project 
	 */
	public ObjectTreeView(ObjectTreeController newController, Project project) {
		super(root);
		
		controller = newController;
		this.project = project;
		
		try {
			this.daoFactory = project.getGeometryDAO();
			this.daoFactory.addObserver(this);
		} catch (SQLException ex) {
			Log.exception(ex);
		}
		
		// Create a tree that allows one selection at a time.
		getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		setCellRenderer(new GeometricRenderer());
		
		// Listen for when the selection changes
		addTreeSelectionListener(controller);
		setRootVisible(false);
		setShowsRootHandles(true);

		// add the mouse listener to the tree
		addMouseListener(controller);
		// add key listener
		addKeyListener(controller);
		
	}
	
	/**
	 * @param path A path in the tree to a geometric
	 * @return The geometric associated
	 */
	public Geometric getGeometric(TreePath path){
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
		return (Geometric) node.getUserObject();
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		List<Change> changes = (List<Change>) arg1;
		
		for (Change change : changes){
			Geometric changed = change.getItem();
			
			try {
				GeometricDAO dao = this.daoFactory.getDao(changed.getClass());
				dao.refresh(changed);
			} catch (SQLException ex) {
				Log.exception(ex);
			}
			
			/* An object has been update: update the linked object in TreeView */
			if (change.isUpdate()){
				DefaultMutableTreeNode node = this.nodes.get(changed.getUID());
				if (node != null){
					node.setUserObject(changed);
					refreshUI(node);
				}
			}
			
			/* An object has been deleted: remove from tree */
			else if (change.isDeletion()){
				DefaultMutableTreeNode node = this.nodes.get(changed.getUID());
				if (node != null){
					TreeNode parentNode = node.getParent();
					node.removeFromParent();
					this.nodes.remove(node);
					refreshUI(parentNode);
				}
			}
			/* Creation: insert in right place in tree */
			else if (change.isCreation() && ! this.nodes.containsKey(changed.getUID())){
				Log.debug("Tree view create node for %s", changed.getUID());
				if (! isShown(changed))
					continue;
				
				DefaultMutableTreeNode newNode = null;
				try {newNode = createTree(changed);}
				catch (SQLException err){
					Log.exception(err); 
					continue;
				}
				
				DefaultMutableTreeNode parentNode = root;
				if (changed instanceof Room){
					Room room = (Room) changed;
					parentNode = this.nodes.get(room.getFloor().getUID());
				} else if (changed instanceof Item){
					Item item = (Item) changed;
					parentNode = this.nodes.get(item.getFloor().getUID());
				}
				
				parentNode.add(newNode);
				refreshUI(parentNode);
			}
		}
	}
	
	/**
	 * Return whether a geometric object is shown in tree view or not
	 * @param item A geometric item
	 * @return True if this geometric should be displayed in the tree
	 */
	private Boolean isShown(Geometric item){
		return (
			item instanceof Floor ||
			item instanceof Room  ||
			item instanceof Area  ||
			item instanceof Item  ||
			item instanceof Primitive
		);
	}
	
	/**
	 * Notify UI class that a node has changed
	 * @param changed The node who has changed
	 */
	private void refreshUI(TreeNode changed){
		((DefaultTreeModel) treeModel).reload(changed);
	}
}
