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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
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
	 * Tree Model backend (proxy on DAO) for tree view
	 * @author Titouan Christophe
	 */
	class ShapeTreeModel implements TreeModel, Observer {
		private GeometryDAO _dao;
		private Set<TreeModelListener> _listeners;
		private Map<Object, List<Geometric>> _cache;
		
		public ShapeTreeModel(GeometryDAO geometry) {
			_dao = geometry;
			_dao.addObserver(this);
			
			_listeners = new HashSet<TreeModelListener>();
			_cache = new Hashtable<Object, List<Geometric>>();
		}

		private List<Geometric> getNodes(Object parent) {
			if (_cache.containsKey(parent))
				return _cache.get(parent);
			
			List<Geometric> res = new ArrayList<Geometric>();
			Class<? extends Object> type = parent.getClass();
			try {
				if (type == String.class){
					res.addAll(_dao.getWalls());
					res.addAll(_dao.getGrounds());
				}
				else if (type == Group.class)
					res.addAll(_dao.getShapesForGroup((Group) parent));
				else if (type == Wall.class || type == Ground.class){
					Grouped item = (Grouped) parent;
					res.add(item.getGroup());
				}
			} catch (SQLException err) {}
			
			if (res.size() > 0)
				_cache.put(parent, res);

			return res;
		}

		@Override
		public void addTreeModelListener(TreeModelListener l) {
			_listeners.add(l);
		}

		@Override
		public Object getChild(Object parent, int index) {
			List<Geometric> children = getNodes(parent);
			return children.get(index);
		}

		@Override
		public int getChildCount(Object parent) {
			return getNodes(parent).size();
		}

		@Override
		public int getIndexOfChild(Object parent, Object child) {
			List<Geometric> children = getNodes(parent);
			for (int i = 0; i < children.size(); i++)
				if (child.equals(children.get(i)))
					return i;
			return -1;
		}

		@Override
		public Object getRoot() {
			return "Geometry";
		}

		@Override
		public boolean isLeaf(Object node) {
			Class<? extends Object> type = node.getClass();
			if (type == String.class)
				return false;
			Geometric geo = (Geometric) node;
			return geo.isLeaf();
		}

		@Override
		public void removeTreeModelListener(TreeModelListener l) {
			_listeners.remove(l);
		}

		@Override
		public void valueForPathChanged(TreePath path, Object newValue) {}

		/**
		 * When DAO notifies data changes:
		 * - invalidate cache
		 * - notify all listeners
		 */
		@Override
		public void update(Observable o, Object arg) {
			if (_cache.containsKey(arg))
				_cache.remove(arg);
			
			TreeModelEvent e = new TreeModelEvent(this, new Object[] { arg });
			for (TreeModelListener l : _listeners)
				l.treeStructureChanged(e);
		}
	}
	
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
	private ShapeTreeModel _model;
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
			_model = new ShapeTreeModel(project.getGeometryDAO());
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
