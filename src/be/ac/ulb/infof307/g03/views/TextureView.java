package be.ac.ulb.infof307.g03.views;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

import be.ac.ulb.infof307.g03.controllers.TextureController;
import be.ac.ulb.infof307.g03.models.Project;
import be.ac.ulb.infof307.g03.utils.Log;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
	
	final static String _COLORPANEL = "Colors";
    final static String _TEXTURESPANEL = "Textures";
    static String _CURRENTMODE ="" ;
    

    static private JList _textureList = new JList();

	static private JList _colorList   = new JList();
	static private JPanel texturesPanel = new JPanel();
    
	private final static int _IMG_WIDTH = 20;
	private final static int _IMG_HEIGHT = 20;
	
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
		addAllFiles();
		_textureFiles.add("Add a new File...");
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
	public String getSelectedColorAsString(){
		return ("Colors/"+_colorList.getSelectedValue().toString());
	}
	
	/**
	 * @return the texture List
	 */
	public String getSelectedTexture(){
		if (_textureList.getSelectedValue().toString().equals("Add a new File...")){
			return null;
		}
		else{
			return ("Textures/Full/"+_textureList.getSelectedValue().toString());
		}
	}
	
	/**
	 * @return current mode between texture and colors
	 */
	public String getCurrentMode(){
		return _CURRENTMODE;
	}

	/**
	 * Add a new texture 
	 * @throws IOException 
	 */
	public void addNewTexture() throws IOException{
		final JFileChooser fc = new JFileChooser();
	    fc.showOpenDialog(this);
	    try{
			File fileToMove = fc.getSelectedFile();
			File destinationMini = new File(System.getProperty("user.dir") + "/src/be/ac/ulb/infof307/g03/assets/Textures/"+fileToMove.getName());
			File destinationFull = new File(System.getProperty("user.dir") + "/src/be/ac/ulb/infof307/g03/assets/Textures/Full/"+fileToMove.getName());

			copyImage(fileToMove,destinationFull); // On récupère l'image avec sa taille originale
			
			String filename=fileToMove.getName();
			if (!(filename).equals("Add a new File...") && (filename.contains(".png"))){
				if(fileToMove.renameTo(destinationMini)){
					Log.debug("Your image has been added with success.");
					fileToMove.renameTo(destinationMini);
					reScale(destinationMini); // Set image to 20x20 format
					filename=filename.replace(".png","");
					_textureFiles.add(_textureFiles.size()-1,filename);
					_textureList = new JList(_textureFiles.toArray());
			        _textureList.setCellRenderer(new ColorCellRenderer());	
			        texturesPanel.removeAll();
			        texturesPanel.add(_textureList);
					texturesPanel.updateUI();
				}
				else{
					Log.debug("The new texture has not been imported. Error.");
				}
			}
	    }
	    catch (NullPointerException e){
	    	Log.warn("NullPointerException catched.");
	    	e.printStackTrace();
	    	// L'utilisateur a clické sur closed, on ne fait rien 	
	    }
	}
	
	private static void copyImage(File toBeCopied,File destination)throws IOException {			    
		 ImageInputStream input = new FileImageInputStream(toBeCopied);
		 ImageOutputStream output = new FileImageOutputStream(destination);
		 byte[] buffer = new byte[1024];
		 int len;
		 while ((len = input.read(buffer)) > 0) {
			 output.write(buffer, 0, len);
		 }
		 input.close();
		 output.close();
	}
	
	private static void reScale(File file) throws IOException{
		BufferedImage originalImage = ImageIO.read(file);
		int type = originalImage.getType() == 0? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
		BufferedImage resizeImagePng = rescaleImage(originalImage, type);
		ImageIO.write(resizeImagePng,"png",file);
	}
	
    private static BufferedImage rescaleImage(BufferedImage originalImage, int type){
		BufferedImage resizedImage = new BufferedImage(_IMG_WIDTH, _IMG_HEIGHT, type);
		Graphics2D image = resizedImage.createGraphics();
		image.drawImage(originalImage, 0, 0, _IMG_WIDTH, _IMG_HEIGHT, null);
		image.dispose();
		return resizedImage;
    }
 
	/**
	 * Adds the 2 switching panes
	 */
	public void addMaterialChoice(){
        //Creates the "cards".
        _textureList = new JList(_textureFiles.toArray());
        _textureList.setCellRenderer(new ColorCellRenderer());
        texturesPanel.add(_textureList);
        texturesPanel.setLayout(new GridLayout(0,1));
         
        JPanel colorsPanel = new JPanel();
        _colorList = new JList(_colorFiles.toArray());
        _colorList.setCellRenderer(new ColorCellRenderer());
        colorsPanel.add(_colorList);
        colorsPanel.setLayout(new GridLayout(0,1));
         
        //Creates the panel that contains the switching panes.
        _cards = new JPanel(new CardLayout());
        _cards.add(colorsPanel, _COLORPANEL);
        _cards.add(texturesPanel, _TEXTURESPANEL);

        GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
	    c.anchor = GridBagConstraints.PAGE_END; //bottom of space;
	    c.weightx = 1;
	    c.weighty = 1;
		c.gridx = 0;
		c.gridy = 1;
		
		_textureList.addMouseListener(_controller);
		_colorList.addMouseListener(_controller);
        this.add(_cards, c);

		//c.ipady = 200;
		
	}
	
	@Override
	public void itemStateChanged(ItemEvent evt) {
        CardLayout cl = (CardLayout)(_cards.getLayout());
        cl.show(_cards, (String)evt.getItem());
        _CURRENTMODE=(String) evt.getItem();
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
