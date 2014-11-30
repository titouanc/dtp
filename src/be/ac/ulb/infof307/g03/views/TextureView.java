package be.ac.ulb.infof307.g03.views;

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JButton;

import be.ac.ulb.infof307.g03.controllers.TextureController;
import be.ac.ulb.infof307.g03.models.Project;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.TextField;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * @author Walter, brochape
 * @brief view of the panel that ill open when user wants to change texture
 */
public class TextureView extends JPanel implements ItemListener {

	private static final long serialVersionUID = 1L;
	private TextureController _controller;
	private Project _project;
	
	private GridBagLayout _paneLayout;
	private ArrayList<String> colorFiles=   new ArrayList <String>();
	private ArrayList<String> textureFiles= new ArrayList <String>();
	
	final static String COLORPANEL = "Colors";
    final static String TEXTURESPANEL = "Textures";
    static String CURRENTMODE ="" ;
    
    @SuppressWarnings("rawtypes")
    static private JList _textureList = new JList();
	@SuppressWarnings("rawtypes")
	static private JList _colorList   = new JList();
    
    JPanel cards; //a panel that uses CardLayout

	/**
	 * @param newControler
	 * @param project
	 */
	public TextureView(TextureController newControler, Project project) {
		//Builds a JPane based on a CardLayout, which is a layout that mages 2+ panes using the same display space
    	super(new CardLayout());  	
    	
    	_controller = newControler;
    	_project = project;
        
    	//Uses the GridbagConstraints
    	_paneLayout = new GridBagLayout();
    	this.setLayout(_paneLayout);
		
    	//Fixes the width of the pane
    	this.setPreferredSize(new Dimension(this.getHeight(),100));
    	
    	// Get filenames
		getAllFiles();
		
		this.addTypeSelection();
		this.addMaterialChoice();
		CURRENTMODE=COLORPANEL; // Mode du début

        
    }
	
	/**
	 * Get all files from a directory
	 */
	public void getAllFiles(){
		File[] files = new File(System.getProperty("user.dir") + "/src/be/ac/ulb/infof307/g03/assets/Colors/").listFiles(); 
		for (File file : files) {
		    if (file.isFile()) {
		    	colorFiles.add(file.getName().replace(".png", ""));
		    }
		}
		files = new File(System.getProperty("user.dir") + "/src/be/ac/ulb/infof307/g03/assets/Textures/").listFiles(); 
		for (File file : files) {
		    if (file.isFile() && !(file.getName().contains("Full"))) {
		    	textureFiles.add(file.getName().replace(".png", ""));
		    }
		}
	}
	
	
	/**
	 * Adds the combobox to the main pane
	 */
	@SuppressWarnings("unchecked")
	public void addTypeSelection(){
		//Simple Label
		JLabel typeLabel = new JLabel("Type : ");
		
		//Pulldown menu
        JPanel comboBoxPane = new JPanel(); 
        comboBoxPane.setLayout(new GridLayout(0,1));
        String comboBoxItems[] = { COLORPANEL, TEXTURESPANEL };
        @SuppressWarnings("rawtypes")
		JComboBox cb = new JComboBox(comboBoxItems);
        cb.setEditable(false);
        cb.addItemListener(this);
        
        //Adds the label and the pulldown menu to the Panel
        comboBoxPane.add(typeLabel);
        comboBoxPane.add(cb);
        
        //Creates the constraints to be applied on this panel
        GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.BOTH;
	    c.anchor = GridBagConstraints.PAGE_START; 
	    
	    //Sets the constraints to the pane
        this.add(comboBoxPane, c);
		
	}
	
	/**
	 * @return the color List
	 */
	@SuppressWarnings("rawtypes")
	public String getSelectedColor(){
		return ("Colors/"+_colorList.getSelectedValue().toString());
	}
	
	/**
	 * @return the texture List
	 */
	@SuppressWarnings("rawtypes")
	public String getSelectedTexture(){
		return ("Textures/Full/"+_textureList.getSelectedValue().toString());
	}
	
	/**
	 * @return current mode between texture and colors
	 */
	public String getCurrentMode(){
		return CURRENTMODE;
	}

	public void addTexture(){
		final JFileChooser fc = new JFileChooser();
	    fc.showOpenDialog(this);
        try {
			Scanner reader = new Scanner(fc.getSelectedFile());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
	}
	/**
	 * Adds the 2 switching panes
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void addMaterialChoice(){
        //Creates the "cards".
        JPanel texturesPanel = new JPanel();
        _textureList = new JList(textureFiles.toArray());
        _textureList.setCellRenderer(new ColorCellRenderer());
        texturesPanel.add(_textureList);
        texturesPanel.setLayout(new GridLayout(0,1));
         
        JPanel colorsPanel = new JPanel();
        _colorList = new JList(colorFiles.toArray());
        _colorList.setCellRenderer(new ColorCellRenderer());
        colorsPanel.add(_colorList);
        colorsPanel.setLayout(new GridLayout(0,1));
         
        //Creates the panel that contains the switching panes.
        cards = new JPanel(new CardLayout());
        cards.add(colorsPanel, COLORPANEL);
        cards.add(texturesPanel, TEXTURESPANEL);

        GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
	    c.anchor = GridBagConstraints.PAGE_END; //bottom of space;
	    c.weightx = 1;
	    c.weighty = 1;
		c.gridx = 0;
		c.gridy = 1;
		
		_textureList.addMouseListener(_controller);
		_colorList.addMouseListener(_controller);
        this.add(cards, c);

		//c.ipady = 200;
		
	}
	
	@Override
	public void itemStateChanged(ItemEvent evt) {
        CardLayout cl = (CardLayout)(cards.getLayout());
        cl.show(cards, (String)evt.getItem());
        CURRENTMODE=(String) evt.getItem();
	}
	
	class ColorCellRenderer extends DefaultListCellRenderer {

	    private static final long serialVersionUID = -7799441088157759804L;
	    private JLabel label;
	    private Color textSelectionColor = Color.BLACK;
	    private Color backgroundSelectionColor = Color.LIGHT_GRAY;
	    private Color textNonSelectionColor = Color.BLACK;
	    private Color backgroundNonSelectionColor = Color.WHITE;

	    ColorCellRenderer() {
	        label = new JLabel();
	        label.setOpaque(true);
	    }

	    @Override
	    public Component getListCellRendererComponent(
	            JList list,
	            Object value,
	            int index,
	            boolean selected,
	            boolean expanded) {


	    	String classPath = getClass().getResource("TextureView.class").toString();
	    	String prefix = "";
	    	Icon imageIcon;
	    	if(classPath.subSequence(0, 3).equals("rsr")){
	    		prefix = "/";
	    		if (list.equals(_textureList)){
	    			imageIcon = new ImageIcon(getClass().getResource(prefix + value.toString()));
	    		}
	    		else{
	    			imageIcon = new ImageIcon(getClass().getResource(prefix + value.toString()));

	    		}
	    	} else {
	    		prefix = System.getProperty("user.dir") + "/src/be/ac/ulb/infof307/g03/assets/";
	    		if (list.equals(_textureList)){
	    			prefix=prefix.concat("Textures/");
	    			imageIcon = new ImageIcon(prefix + value.toString()+".png" );
	    		}
	    		else{
	    			prefix=prefix.concat("Colors/");
	    			imageIcon = new ImageIcon(prefix + value.toString()+".png");
	    		}
	    		
	    	}
	        label.setIcon(imageIcon);
	        label.setText(value.toString());
	        //label.setToolTipText();

	        if (selected) {
	            label.setBackground(backgroundSelectionColor);
	            label.setForeground(textSelectionColor);
	        } else {
	            label.setBackground(backgroundNonSelectionColor);
	            label.setForeground(textNonSelectionColor);
	        }

	        return label;
	    }
	}
	
}
