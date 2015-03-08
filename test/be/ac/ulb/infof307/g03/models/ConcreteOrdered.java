/**
 * 
 */
package be.ac.ulb.infof307.g03.models;

import com.j256.ormlite.table.DatabaseTable;

/**
 * @author Titouan Christophe
 * A concrete class to test the Ordered models
 */
@DatabaseTable(daoClass=GeometricDAO.class)
public class ConcreteOrdered extends Indexed {

	/**
	 * Default constructor
	 */
	public ConcreteOrdered() {super();}

	@Override
	public String getUIDPrefix() {return "ord";}

}
