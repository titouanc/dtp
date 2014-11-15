package be.ac.ulb.infof307.g03.models;

import java.util.Observable;
import java.util.Observer;

/**
 * Mock class to test an Observable object. 
 * Simply store the last argument given to the update() method,
 * and count the number of calls made
 * @author Titouan Christophe
 * @param <Type> Expected type for update() argument
 */
class MockObserver<Type> implements Observer {
	public Type changes = null;
	private Integer _nCalls = 0;
	
	@Override
	public void update(Observable arg0, Object arg1) {
		changes = (Type) arg1;
		_nCalls++;
	}
	
	public void reset(){
		changes = null;
		_nCalls = 0;
	}
	
	public Boolean hasBeenCalled(){
		return _nCalls > 0;
	}
	
	public int getCallNumber(){
		return _nCalls;
	}
}