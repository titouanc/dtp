/**
 * 
 */
package be.ac.ulb.infof307.g03.models;
import java.sql.SQLException;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

/**
 * @author Titouan Christophe
 * @param <Batman> The mapped Geometric type
 */
public class GeometricDAO<Batman extends Geometric> extends BaseDaoImpl<Batman, Integer>{
	MasterDAO master = null;

	/**
	 * @param connectionSource
	 * @param dataClass
	 * @throws SQLException
	 */
	public GeometricDAO(ConnectionSource connectionSource, Class<Batman> dataClass) throws SQLException {
		super(connectionSource, dataClass);
	}
	
	/**
	 * Setup the master DAO (the one who dispatches changes notifications)
	 * @param master
	 */
	public void setMaster(MasterDAO master){
		this.master = master;
	}
	
	/**
	 * Insert an object in the database, and notify the master of the
	 * creation for subsequent notifyObservers() invocations
	 * @param nanana The object to insert
	 * @return The number of affected rows
	 * @throws SQLException
	 */
	public int insert(Object nanana) throws SQLException {
		int res = super.create((Batman) nanana);
		this.master.addChange(Change.create((Batman) nanana));
		return res;
	}
	
	/**
	 * Update an object in the database, and notify the master of the
	 * modifications for subsequent notifyObservers() invocations
	 * @param nanana The object to modify
	 * @return The number of affected rows
	 * @throws SQLException
	 */
	public int modify(Object nanana) throws SQLException {
		int res = super.update((Batman) nanana);
		this.master.addChange(Change.update((Batman) nanana));
		return res;
	}
	
	/**
	 * Remove an object from the database, and notify the master of the
	 * deletion for subsequent MasterDAO.notifyObservers() invocations
	 * @param nanana The object to remove
	 * @return The number of affected rows
	 * @throws SQLException
	 */
	public int remove(Object nanana) throws SQLException {
		int res = super.delete((Batman) nanana);
		this.master.addChange(Change.delete((Batman) nanana));
		return res;
	}
}
