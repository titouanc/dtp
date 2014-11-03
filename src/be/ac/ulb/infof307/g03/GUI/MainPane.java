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
 * This class implements the main view of the application, namely a splitview.
 * It contains a 3D view on the right.
 */
public class MainPane extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private JSplitPane _splitPane;
	private JScrollPane _listScrollPane;
	private JPanel _thirdDimensionPane;
	private Canvas3D _canvas;
	
	public MainPane(){
		super(new BorderLayout());
		
		// Left menu		
        String[] listShape = new String[] {"Rectangle", "Rectangle", "Rond", "Cercle"};
        JList list = new JList(listShape);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        _listScrollPane = new JScrollPane(list);

        // jme3 
        AppSettings settings = new AppSettings(true);
        settings.setWidth(640);
        settings.setHeight(480);
        settings.setFrameRate(60);
        
        // Creating Canvas
        _canvas = new Canvas3D();
        _canvas.setSettings(settings);
        _canvas.createCanvas();
        JmeCanvasContext context = (JmeCanvasContext) _canvas.getContext();
        context.setSystemListener(_canvas);
        Dimension dimension = new Dimension(640, 480);
        context.getCanvas().setPreferredSize(dimension);
        _canvas.startCanvas();
        

        add(context.getCanvas(),BorderLayout.EAST);
       
        // Body
		_splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,_listScrollPane, _thirdDimensionPane);
		_splitPane.setOneTouchExpandable(true);
		_splitPane.setDividerLocation(150);
		_splitPane.setPreferredSize(new Dimension(150, 480));
		
		add(_splitPane);

	}

}
