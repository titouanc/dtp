package be.ac.ulb.infof307.g03.GUI;

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import java.awt.BorderLayout;
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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

	
/**
 * @author Walter, brochape
 * @brief view of the panel that ill open when user wants to change texture
 */
public class TextureView extends JPanel {
	
	class MyCellRenderer extends DefaultListCellRenderer {
	    private static final long serialVersionUID = -7799441088157759804L;
	   
	    @Override
	    public Component getListCellRendererComponent(
	 	       JList list,           // the list
	 	       Object value,            // value to display
	 	       int index,               // cell index
	 	       boolean isSelected,      // is the cell selected
	 	       boolean cellHasFocus)    // does the cell have focus
	 	     {
	 	         String s = value.toString();
	 	         Icon imageIcon = null;
	 	         setText(s);
	 	         if (isSelected) {
	 	             setBackground(list.getSelectionBackground());
	 	             setForeground(list.getSelectionForeground());
	 	         } else {
	 	             setBackground(list.getBackground());
	 	             setForeground(list.getForeground());
	 	         }
	 	         
	 	         if(classPath.subSequence(0, 3).equals("rsr")) {
	 	        	 if (getCurrentMode().equals(TEXTUREMODE)) {
	 	        		 if (!(value.toString()==ADDTEXTURE)) {
	 	        			 if(value.toString().contains(File.separator)) {
	 	        				imageIcon = new ImageIcon(value.toString().replace("Full", "Mini")+".png");
	 	        			 } else {
	 	        				 imageIcon = new ImageIcon(getClass().getResource("/"+value.toString()+".png"));
	 	        			 }
	 	        		 } else {
	 	        			 imageIcon = new ImageIcon(getClass().getResource("/"+"addFile.png"));
	 	        		 }
	 	        	 } else {
	 	        		 imageIcon = new ImageIcon(getClass().getResource("/"+value.toString()+"Color.png"));
	 	        	 }
	 	         } else {
	 	        	 String prefix = System.getProperty("user.dir") + "/src/be/ac/ulb/infof307/g03/assets/";
	 	        	 if (getCurrentMode().equals(TEXTUREMODE))   			
	 	        		 if (!(value.toString()==ADDTEXTURE)) 
	 	        			 imageIcon = new ImageIcon(prefix+"/Textures/"+value.toString()+".png" );
	 	        		 else
	 	        			 imageIcon = new ImageIcon(prefix+"Tools/addFile.png" );
	 	        	 else
	 	        		 imageIcon = new ImageIcon(prefix+"Colors/"+value.toString()+"Color.png");
	 	         }
	 	         this.setIcon(imageIcon);
	 	         
	 	         setEnabled(list.isEnabled());
	 	         setFont(list.getFont());
	 	         setOpaque(true);
	 	         return this;
	 	     }
	   /*
	        if (value.toString().contains("/")){
		        // If the filename contains / , it means it's a path so we want just the end of it
	        	String name="";
	        	int start = value.toString().lastIndexOf('/') + 1;
	        	name=value.toString().substring(start);
	        	name=name.toString().replace("Full","");
	        	label.setText(name);
	        } else{
	        	label.setText(value.toString());
	        }
	        return label;
	    }*/
	}
	
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
				controller.onDelete();
			}
		}
	}
	
	private static final long serialVersionUID = 1L;
	private TextureController controller;
	
	private Vector<String> colorFiles = new Vector <String>();
	private Vector<String> textureFiles = new Vector <String>();
	
	// Different String
	public final static String COLORMODE = "Colors";
	public final static String TEXTUREMODE = "Textures";
	private final static String ADDTEXTURE= "Add a new Texture...";
	private String classPath= getClass().getResource("TextureView.class").toString();
	private String addedFilePath = "textureAdded" ;
    
    // Action alias
    static private final String DELETE = "Delete";

    private JList displayedList = null;	
    private JComboBox comboBox = null;

	/**
	 * @param newControler
	 */
	public TextureView(TextureController controler) {    	
    	this.controller = controler;
		
    	//Fixes the width of the pane
    	this.setPreferredSize(new Dimension(this.getHeight(),100));
    	
		this.addAllFiles();
		this.addTypeSelectionPanel();
		this.addMaterialChoice();
		this.updateDisplayedList();
    }

	public JList getDisplayedList() {
		return this.displayedList;
	}
	
	private void addTypeSelectionPanel(){
		JPanel typeSelectionPanel = new JPanel();
		typeSelectionPanel.setMinimumSize(new Dimension(this.getHeight(),100));
		JLabel typeLabel = new JLabel("Type : ");
		
		this.comboBox = new JComboBox();
		this.comboBox.addItem(COLORMODE);
		this.comboBox.addItem(TEXTUREMODE);
		this.comboBox.setEditable(false);
		this.comboBox.addItemListener(controller);
        
        typeSelectionPanel.add(typeLabel,BorderLayout.WEST);
        typeSelectionPanel.add(this.comboBox,BorderLayout.EAST);	
        this.add(typeSelectionPanel,BorderLayout.PAGE_START);
	}

	public void addMaterialChoice(){
        this.displayedList = new JList();
        this.displayedList.setCellRenderer(new MyCellRenderer());
        this.displayedList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        this.displayedList.setLayoutOrientation(JList.VERTICAL);
        this.displayedList.setVisibleRowCount(-1);
        this.displayedList.addMouseListener(controller);
      
        Dimension dimension = new Dimension(190,200);
        JScrollPane scrollPane = new JScrollPane(this.displayedList);
        scrollPane.setPreferredSize(dimension);
        scrollPane.setMinimumSize(dimension);

		this.add(scrollPane,BorderLayout.PAGE_END);
	}
	
	public void updateDisplayedList() {
		this.displayedList.removeAll();
		if (this.getCurrentMode().equals(COLORMODE)) {
			this.displayedList.setListData(this.colorFiles);
		} else if (this.getCurrentMode().equals(TEXTUREMODE)) {
			this.displayedList.setListData(this.textureFiles);
		}
		this.displayedList.updateUI();
	}
	
    /**
     * Get the name of the jar currently running so we don't need to hardcode it
     * @return the name of the JAR currently running
     */
    private String getRunningJarName(){
    	String name = this.getClass().getResource(this.getClass().getSimpleName() + ".class").getFile();
    	name = ClassLoader.getSystemClassLoader().getResource(name).getFile();
    	name=name.substring(0, name.lastIndexOf('!'));
	    int start = name.lastIndexOf('/') + 1;
	    int end = name.lastIndexOf('.');
	    name = name.substring(start, end)+".jar";
	    return name ;
    }
    
	/**
	 * Read the Jar and get the fileName
	 * @param obj
	 * @return
	 */
    private static String process(Object obj) {
        JarEntry entry = (JarEntry)obj;
        String name = entry.getName();
        return name;
      }
    
    /**
     * Parse Jar File and add textures files to the right list
     */
    private void addFilesJar(){
    	JarFile jarFile;
		String filename;
		String file;
		try { // first we will check all the files that the jar contents
			String path = getRunningJarName() ;	    
			jarFile = new JarFile(path);
		    Enumeration<JarEntry> item = jarFile.entries();
		    while (item.hasMoreElements()) {
		    	file=process(item.nextElement());
		    	if (file.contains("Color") && !(file.contains(File.separator))){
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
		 // Then we will read the file that contents the added texture path of the user
		    File fileAdd =new File(addedFilePath);
    		if(fileAdd.exists()){
    			readFile(new File(addedFilePath));
    		}			    		   		    
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
	/**
	 * Get all files from a directory
	 */
	private void addAllFiles(){
		if(classPath.subSequence(0, 3).equals("rsr")){	
			this.addFilesJar();	    
    	} else {
    		this.addFiles(); 		
    	}    	
	}
	
	/**
	 * Parse files from /Textures and /Colors in assets
	 */
	private void addFiles(){
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
	
	/**
	 * Read a given File and addtextures to textureList
	 * @param file
	 * @throws IOException
	 */
	private void readFile(File file) throws IOException {
		BufferedReader buffer = null;
		try {	 
			String filename;
			buffer = new BufferedReader(new FileReader(addedFilePath));
			while ((filename = buffer.readLine()) != null) {
				filename=filename.replace(".png", "");
				this.textureFiles.add(filename);
			}
			buffer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @return add texture
	 */
	public String getAddFile(){
		return ADDTEXTURE;
	}
	
	/**
	 * @return current mode between texture and colors
	 */
	public String getCurrentMode(){
		return this.comboBox.getSelectedItem().toString();
	}
	
	/**
	 * @return filename
	 */
	public String getAddedFilePath(){
		return addedFilePath;
	}
	
	/**
	 * @param filename
	 * Update the panel 
	 */
	public void updatePanel(String filename){
		filename=filename.replace(".png","");	
		this.textureFiles.add(this.textureFiles.size()-1,filename);
		this.updateUI();
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

	public String getSelectedTextureName() {
		if (this.displayedList.isSelectionEmpty()) 
			return null;
		
		if (this.getCurrentMode().equals(COLORMODE)) {
			if(classPath.subSequence(0, 3).equals("rsr")){		
				return (this.displayedList.getSelectedValue().toString());
			} else {
				return ("Colors/" + this.displayedList.getSelectedValue().toString()+"Color");
			}
		} else {
			if(classPath.subSequence(0, 3).equals("rsr")){
				if (this.displayedList.getSelectedValue().toString().equals(ADDTEXTURE)){
					return ADDTEXTURE;
				} else {
					String res = this.displayedList.getSelectedValue().toString();
					if(!res.contains(File.separator)){
						res += "Full";
					}
					return (res);
				}
			} else {
				if (this.displayedList.getSelectedValue().toString().equals(ADDTEXTURE)){
					return ADDTEXTURE;
				} else {
					return ("Textures/Full/" + this.displayedList.getSelectedValue().toString()+"Full");
				}
			}
		}
	}

	public void deleteTexture(String toDelete) {
		textureFiles.remove(toDelete);
		updateUI();				
	}
		
}


