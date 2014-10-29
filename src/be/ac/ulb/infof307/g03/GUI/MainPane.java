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

import com.jme3.app.Application;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;

/**
 * @author pierre
 *
 */
public class MainPane extends JPanel {
	private JSplitPane _splitPane;
	private JScrollPane _listScrollPane;
	private JPanel _thirdDimensionPane;
	
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
        
        AppSettings settings = new AppSettings(true);
        settings.setWidth(640);
        settings.setHeight(480);
        Canvas3D canvas = new Canvas3D();
        canvas.setSettings(settings);
        canvas.createCanvas();
        JmeCanvasContext context = (JmeCanvasContext) canvas.getContext();
        context.setSystemListener(canvas);
        Dimension dimension = new Dimension(640, 480);
        context.getCanvas().setPreferredSize(dimension);
        canvas.startCanvas();
        
        
        _thirdDimensionPane = new JPanel();
        _thirdDimensionPane.setMinimumSize(minimumSize);
        _thirdDimensionPane.add(context.getCanvas());
       
		_splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,_listScrollPane, _thirdDimensionPane);
		_splitPane.setOneTouchExpandable(true);
		_splitPane.setDividerLocation(150);
		_splitPane.setPreferredSize(new Dimension(800, 400));
		
		add(_splitPane, BorderLayout.PAGE_START);
	}

}
