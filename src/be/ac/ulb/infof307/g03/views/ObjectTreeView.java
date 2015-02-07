/**
 * 
 */
package be.ac.ulb.infof307.g03.views;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSlider;
import javax.swing.JTree;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;

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
	class ModificationFrame extends JFrame implements ActionListener, PropertyChangeListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Primitive primitive = null;
		private JFormattedTextField scalex, scaley, scalez, posz, rotx, roty, rotz;
		private JSlider sliderRotx, sliderRoty, sliderRotz;
		
		private static final String OK = "ok";
		private static final String CANCEL = "cancel";
		
		public ModificationFrame(Primitive prim) {
			super("Modification Panel");
			this.primitive = prim;
			
			JPanel panel = new JPanel();
			this.add(panel);
			Dimension dimension = new Dimension(400,300);
			this.setPreferredSize(dimension);
			this.setMaximumSize(dimension);
			this.setMinimumSize(dimension);
			this.setResizable(false);
			GridBagLayout layout = new GridBagLayout();
			panel.setLayout(layout);
			
			GridBagConstraints constraints = new GridBagConstraints();
			constraints.gridx = 0;
			constraints.gridy = 0;
			constraints.gridwidth = 1;
			
			panel.add(new JLabel("Scale x : "),constraints);
			++constraints.gridy;
			panel.add(new JLabel("Scale y : "), constraints);
			++constraints.gridy;
			panel.add(new JLabel("Scale z : "), constraints);
			++constraints.gridy;
			panel.add(new JLabel("Translation z : "), constraints);
			constraints.gridwidth = 1;
			++constraints.gridy;
			panel.add(new JLabel("Rotation x : "), constraints);
			++constraints.gridy;
			panel.add(new JLabel("Rotation y : "), constraints);
			++constraints.gridy;
			panel.add(new JLabel("Rotation z : "), constraints);
			
			constraints.gridx = 2;
			constraints.gridy = 0;
			scalex = new JFormattedTextField(prim.getScale().x);
			scalex.setColumns(3);
			panel.add(scalex, constraints);
			scalex.addPropertyChangeListener(this);
			++constraints.gridy;
			scaley = new JFormattedTextField(prim.getScale().y);
			scaley.setColumns(3);
			panel.add(scaley,constraints);
			scaley.addPropertyChangeListener(this);
			++constraints.gridy;
			scalez = new JFormattedTextField(prim.getScale().z);
			scalez.setColumns(3);
			panel.add(scalez,constraints);
			scalez.addPropertyChangeListener(this);
			++constraints.gridy;
			posz = new JFormattedTextField(prim.getTranslation().z);
			posz.setColumns(3);
			panel.add(posz,constraints);
			posz.addPropertyChangeListener(this);
			++constraints.gridy;
			rotx = new JFormattedTextField(prim.getRotation().x);
			rotx.setColumns(3);
			panel.add(rotx,constraints);
			rotx.addPropertyChangeListener(this);
			++constraints.gridy;
			roty = new JFormattedTextField(prim.getRotation().y);
			roty.setColumns(3);
			panel.add(roty,constraints);
			roty.addPropertyChangeListener(this);
			++constraints.gridy;
			rotz = new JFormattedTextField(prim.getRotation().z);
			rotz.setColumns(3);
			panel.add(rotz,constraints);
			rotz.addPropertyChangeListener(this);
			
			constraints.gridx = 1;
			constraints.gridy = 4;
			sliderRotx = new JSlider(JSlider.HORIZONTAL, -180, 180,(int)prim.getRotation().x);
			sliderRotx.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					
					rotx.setValue(sliderRotx.getValue());
				}
			});
			panel.add(sliderRotx ,constraints);
			++constraints.gridy;
			sliderRoty = new JSlider(JSlider.HORIZONTAL, -180, 180, (int)prim.getRotation().y);
			sliderRoty.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					roty.setValue(sliderRoty.getValue());
				}
			});
			panel.add(sliderRoty ,constraints);
			++constraints.gridy;
			sliderRotz = new JSlider(JSlider.HORIZONTAL, -180, 180, (int)prim.getRotation().z);
			sliderRotz.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					rotz.setValue(sliderRotz.getValue());
				}
			});
			panel.add(sliderRotz ,constraints);
			
			++constraints.gridy;
			JButton applyButton = new JButton("Ok");
			applyButton.setActionCommand(OK);
			applyButton.addActionListener(this);
			panel.add(applyButton, constraints);
			++constraints.gridx;
			JButton cancelButton = new JButton("Cancel");
			cancelButton.setActionCommand(CANCEL);
			cancelButton.addActionListener(this);
			panel.add(cancelButton, constraints);
		}
		
		public void applyModif() {
			primitive.setScale(new Vector3f(((Number)scalex.getValue()).floatValue(),
					((Number)scaley.getValue()).floatValue(),
					((Number)scalez.getValue()).floatValue()));
			primitive.setTranslation(new Vector3f(	primitive.getTranslation().getX(),
					primitive.getTranslation().getY(),
					((Number)posz.getValue()).floatValue()));
			primitive.setRotation(new Vector3f(	((Number)rotx.getValue()).floatValue()*FastMath.PI/180,
					((Number)roty.getValue()).floatValue()*FastMath.PI/180,
					((Number)rotz.getValue()).floatValue()*FastMath.PI/180));
			try {
				dao.update(primitive);
				dao.notifyObservers(primitive);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			String cmd = e.getActionCommand();
			if (cmd.equals(OK)) {
				this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
			} else if (cmd.equals(CANCEL)) {
				// restore
				this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
			}
		}
		
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			applyModif();
		}

	}
	
	private static final long serialVersionUID = 1L;
	
	// Attribute
	private ObjectTreeController controller;
	private Project project;
	private GeometryDAO dao;
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
				controller.showGrouped((Meshable) clickedItem);
			} else if (cmd.equals(HIDE)){
				controller.hideGrouped((Meshable) clickedItem);
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
				ModificationFrame mf = new ModificationFrame((Primitive) clickedItem);
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
	
	private DefaultMutableTreeNode _createNode(Geometric item){
		DefaultMutableTreeNode res = new DefaultMutableTreeNode(item.toString());
		res.setUserObject(item);
		boolean hasChildren = (item instanceof Room || item instanceof Floor);
		res.setAllowsChildren(hasChildren);
		this.nodes.put(item.getUID(), res);
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
	
	/**
	 * Create a tree from the dao
	 * @throws SQLException
	 */
	public void createTree() throws SQLException{
		Log.debug("createTree");
		root.removeAllChildren();
		for (Floor floor : this.dao.getFloors()){
			DefaultMutableTreeNode floorNode = _createNode(floor);
			for (Room room : floor.getRooms()){
				floor.getRooms().refresh(room);
				floorNode.add(_createTree(room));
			}
			for (Item item : floor.getItems()){
				floor.getItems().refresh(item);
				floorNode.add(_createNode(item));
			}
			root.add(floorNode);
		}
	}
	
	/**
	 * Create the object tree
	 */
	public void createObjectTree() {
		root.removeAllChildren();
		Entity entity = (Entity) dao.getByUID(project.config("entity.current"));
		for (Primitive primitive : entity.getPrimitives()) {
			DefaultMutableTreeNode primitiveNode = new DefaultMutableTreeNode(primitive.toString());
			primitiveNode.setUserObject(primitive);
			primitiveNode.setAllowsChildren(false);
			this.nodes.put(primitive.getUID(), primitiveNode);
			root.add(primitiveNode);
		}
	}
	
	/**
	 *  Clear the object tree
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
	 * @return The pop up menu
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
			this.dao = project.getGeometryDAO();
			this.dao.addObserver(this);
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
	 * @param path
	 * @return the selected geometric
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
				this.dao.refresh(changed);
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
				try {newNode = _createTree(changed);}
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
	 * Return whether a geometric object is hown in tree view or not
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
