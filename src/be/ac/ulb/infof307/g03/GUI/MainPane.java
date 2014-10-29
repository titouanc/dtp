/**
 * 
 */
package be.ac.ulb.infof307.g03.GUI;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;

/**
 * @author pierre
 *
 */
public class MainPane extends JPanel {
	private JSplitPane _splitPane;
	private JScrollPane _listScrollPane;
	private JScrollPane _thirdDimensionPane;
	public MainPane(){
		super(new BorderLayout());
		Dimension minimumSize = new Dimension(100, 50);
		
        String[] listShape = new String[] {"Rectangle", "Rectangle", "Rond", "Cercle"};
        JList list = new JList(listShape);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        _listScrollPane = new JScrollPane(list);
        _listScrollPane.setMinimumSize(minimumSize);
        
        JLabel blankJlabel = new JLabel();
        blankJlabel.setHorizontalAlignment(JLabel.CENTER);
        
        _thirdDimensionPane = new JScrollPane(blankJlabel);
        _thirdDimensionPane.setMinimumSize(minimumSize);
       
		_splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,_listScrollPane, _thirdDimensionPane);
		_splitPane.setOneTouchExpandable(true);
		_splitPane.setDividerLocation(150);
		_splitPane.setPreferredSize(new Dimension(800, 400));
		
		add(_splitPane, BorderLayout.PAGE_START);
	}

}
