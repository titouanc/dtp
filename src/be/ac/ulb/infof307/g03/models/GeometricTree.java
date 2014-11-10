package be.ac.ulb.infof307.g03.models;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * Tree Model backend (proxy on DAO)
 * @author Titouan Christophe
 */
public class GeometricTree implements TreeModel, Observer {
	private GeometryDAO _dao;
	private Set<TreeModelListener> _listeners;
	private Map<String, List<Geometric>> _cache;
	private final static String _ROOT = "Geometry";
	
	/**
	 * Create a new tree representation of content accessed through DAO
	 * @param geometry The DAO object to observe
	 */
	public GeometricTree(GeometryDAO geometry) {
		_dao = geometry;
		_dao.addObserver(this);
		
		_listeners = new HashSet<TreeModelListener>();
		_cache = new Hashtable<String, List<Geometric>>();
	}

	/**
	 * Translate given object into its caching key
	 * @param o A tree object
	 * @return The key for this object content in cache
	 */
	private String toKey(Object o){
		if (o instanceof String)
			return (String) o;
		if (o instanceof Geometric)
			return ((Geometric) o).getUID();
		return o.toString();
	}
	
	/**
	 * @param o An object from the tree
	 * @return True if it is the root
	 */
	private Boolean isRoot(Object o){
		return o == null || o.equals(_ROOT);
	}
	
	/**
	 * Query for children node, handle caching
	 * @param parent The node we're querying for children
	 * @return A list of Geometric object
	 */
	private List<Geometric> getNodes(Object parent) {
		String key = toKey(parent);
		
		// We have this information in cache, simply return it
		if (_cache.containsKey(key))
			return _cache.get(key);
		
		// Otherwise fetch information from database
		List<Geometric> res = new ArrayList<Geometric>();
		try {
			if (isRoot(parent))
				res.addAll(_dao.getRootNodes());
			else if (parent instanceof Group){
				Group grp = (Group) parent;
				Wall wall = _dao.getWall(grp);
				if (wall != null) res.add(wall);
				Ground gnd = _dao.getGround(grp);
				if (gnd != null) res.add(gnd);
				res.addAll(_dao.getShapesForGroup(grp));
			}
		} catch (SQLException err) {}
		
		// Save in cache before returning
		_cache.put(key, res);
		return res;
	}

	/**
	 * Invalidate cache for given object.
	 * @note If this object is in a group, its group cache is also flushed.
	 * @param node The cache associated to this node will be flushed
	 */
	private void invalidateCache(Object node){
		String key = toKey(node);
		if (_cache.containsKey(key))
			_cache.remove(key);

		// Invalidate parent (since its child list might have changed)
		if (node instanceof Geometric){
			Group parent = ((Geometric) node).getGroup();
			if (parent != null){
				key = toKey(parent);
				if (_cache.containsKey(key))
					_cache.remove(key);
			}
		}
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
		if (isRoot(child))
			return 0;
		
		String key = ((Geometric) child).getUID();
		List<Geometric> children = getNodes(parent);
		
		for (int i = 0; i < children.size(); i++)
			if (key.equals(children.get(i).getUID()))
				return i;
		return -1;
	}

	@Override
	public Object getRoot() {
		return _ROOT;
	}

	@Override
	public boolean isLeaf(Object node) {
		if (isRoot(node))
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

	private LinkedList<Object> getFullPath(Object o){
		if (o == null)
			o = _ROOT;
		LinkedList<Object> res = isRoot(o) ? 
				new LinkedList<Object>() : 
				getFullPath(((Geometric) o).getGroup());
		res.add(o);
		return res;
	}
	
	private TreeModelEvent _makeEvent(Geometric item){
		LinkedList<Object> fullPath = getFullPath(item);
		fullPath.removeLast();
		invalidateCache(item);
		
		return new TreeModelEvent(
			this, 
			fullPath.toArray(), 
			new int[] {getIndexOfChild(fullPath.getLast(), item)},
			new Object[] {item}
		);
	}
	
	private void _createToListeners(Geometric creation){
		TreeModelEvent event = _makeEvent(creation);
		System.out.println(event);
		for (TreeModelListener l : _listeners)
			l.treeNodesInserted(event);
	}
	
	private void _updateToListeners(Geometric updated){
		TreeModelEvent event = _makeEvent(updated);
		System.out.println(event);
		for (TreeModelListener l : _listeners)
			l.treeNodesChanged(event);
	}
	
	private void _deleteToListeners(Geometric deletion){
		TreeModelEvent event = _makeEvent(deletion);
		System.out.println(event);
		for (TreeModelListener l : _listeners)
			l.treeNodesRemoved(event);
	}
	
	/**
	 * When DAO notifies data changes:
	 * - invalidate cache
	 * - notify all listeners
	 */
	@Override
	public void update(Observable o, Object arg) {
		if (arg != null){
			ModelChange changes = (ModelChange) arg;
			for (Geometric g : changes.getCreates())
				_createToListeners(g);
			for (Geometric g : changes.getUpdates())
				_updateToListeners(g);
			for (Geometric g : changes.getDeletes())
				_deleteToListeners(g);
		}
	}
}
