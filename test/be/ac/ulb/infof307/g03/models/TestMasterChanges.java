package be.ac.ulb.infof307.g03.models;

import static org.junit.Assert.*;

import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestMasterChanges extends DAOTest {
	@Test
	public void test_no_change() throws SQLException {
		MockObserver<Change> mock = new MockObserver<Change>();
		this.master.addObserver(mock);
		
		GeometricDAO<Point> dao = this.master.getDao(Point.class);
		dao.create(new Point());
		master.notifyObservers();
		assertFalse(mock.hasBeenCalled());
	}

	@Test
	public void test_notify_change() throws SQLException {
		MockObserver<Change> mock = new MockObserver<Change>();
		this.master.addObserver(mock);
		
		GeometricDAO<Point> dao = this.master.getDao(Point.class);
		dao.insert(new Point());
		master.notifyObservers();
		assertTrue(mock.hasBeenCalled());
	}
}
