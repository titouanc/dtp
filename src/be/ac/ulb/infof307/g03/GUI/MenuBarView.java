package be.ac.ulb.infof307.g03.GUI;

import java.awt.event.*;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

/**
 * @author julianschembri, brochape, walter
 * based on Oracle examples
 * 
 * This class implement the menu bar for the HomePlans GUI
 */
public class MenuBarView extends JMenuBar {
	private static final long serialVersionUID = 1L;
	
	private MenuBarController controller; 
	
	/**
	 * Constructor of the class MenuBar
	 * Build all the menu and the menu items
	 * @param newController The controller of the view.
	 */
    public MenuBarView(MenuBarController newController) {
    	super();
    	
    	this.controller = newController;
    	
        JMenu menu;
        
        // Build the File menu
        menu = new JMenu("File");
        menu.getAccessibleContext().setAccessibleDescription("Manage the file.");
        this.add(menu);
        
        // Build New action
        menu.add(createMenuItem("New", KeyEvent.VK_N, MenuBarController.NEW, "Create a new project."));
        // Build Open action
        menu.add(createMenuItem("Open", KeyEvent.VK_O, MenuBarController.OPEN, "Open a saved project."));
        // Build Open action
        menu.add(createMenuItem("Demo",KeyEvent.VK_D, MenuBarController.DEMO, "Open the demo of the project."));
        // Separator
        menu.addSeparator();
        // Build Save action
        menu.add(createMenuItem("Save", KeyEvent.VK_S, MenuBarController.SAVE, "Save the current project."));
        menu.add(createMenuItem("Save As", KeyEvent.VK_A , MenuBarController.SAVE_AS, "Save the current project as a new file."));
        // Separator
        menu.addSeparator();
        // Build Import Action
        menu.add(createMenuItem("Import", KeyEvent.VK_I, MenuBarController.IMPORT, "Import an object."));
        // Separator
        menu.addSeparator();
        // Build Quit action
        menu.add(createMenuItem("Quit", KeyEvent.VK_Q, MenuBarController.QUIT, "Quit HomePlans application."));    
        
        // Build the Help menu
        menu = new JMenu("Help");
        menu.add(createMenuItem("Keybindings", KeyEvent.VK_K, MenuBarController.KEYBINDINGS, "Display the keybindings."));
        menu.add(createMenuItem("Tools", KeyEvent.VK_T, MenuBarController.TOOLS, "Display the help."));
        menu.add(createMenuItem("About", KeyEvent.VK_H, MenuBarController.ABOUT, "About HomePlans."));
        menu.getAccessibleContext().setAccessibleDescription("Help.");
        this.add(menu);
    }   
	
	/**
	 * Create a menu item binded with the relative handler
	 * @param label A String used as menu name
	 * @param keyEvent An int used as key number 
	 * @param cmd A String used as id
	 * @param descr A String used to describe the behavior of the menu item
	 * @return A JMenuItem instance
	 */
	private JMenuItem createMenuItem(String label, int keyEvent, String cmd, String descr ) {
    	JMenuItem menuItem;
    	menuItem = new JMenuItem(label, keyEvent);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(keyEvent, ActionEvent.CTRL_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription(descr);
        menuItem.addActionListener(this.controller);
        menuItem.setActionCommand(cmd);
    	return menuItem;
    }
}
