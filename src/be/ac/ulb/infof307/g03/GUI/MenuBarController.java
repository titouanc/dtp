/**
 * 
 */
package be.ac.ulb.infof307.g03.GUI;

/**
 * @author fhennecker, julian, pierre
 * @brief Controller of the MenuBar
 */
public class MenuBarController {
	private MenuBarView _view;
	
	/**
	 * Constructor of MenuBarController.
	 * It creates the view associated with the controller.
	 */
	public MenuBarController(){
		_view = new MenuBarView(this);
	}
	
	/**
	 * @return the controller's view
	 */
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
