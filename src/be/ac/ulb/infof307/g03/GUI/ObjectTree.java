/**
 * 
 */
package be.ac.ulb.infof307.g03.GUI;

import java.awt.GridLayout;
import java.sql.SQLException;

import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import be.ac.ulb.infof307.g03.models.*;

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
	GeometryDAO _dao;
	
	public ObjectTree(Project project){
		super(new GridLayout(1,0));
		try {
			_dao = project.getGeometryDAO();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Create the nodes.
        _topNode = new DefaultMutableTreeNode("My home");
        try {
        	createNodes();
        } catch (SQLException err){
        	
        }
        	
        //Create a tree that allows one selection at a time.
        _tree = new JTree(_topNode);
        _tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
 
        //Listen for when the selection changes. TODO
        _tree.addTreeSelectionListener(this);
 
        //Add the tree pane to this panel.
        add(_tree);
	}
	
	private void createNodes() throws SQLException{
		_topNode = new DefaultMutableTreeNode("Geometry");
		for (Shape shape : _dao.getRootNodes()){
			createNodes(_topNode, shape);
		}
	}

	private void createNodes(DefaultMutableTreeNode top, Shape shape) throws SQLException {
		DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(shape.toString());
		if (shape.getClass() == Group.class){
			Group group = (Group) shape;
			for (Shape subShape : _dao.getShapesForGroup(group))
				createNodes(newNode, subShape);
		}
		top.add(newNode);
    }
         

	@Override
	public void valueChanged(TreeSelectionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
