/**
 * 
 */
package be.ac.ulb.infof307.g03.GUI;

import java.awt.event.*;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

/**
 * @author julianschembri
 * based on Oracle example
 */
public class MenuBar extends JMenuBar implements ActionListener {
	/**
	 * 
	 */
	
	private static final long serialVersionUID = 1L;
	
	static private final String _NEW  = "new" ;
	static private final String _OPEN = "open";
	static private final String _SAVE = "save";
	static private final String _QUIT = "quit";
	static private final String _UNDO = "undo";
	static private final String _REDO = "redo";
	
    public MenuBar() {
    	super();
    	
        JMenu menu;
        
        // Build the File menu
        menu = new JMenu("File");
        menu.getAccessibleContext().setAccessibleDescription("Manage the file.");
        this.add(menu);
        
        // Build New action
        menu.add(createMenuItem("New", KeyEvent.VK_N, _NEW, "Create a new project."));
        // Build Open action
        menu.add(createMenuItem("Open", KeyEvent.VK_O, _OPEN, "Open a saved project."));
        // Separator
        menu.addSeparator();
        // Build Save action
        menu.add(createMenuItem("Save", KeyEvent.VK_S, _SAVE, "Save the current project."));
        // Separator
        menu.addSeparator();
        // Build Quit action
        menu.add(createMenuItem("Quit", KeyEvent.VK_Q, _QUIT, "Quit HomePlans application."));
        
        
        // Build the Edit menu
        menu = new JMenu("Edit");
        menu.getAccessibleContext().setAccessibleDescription("Edit the project.");
        this.add(menu);
        
        // Build Undo action
        menu.add(createMenuItem("Undo", KeyEvent.VK_Z, _UNDO, "Undo last action."));
        // Build Redo action
        menu.add(createMenuItem("Redo", KeyEvent.VK_Y, _REDO, "Redo last undo action."));
        
        
        // Build the Help menu
        menu = new JMenu("Help");
        menu.getAccessibleContext().setAccessibleDescription("Help.");
        this.add(menu);
    }
    
	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd.equals(_NEW)) {
			actionNew();
		} else if (cmd.equals(_OPEN)) {
			actionOpen();
		} else if (cmd.equals(_SAVE)) {
			actionSave();
		} else if (cmd.equals(_QUIT)) {
			actionQuit();
		} else if (cmd.equals(_UNDO)) {
			actionUndo();
		} else if (cmd.equals(_REDO)) {
			actionRedo();
		}
	}    
	
	private JMenuItem createMenuItem(String label, int keyEvent, String cmd, String descr ) {
    	JMenuItem menuItem;
    	menuItem = new JMenuItem(label, keyEvent);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(keyEvent, ActionEvent.CTRL_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription(descr);
        menuItem.addActionListener(this);
        menuItem.setActionCommand(cmd);
    	return menuItem;
    }
	
	private void actionNew() {
		System.out.println("new");
	}
	private void actionOpen() {
		System.out.println("open");
	}
	private void actionSave() {
		System.out.println("save");
	}
	private void actionQuit() {
		System.exit(0);
	}
	private void actionUndo() {
		System.out.println("undo");
	}
	private void actionRedo() {
		System.out.println("redo");
	}

}
