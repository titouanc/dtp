/**
 * 
 */
package be.ac.ulb.infof307.g03.GUI;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.*;

/**
 * @author julianschembri, pierre
 *
 */
public class GUI {
	/**
	 * Creation of top level components
	 */
	public GUI() {
		// Create and set up the window
		JFrame frame = new JFrame("HomePlans");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Create the menuBar
        MenuBar menuBar = new MenuBar();
        frame.setJMenuBar(menuBar.createMenuBar());
        
        // Creation du main panel
        // http://docs.oracle.com/javase/tutorial/uiswing/components/toplevel.html
        JPanel contentPane = new JPanel(new BorderLayout());
        
        ToolsBar toolsBar = new ToolsBar();
        contentPane.add(toolsBar, BorderLayout.NORTH);
        
        // blank split plane 
//        String[] listShape = new String[] {"Rectangle", "Rectangle", "Rond", "Cercle"};
//        JList list = new JList(listShape);
//        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//         
//        JScrollPane listScrollPane = new JScrollPane(list);
//        JLabel blankJlabel = new JLabel();
//        blankJlabel.setHorizontalAlignment(JLabel.CENTER);
//        JScrollPane pictureScrollPane = new JScrollPane(blankJlabel);
//        //Create a split pane with the two scroll panes in it.
//        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,listScrollPane, pictureScrollPane);
//        splitPane.setOneTouchExpandable(true);
//        splitPane.setDividerLocation(150);
// 
//        //Provide minimum sizes for the two components in the split pane.
//        Dimension minimumSize = new Dimension(100, 50);
//        listScrollPane.setMinimumSize(minimumSize);
//        pictureScrollPane.setMinimumSize(minimumSize);
// 
//        //Provide a preferred size for the split pane.
//        splitPane.setPreferredSize(new Dimension(800, 400));
        MainPane workspace = new MainPane();

        
        //frame.setPreferredSize(new Dimension(900, 600));
        contentPane.add(workspace, BorderLayout.SOUTH);
        
        // ajoute le panel principal au frame
        frame.setContentPane(contentPane);
        
        // Display the window.
        frame.pack();
        frame.setVisible(true);
	}
}
