package be.ac.ulb.infof307.g03.models;

import static org.junit.Assert.*;

import java.sql.SQLException;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

public class TestGeometricTree {
	private ConnectionSource _db;
	private GeometryDAO _dao;

	@Before
	public void setUp() throws Exception {
		_db = new JdbcConnectionSource("jdbc:sqlite::memory:");
		GeometryDAO.migrate(_db);
		_dao = new GeometryDAO(_db);
	}

	@After
	public void tearDown() throws Exception {
		_db.close();
	}
	
	/**
	 * Create a new Project structured like this:
	 * - Group "Toplevel"
	 *    - Group "Sublevel 1"
	 *        - Ground
	 *        - Line (0,0,0) (0,0,1)
	 *        - Group "Sublevel 2"
	 *            - Wall
	 *            - 4 lines (square) in 0,0,0 1,1,0
	 * @throws SQLException
	 */
	private void create_basic_project() throws SQLException{
		Group top = new Group("Toplevel");
		_dao.create(top);
		
		Group sub1 = new Group("Sublevel 1");
		_dao.addShapeToGroup(top, sub1);
		
		Group sub2 = new Group("Sublevel 2");
		_dao.addShapeToGroup(sub1, sub2);
		
		Point o = new Point(0, 0, 0),
			  x = new Point(1, 0, 0),
			  y = new Point(0, 1, 0),
			  xy = new Point(1, 1, 0);
		
		_dao.addShapeToGroup(sub2, new Line(o, x));
		_dao.addShapeToGroup(sub2, new Line(x, xy));
		_dao.addShapeToGroup(sub2, new Line(xy, y));
		_dao.addShapeToGroup(sub2, new Line(y, o));
		
		_dao.addShapeToGroup(sub1, new Line(o, xy));
		
		_dao.create(new Wall(sub2, 1));
		_dao.create(new Ground(sub1));
	}

	/**
	 * Mock a Tree Model Listener, so that we store the last argument 
	 * for each of the required methods, or null if it has not been called yet.
	 * @author Titouan Christophe
	 */
	class MockTreeModelListener implements TreeModelListener {
		public int calls = 0;
		public TreeModelEvent change = null, insert = null, remove = null, struct = null;
		public void reset(){change = insert = remove = struct = null; calls = 0;}
		@Override
		public void treeNodesChanged(TreeModelEvent arg0) {change = arg0; calls++;}
		@Override
		public void treeNodesInserted(TreeModelEvent arg0) {insert = arg0; calls++;}
		@Override
		public void treeNodesRemoved(TreeModelEvent arg0) {remove = arg0; calls++;}
		@Override
		public void treeStructureChanged(TreeModelEvent arg0) {struct = arg0; calls++;}
	}
	
	@Test
	public void test_tree_structure() throws SQLException {
		create_basic_project();
		GeometricTree treemodel = new GeometricTree(_dao);
		
		Object root = treemodel.getRoot();
		assertEquals("Geometry", root);
		assertEquals(1, treemodel.getChildCount(root));
		assertFalse(treemodel.isLeaf(root));
		
		Object top = treemodel.getChild(root, 0);
		assertEquals(Group.class, top.getClass());
		assertEquals(1, treemodel.getChildCount(top));
		
		Object sub1 = treemodel.getChild(top, 0);
		assertEquals(Group.class, sub1.getClass());
		assertEquals(3, treemodel.getChildCount(sub1));
		
		Object obj = treemodel.getChild(sub1, 0);
		assertEquals(Ground.class, obj.getClass());
		assertTrue(treemodel.isLeaf(obj));
	}
	
	@Test
	public void test_tree_indexes() throws SQLException {
		create_basic_project();
		GeometricTree treemodel = new GeometricTree(_dao);
		
		Ground gnd = (Ground) _dao.getByUID("gnd-1");
		assertNotNull(gnd);
		
		Group grp = (Group) _dao.getByUID("grp-2");
		assertNotNull(grp);
		assertEquals(0, treemodel.getIndexOfChild(grp, gnd));
		
		Group subgrp = (Group) _dao.getByUID("grp-3");
		assertEquals(2, treemodel.getIndexOfChild(grp, subgrp));
		
		Object root = treemodel.getRoot();
		Group top = _dao.getGroup("Toplevel");
		assertEquals(0, treemodel.getIndexOfChild(root, top));
	}

	@Test
	public void test_update() throws SQLException{
		create_basic_project();
		GeometricTree treemodel = new GeometricTree(_dao);
		_dao.addObserver(treemodel);
		
		MockTreeModelListener mock = new MockTreeModelListener();
		treemodel.addTreeModelListener(mock);
		
		// Simulate observable notifications, assert the listener get the right events
		Group grp = (Group) _dao.getRootNodes().get(0);
		grp.setName("TestTest");
		assertEquals(1, _dao.update(grp));
		_dao.notifyObservers();
		assertNotNull(mock.change);
		
		// Detach the listener, it should not receive anything
		mock.reset();
		treemodel.removeTreeModelListener(mock);
		treemodel.update(null, null);
		_dao.notifyObservers();
		assertEquals(0, mock.calls);
	}
}
