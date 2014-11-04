/**
 * 
 */
package be.ac.ulb.infof307.g03.GUI;

import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

/**
 * @author pierre
 *
 */
public class ObjectTree extends JPanel implements TreeSelectionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTree _tree;
	DefaultMutableTreeNode _topNode;
	
	public ObjectTree(){
		super(new GridLayout(1,0));
		//Create the nodes.
        _topNode = new DefaultMutableTreeNode("My home");
        createNodes(_topNode);
 
        //Create a tree that allows one selection at a time.
        _tree = new JTree(_topNode);
        _tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
 
        //Listen for when the selection changes. TODO
        _tree.addTreeSelectionListener(this);
 
 
 
        //Add the tree pane to this panel.
        add(_tree);
		
	}

	private void createNodes(DefaultMutableTreeNode top) {
        DefaultMutableTreeNode shapeName = null;
        DefaultMutableTreeNode groupName = null;
 
        shapeName = new DefaultMutableTreeNode("Group 1");
        top.add(shapeName);
 

        groupName = new DefaultMutableTreeNode("Shape 1");
        shapeName.add(groupName);
 

        groupName = new DefaultMutableTreeNode("Shape 2");
        shapeName.add(groupName);
 
        groupName = new DefaultMutableTreeNode("Shape 3");
        shapeName.add(groupName);
 

        groupName = new DefaultMutableTreeNode("Shape 4");
        shapeName.add(groupName);

        groupName = new DefaultMutableTreeNode("Shape 5");
        shapeName.add(groupName);
 
        groupName = new DefaultMutableTreeNode("Shape 6");
        shapeName.add(groupName);
 
        shapeName = new DefaultMutableTreeNode("Living Room");
        top.add(shapeName);
 
        //VM
        groupName = new DefaultMutableTreeNode("Shape 1");
        shapeName.add(groupName);
 
        //Language Spec
        groupName = new DefaultMutableTreeNode("Shape 2");
        shapeName.add(groupName);
    }
         

	@Override
	public void valueChanged(TreeSelectionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
