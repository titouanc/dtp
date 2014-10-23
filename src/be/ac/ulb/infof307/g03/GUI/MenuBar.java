/**
 * 
 */
package be.ac.ulb.infof307.g03.GUI;

import java.awt.*;
import java.awt.event.*;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

/**
 * @author julianschembri
 * based on Oracle example
 */
public class MenuBar {
 
    public JMenuBar createMenuBar() {
        JMenuBar menuBar;
        JMenu menu;
        JMenuItem menuItem;

        // Create the menu bar
        menuBar = new JMenuBar();
 
        
        // Build the File menu
        menu = new JMenu("File");
        menu.setMnemonic(KeyEvent.VK_A);
        menu.getAccessibleContext().setAccessibleDescription("Manage the file.");
        menuBar.add(menu);
        
        // Build New action
        menuItem = new JMenuItem("New",KeyEvent.VK_T);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription("Create a new project.");
        menu.add(menuItem);
        
        // Build Open action
        menuItem = new JMenuItem("Open",KeyEvent.VK_T);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription("Open a saved project.");
        menu.add(menuItem);
        
        // Separator
        menu.addSeparator();
        
        // Build Save action
        menuItem = new JMenuItem("Save",KeyEvent.VK_T);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription("Save the current project.");
        menu.add(menuItem);
        
        // Separator
        menu.addSeparator();
        
        // Build Quit action
        menuItem = new JMenuItem("Quit",KeyEvent.VK_T);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription("Quit HomePlans application.");
        menu.add(menuItem);
        
        
        // Build the Edit menu
        menu = new JMenu("Edit");
        menu.setMnemonic(KeyEvent.VK_A);
        menu.getAccessibleContext().setAccessibleDescription("Edit the project.");
        menuBar.add(menu);
        
        // Build Undo action
        menuItem = new JMenuItem("Undo",KeyEvent.VK_T);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription("Undo last action.");
        menu.add(menuItem);
        
        // Build Redo action
        menuItem = new JMenuItem("Redo",KeyEvent.VK_T);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription("Redo last undo action.");
        menu.add(menuItem);
        
        
        // Build the Help menu
        menu = new JMenu("Help");
        menu.setMnemonic(KeyEvent.VK_A);
        menu.getAccessibleContext().setAccessibleDescription("Help.");
        menuBar.add(menu);
 
        return menuBar;
    }

}
