/**
 * 
 */
package be.ac.ulb.infof307.g03.GUI;

/**
 * @author fhennecker
 *
 */
public class MenuBarController {
	private MenuBarView _view;
	
	public MenuBarController(){
		_view = new MenuBarView(this);
	}
	
	public MenuBarView getView(){
		return _view;
	}
	
	/**
	 * Handler launched when menu item "New" is clicked
	 */
	public void onNew() {
		System.out.println("new");
	}
	/**
	 * Handler launched when menu item "Open" is clicked
	 */
	public void onOpen() {
		System.out.println("open");
	}
	/**
	 * Handler launched when menu item "Save" clicked
	 */
	public void onSave() {
		System.out.println("save");
	}
	/**
	 * Handler launched when menu item "Quit" clicked
	 */
	public void onQuit() {
		System.exit(0);
	}
	/**
	 * Handler launched when menu item "Undo" clicked
	 */
	public void onUndo() {
		System.out.println("undo");
	}
	/**
	 * Handler launched when menu item "Redo" clicked
	 */
	public void onRedo() {
		System.out.println("redo");
	}
}
