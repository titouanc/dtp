package be.ac.ulb.infof307.g03.views;

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import be.ac.ulb.infof307.g03.controllers.TextureController;
import be.ac.ulb.infof307.g03.models.Project;
import be.ac.ulb.infof307.g03.utils.Log;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

	
/**
 * @author Walter, brochape
 * @brief view of the panel that ill open when user wants to change texture
 */
public class TextureView extends JPanel implements ItemListener {

	private static final long serialVersionUID = 1L;
	private TextureController controller;
	private Project project;
	
	private GridBagLayout paneLayout;
	private ArrayList<String> colorFiles=   new ArrayList <String>();
	private ArrayList<String> textureFiles= new ArrayList <String>();
	
	private final static String COLORPANEL = "Colors";
	private final static String TEXTURESPANEL = "Textures";
	private final static String ADDTEXTURE= "Add a new Texture...";
    private static String CURRENTMODE ="" ;
    
    // Action alias
    static private final String DELETE = "Delete";

    static private JList textureList = new JList();

	static private JList colorList   = new JList();
	static private JPanel texturesPanel = new JPanel();
	private String classPath= getClass().getResource("TextureView.class").toString();
	
    private JPanel cards; //a panel that uses CardLayout 
    
    /**
	 * This class implements a ActionListener to be 
	 * used with a popup menu
	 */
	class PopupListener implements ActionListener {

		/**
		 * This method is called when user click on a menu
		 * @param event click on menu
		 */
		@Override
		public void actionPerformed(ActionEvent event) {
			String cmd = event.getActionCommand();
			if (cmd.equals(DELETE)) {
				String toDelete=controller.deleteFile();
				textureFiles.remove(toDelete);
				update();
				
			}
		}
	}

	/**
	 * @param newControler
	 * @param project
	 */
	public TextureView(TextureController newControler, Project project) {
		//Builds a JPane based on a CardLayout, which is a layout that mages 2+ panes using the same display space
    	super(new CardLayout());  	
    	
    	this.controller = newControler;
    	this.project = project;
        
    	//Uses the GridbagConstraints
    	this.paneLayout = new GridBagLayout();
    	this.setLayout(this.paneLayout);
		
    	//Fixes the width of the pane
    	this.setPreferredSize(new Dimension(this.getHeight(),100));
    	
    	// Get filenames
    	texturesPanel.removeAll();
		this.addAllFiles();
		this.textureFiles.add(ADDTEXTURE);
		this.addTypeSelection();
		this.addMaterialChoice();
		CURRENTMODE=COLORPANEL; // Mode du début

        
    }
	
    private static String process(Object obj) {
        JarEntry entry = (JarEntry)obj;
        String name = entry.getName();
        return name;
      }
	/**
	 * Get all files from a directory
	 */
	private void addAllFiles(){
		if(classPath.subSequence(0, 3).equals("rsr")){	
			JarFile jarFile;
			String filename;
			try {
				String file;
				jarFile = new JarFile("HomePlans.jar");
			    Enumeration ta = jarFile.entries();
			    while (ta.hasMoreElements()) {
			    	file=process(ta.nextElement());
			    	if (file.contains("Color") && !(file.contains("/"))){
			    		filename=file.replace(".png", "");
			    		filename=filename.replace("Color", "");
    		    		this.colorFiles.add(filename);    		    		
    		    	}
    		    	else if(file.contains("Full")){
    		    		filename=file.replace("Full","");
    		    		filename=filename.replace(".png", "");
    		    		this.textureFiles.add(filename);
    		    	}
			     }			  
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		    
    	} else {
    		File[] files = new File(System.getProperty("user.dir") + "/src/be/ac/ulb/infof307/g03/assets/Colors/").listFiles(); 
    		for (File file : files) {
    		    if (file.isFile()) {
    		    	String filename=file.getName().replace("Color","");
    		    	filename=filename.replace(".png", "");
    		    	this.colorFiles.add(filename);
    		    }
    		}
    		files = new File(System.getProperty("user.dir") + "/src/be/ac/ulb/infof307/g03/assets/Textures/").listFiles(); 
    		for (File file : files) {
    		    if (file.isFile()) {
    		    	if(!(file.getName().contains("Full"))){
    		    		this.textureFiles.add(file.getName().replace(".png", ""));	
    		    	}
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
        String comboBoxItems[] = { COLORPANEL, TEXTURESPANEL };

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
		return ADDTEXTURE;
	}
	
	/**
	 * @return the color List
	 */
	public String getSelectedColorAsString(){
		if(classPath.subSequence(0, 3).equals("rsr")){		
			return (colorList.getSelectedValue().toString());
		}
		else{
			return ("Colors/" + colorList.getSelectedValue().toString()+"Color");
		}
	}
	
	/**
	 * @return the texture List
	 */
	public String getSelectedTexture(){
		if(classPath.subSequence(0, 3).equals("rsr")){
			if (textureList.getSelectedValue().toString().equals(ADDTEXTURE)){
				return ADDTEXTURE;
			}
			else{
				String res = textureList.getSelectedValue().toString();
				if(!res.contains(File.separator)){
					res += "Full";
				}
				return (res);
			}
		}
		else{
			if (textureList.getSelectedValue().toString().equals(ADDTEXTURE)){
				return ADDTEXTURE;
			}
			else{
				return ("Textures/Full/" + textureList.getSelectedValue().toString());
			}
		}
	}
	
	/**
	 * @return current mode between texture and colors
	 */
	public String getCurrentMode(){
		return CURRENTMODE;
	}
	
	/**
	 * Update the JPanel : refresh it 
	 */
	private void update(){
		textureList = new JList(this.textureFiles.toArray());
        textureList.setCellRenderer(new ColorCellRenderer());	
        texturesPanel.removeAll();
        texturesPanel.add(textureList);
		texturesPanel.updateUI();
		textureList.addMouseListener(this.controller);
	}
	
	/**
	 * @param filename
	 * Update the panel 
	 */
	public void updatePanel(String filename){
		filename=filename.replace(".png","");	
		this.textureFiles.add(this.textureFiles.size()-1,filename);
		this.update();
	}
 
	/**
	 * Adds the 2 switching panes
	 */
	public void addMaterialChoice(){
        //Creates the "cards".
        textureList = new JList(this.textureFiles.toArray());
        textureList.setCellRenderer(new ColorCellRenderer());
        texturesPanel.add(textureList);
        texturesPanel.setLayout(new GridLayout(0,1));
         
        JPanel colorsPanel = new JPanel();
        colorList = new JList(this.colorFiles.toArray());
        colorList.setCellRenderer(new ColorCellRenderer());
        colorsPanel.add(colorList);
        colorsPanel.setLayout(new GridLayout(0,1));
         
        //Creates the panel that contains the switching panes.
        this.cards = new JPanel(new CardLayout());
        this.cards.add(colorsPanel, COLORPANEL);
        this.cards.add(texturesPanel, TEXTURESPANEL);

        GridBagConstraints gridBagCons = new GridBagConstraints();
		gridBagCons.fill = GridBagConstraints.BOTH;
	    gridBagCons.anchor = GridBagConstraints.PAGE_END; //bottom of space;
	    gridBagCons.weightx = 1;
	    gridBagCons.weighty = 1;
		gridBagCons.gridx = 0;
		gridBagCons.gridy = 1;
		
		textureList.addMouseListener(this.controller);
		colorList.addMouseListener(this.controller);
        this.add(this.cards, gridBagCons);

		//c.ipady = 200;
		
	}
	
	/**
	 * Set the layout if we are in texture mode or color mode
	 */
	@Override
	public void itemStateChanged(ItemEvent evt) {
        CardLayout cl = (CardLayout) this.cards.getLayout();
        cl.show(this.cards, (String) evt.getItem());
        CURRENTMODE = (String) evt.getItem();
	}
	
	/**
	 * 
	 * @param label
	 * @param action
	 * @param listener
	 * @return the menuItem
	 */
	private JMenuItem createJMenuItem(String label, String action, PopupListener listener) {
		JMenuItem menuItem = new JMenuItem(label);
		menuItem.addActionListener(listener);
		menuItem.setActionCommand(action);
		return menuItem;
	}
	
	/**
	 * Build a contextual menu for a clicked item
	 * @return the popup
	 */
	public JPopupMenu createPopupMenu(){	
		PopupListener listener = new PopupListener();
		JPopupMenu res = new JPopupMenu();
		res.add(createJMenuItem(DELETE, DELETE, listener));
		return res;
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
	    	String prefix = "/";
	    	Icon imageIcon;
	    	if(classPath.subSequence(0, 3).equals("rsr")){
	    		if (list.equals(textureList)){
	    			if (!(value.toString()==ADDTEXTURE)){
	    				if(value.toString().contains(File.separator)){
	    					imageIcon = new ImageIcon(value.toString().replace("Full", "Mini")+".png");
	    				}
	    				else{
	    					imageIcon = new ImageIcon(
	    							getClass().getResource(prefix+value.toString()+".png"));
	    				}
	    			}
	    			else{
	    				imageIcon = new ImageIcon(getClass().getResource(prefix+"addFile.png"));
	    			}
	    		}
	    		else{
	    			imageIcon = new ImageIcon(getClass().getResource(prefix+value.toString()+"Color.png"));
	    		}
	    	} else {
	    		prefix = System.getProperty("user.dir") + "/src/be/ac/ulb/infof307/g03/assets/";
	    		if (list.equals(textureList)){	    			
	    			if (!(value.toString()==ADDTEXTURE)){
	    				prefix = prefix+"Textures/";
		    			imageIcon = new ImageIcon(prefix+value.toString()+".png" );
	    			}
	    			else{
	    				prefix=prefix+"Tools/";
		    			imageIcon = new ImageIcon(prefix+"addFile.png" );
	    			}
	    		}
	    		else{
	    			prefix = prefix+"Colors/";
	    			imageIcon = new ImageIcon(prefix+value.toString()+"Color.png");
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


