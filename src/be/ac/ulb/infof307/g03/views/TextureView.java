package be.ac.ulb.infof307.g03.views;

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

import be.ac.ulb.infof307.g03.controllers.TextureController;
import be.ac.ulb.infof307.g03.models.Project;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;


/**
 * @author Walter, brochape
 * @brief view of the panel that ill open when user wants to change texture
 */
public class TextureView extends JPanel implements ItemListener {

	private static final long serialVersionUID = 1L;
	private TextureController _controller;
	private Project _project;
	
	private GridBagLayout _paneLayout;
	private ArrayList<String> _colorFiles=   new ArrayList <String>();
	private ArrayList<String> _textureFiles= new ArrayList <String>();
	
	private final static String _COLORPANEL = "Colors";
	private final static String _TEXTURESPANEL = "Textures";
	private final static String _ADDTEXTURE= "Add a new Texture...";
    private static String _CURRENTMODE ="" ;
    

    static private JList _textureList = new JList();

	static private JList _colorList   = new JList();
	static private JPanel _texturesPanel = new JPanel();
	
    private JPanel _cards; //a panel that uses CardLayout

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
    	_texturesPanel.removeAll();
		this.addAllFiles();
		_textureFiles.add(_ADDTEXTURE);
		this.addTypeSelection();
		this.addMaterialChoice();
		_CURRENTMODE=_COLORPANEL; // Mode du début

        
    }
	
	/**
	 * Get all files from a directory
	 */
	private void addAllFiles(){
		File[] files = new File(System.getProperty("user.dir") + "/src/be/ac/ulb/infof307/g03/assets/Colors/").listFiles(); 
		for (File file : files) {
		    if (file.isFile()) {
		    	_colorFiles.add(file.getName().replace(".png", ""));
		    }
		}
		files = new File(System.getProperty("user.dir") + "/src/be/ac/ulb/infof307/g03/assets/Textures/").listFiles(); 
		for (File file : files) {
		    if (file.isFile()) {
		    	if(!(file.getName().contains("Full"))){
		    		_textureFiles.add(file.getName().replace(".png", ""));	
		    	}
		    }
		}
	}
	
	
	/**
	 * Adds the combobox to the main pane
	 */
	private void addTypeSelection(){
		//Simple Label
		JLabel typeLabel = new JLabel("Type : ");
		
		//Pulldown menu
        JPanel comboBoxPane = new JPanel(); 
        comboBoxPane.setLayout(new GridLayout(0,1));
        String comboBoxItems[] = { _COLORPANEL, _TEXTURESPANEL };

		JComboBox comboBox = new JComboBox(comboBoxItems);
        comboBox.setEditable(false);
        comboBox.addItemListener(this);
        
        //Adds the label and the pulldown menu to the Panel
        comboBoxPane.add(typeLabel);
        comboBoxPane.add(comboBox);
        
        //Creates the constraints to be applied on this panel
        GridBagConstraints gridBagCons = new GridBagConstraints();

		gridBagCons.fill = GridBagConstraints.BOTH;
	    gridBagCons.anchor = GridBagConstraints.PAGE_START; 
	    
	    //Sets the constraints to the pane
        this.add(comboBoxPane, gridBagCons);
		
	}
	
	/**
	 * @return add tetxture
	 */
	public String getAddFile(){
		return _ADDTEXTURE;
	}
	
	/**
	 * @return the color List
	 */
	public String getSelectedColorAsString(){
		return ("Colors/" + _colorList.getSelectedValue().toString());
	}
	
	/**
	 * @return the texture List
	 */
	public String getSelectedTexture(){
		if (_textureList.getSelectedValue().toString().equals(_ADDTEXTURE)){
			return _ADDTEXTURE;
		}
		else{
			return ("Textures/Full/" + _textureList.getSelectedValue().toString());
		}
	}
	
	/**
	 * @return current mode between texture and colors
	 */
	public String getCurrentMode(){
		return _CURRENTMODE;
	}
	
	public void updatePanel(String filename){
		filename=filename.replace(".png","");
		_textureFiles.add(_textureFiles.size()-1,filename);
		_textureList = new JList(_textureFiles.toArray());
        _textureList.setCellRenderer(new ColorCellRenderer());	
        _texturesPanel.removeAll();
        _texturesPanel.add(_textureList);
		_texturesPanel.updateUI();
		_textureList.addMouseListener(_controller);
	}
 
	/**
	 * Adds the 2 switching panes
	 */
	public void addMaterialChoice(){
        //Creates the "cards".
        _textureList = new JList(_textureFiles.toArray());
        _textureList.setCellRenderer(new ColorCellRenderer());
        _texturesPanel.add(_textureList);
        _texturesPanel.setLayout(new GridLayout(0,1));
         
        JPanel colorsPanel = new JPanel();
        _colorList = new JList(_colorFiles.toArray());
        _colorList.setCellRenderer(new ColorCellRenderer());
        colorsPanel.add(_colorList);
        colorsPanel.setLayout(new GridLayout(0,1));
         
        //Creates the panel that contains the switching panes.
        _cards = new JPanel(new CardLayout());
        _cards.add(colorsPanel, _COLORPANEL);
        _cards.add(_texturesPanel, _TEXTURESPANEL);

        GridBagConstraints gridBagCons = new GridBagConstraints();
		gridBagCons.fill = GridBagConstraints.BOTH;
	    gridBagCons.anchor = GridBagConstraints.PAGE_END; //bottom of space;
	    gridBagCons.weightx = 1;
	    gridBagCons.weighty = 1;
		gridBagCons.gridx = 0;
		gridBagCons.gridy = 1;
		
		_textureList.addMouseListener(_controller);
		_colorList.addMouseListener(_controller);
        this.add(_cards, gridBagCons);

		//c.ipady = 200;
		
	}
	
	@Override
	public void itemStateChanged(ItemEvent evt) {
        CardLayout cl = (CardLayout) _cards.getLayout();
        cl.show(_cards, (String) evt.getItem());
        _CURRENTMODE = (String) evt.getItem();
	}
	
	class ColorCellRenderer extends DefaultListCellRenderer {

	    private static final long serialVersionUID = -7799441088157759804L;
	    private JLabel _label;
	    private Color _textSelectionColor = Color.BLACK;
	    private Color _backgroundSelectionColor = Color.LIGHT_GRAY;
	    private Color _textNonSelectionColor = Color.BLACK;
	    private Color _backgroundNonSelectionColor = Color.WHITE;

	    ColorCellRenderer() {
	        _label = new JLabel();
	        _label.setOpaque(true);
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
	    			prefix = prefix.concat("Textures/");
	    			imageIcon = new ImageIcon(prefix + value.toString()+".png" );
	    		}
	    		else{
	    			prefix = prefix.concat("Colors/");
	    			imageIcon = new ImageIcon(prefix + value.toString()+".png");
	    		}
	    		
	    	}
	        _label.setIcon(imageIcon);
	        _label.setText(value.toString());
	        //label.setToolTipText();

	        if (selected) {
	            _label.setBackground(_backgroundSelectionColor);
	            _label.setForeground(_textSelectionColor);
	        } else {
	            _label.setBackground(_backgroundNonSelectionColor);
	            _label.setForeground(_textNonSelectionColor);
	        }

	        return _label;
	    }
	}
	
}
