/**
 * 
 */
package be.ac.ulb.infof307.g03.models;

/**
 * @author Titouan Christophe
 * A concrete class to test the Ordered models
 */
public class ConcreteOrdered extends Ordered {

	/**
	 * Default constructor
	 */
	public ConcreteOrdered() {super();}

	@Override
	public String getUIDPrefix() {return "ord";}

}
