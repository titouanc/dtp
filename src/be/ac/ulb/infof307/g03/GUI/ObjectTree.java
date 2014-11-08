/**
 * 
 */
package be.ac.ulb.infof307.g03.GUI;

import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import be.ac.ulb.infof307.g03.models.*;

/**
 * @author pierre, titou
 * 
 */
public class ObjectTree extends JPanel implements TreeSelectionListener {
	
	/**
	 * Tree Model backend (proxy on DAO) for tree view
	 * @author Titouan Christophe
	 */
	class ShapeTreeModel implements TreeModel, Observer {
		private GeometryDAO _dao;
		private Set<TreeModelListener> _listeners;
		private Map<Object, List<Shape>> _cache;
		
		public ShapeTreeModel(GeometryDAO geometry) {
			_dao = geometry;
			_dao.addObserver(this);
			
			_listeners = new HashSet<TreeModelListener>();
			_cache = new Hashtable<Object, List<Shape>>();
		}

		private List<Shape> getNodes(Object parent) {
			if (_cache.containsKey(parent))
				return _cache.get(parent);
			
			List<Shape> res = new ArrayList<Shape>();
			try {
				if (parent.getClass() == String.class)
					res = _dao.getRootNodes();
				else if (parent.getClass() == Group.class)
					res = _dao.getShapesForGroup((Group) parent);
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
			List<Shape> children = getNodes(parent);
			return children.get(index);
		}

		@Override
		public int getChildCount(Object parent) {
			return getNodes(parent).size();
		}

		@Override
		public int getIndexOfChild(Object parent, Object child) {
			List<Shape> children = getNodes(parent);
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
			return type != Group.class && type != String.class;
		}

		@Override
		public void removeTreeModelListener(TreeModelListener l) {
			_listeners.remove(l);
		}

		@Override
		public void valueForPathChanged(TreePath path, Object newValue) {}

		/**
		 * When DAO notifies data changes:
		 * - notify all listeners
		 * - invalidate cache
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
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTree _tree;
	private ShapeTreeModel _model;

	public ObjectTree(Project project) {
		super(new GridLayout(1, 0));
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
		
		// Listen for when the selection changes. TODO
		_tree.addTreeSelectionListener(this);
		
		// Add the tree pane to this panel.
		add(_tree);
	}

	@Override
	public void valueChanged(TreeSelectionEvent arg0) {
		// TODO Auto-generated method stub

	}

}
