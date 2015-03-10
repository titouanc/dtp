package be.ac.ulb.infof307.g03.GUI;

import be.ac.ulb.infof307.g03.utils.Log;
import be.ac.ulb.infof307.g03.models.Config;
import be.ac.ulb.infof307.g03.models.Project;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Observable;
import java.util.Observer;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * @author wmoulart
 * @brief Controller of the JPanel that will open when user wants to change the texture of a group.
 */
public class TextureController implements ActionListener, MouseListener, ItemListener, Observer {
	// Attributes
	private TextureView view;
	private Project project;
	
	// Static value
	private final static int IMG_WIDTH = 20;
	private final static int IMG_HEIGHT = 20;
	
	//private final static String CHANGETEXTURE	= "Change Texture";
	private String classPath= getClass().getResource("TextureController.class").toString();

	
	/**
	 * @param aProject
	 */
	public TextureController(Project aProject){	
		this.project = aProject; 
		this.project.addObserver(this);
	}
	
	/**
	 * Run the View
	 */
	public void run(){
		initView();
	}
	
	/**
	 */
	public void initView(){
		this.view = new TextureView(this);
	}
	
	/**
	 * @return The controller view 
	 */
	public TextureView getView(){
		return this.view;
	}

	@Override
	public void update(Observable obs, Object arg) {
		if (obs instanceof Project){
			Config conf = (Config) arg ;
			if (conf.getName().equals("texture.mode") ){
				//TODO updateTextureMode(conf.getValue());
			}
		}
		
	}

	/**
	 * Add textures from assets/Colors and assets/Textures
	 * @param fileToImport
	 * @throws IOException
	 */
	private void addTexture(File fileToImport) throws IOException{
		try{
			File destinationMini = new File(System.getProperty("user.dir") + "/src/be/ac/ulb/infof307/g03/assets/Textures/"+fileToImport.getName());
			String destinationBig=fileToImport.getName().replace(".png", "Full.png");
			File destinationFull = new File(System.getProperty("user.dir") + "/src/be/ac/ulb/infof307/g03/assets/Textures/Full/"+destinationBig);
			copyImage(fileToImport, destinationFull); // On récupère l'image avec sa taille originale
			
			String filename = fileToImport.getName();
			if (!(filename).equals(this.view.getAddFile()) && (filename.endsWith(".png"))){
				if(fileToImport.renameTo(destinationMini)){
					reScale(destinationMini); // Set image to 20x20 format
					this.view.updatePanel(filename);
				} else{
					Log.debug("The new texture has not been imported. Error.");
				}
			} else{
				JOptionPane.showMessageDialog(view, "Only png allowed for the moment");
				Log.debug("Only png allowed");
			}
	    }
	    catch (NullPointerException ex){
	    	Log.exception(ex);	
	    } 
	}
	
	/**
	 * Add Textures from a Jar File
	 * @param fileToImport
	 * @throws IOException
	 */
	private void addTextureJar(File fileToImport) throws IOException {
		File destinationMini = new File(fileToImport.getAbsolutePath().replace(".png", "") + "Mini.png");
		File destinationFull = new File(fileToImport.getAbsolutePath().replace(".png", "") + "Full.png");
		copyImage(fileToImport, destinationFull); // On récupère l'image avec sa taille originale
		String filename = fileToImport.getName();
		if (!(filename).equals(this.view.getAddFile()) && (filename.endsWith(".png"))){
			if(fileToImport.renameTo(destinationMini)){
				reScale(destinationMini); // Set image to 20x20 format
				this.view.updatePanel(destinationFull.getAbsolutePath());
				this.writeToFile(destinationFull.getAbsolutePath()+"\n");			
			} else{
				Log.debug("The new texture has not been imported. Error.");
			}
		} else{
			Log.debug("Only .png allowed");
		}
	}
	
	/**
	 * Add a new texture 
	 * @throws IOException 
	 */
	public void addNewTexture() throws IOException{	
		final JFileChooser fc = new JFileChooser();
		FileNameExtensionFilter filterPng = new FileNameExtensionFilter("Portable Network Graphics (PNG)", "png");
	    fc.setFileFilter(filterPng);
		int returnVal = fc.showOpenDialog(this.view);
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			if(!(classPath.subSequence(0, 3).equals("rsr"))){	
			    try{
			    	File fileToImport = fc.getSelectedFile();
					this.addTexture(fileToImport);
			    }
			    catch (NullPointerException ex){
			    	Log.exception(ex);	
			    }   
			} else{
				try{		
					File fileToImport = fc.getSelectedFile();
					this.addTextureJar(fileToImport);
				}
				catch(NullPointerException ex){
			    	Log.exception(ex);				
				}
			}
	    }
	}
	
	/**
	 * Write the path to a texture in a file
	 * @param addedFilePath
	 */
	private void writeToFile(String addedFilePath){
		try{
    		File file =new File(view.getAddedFilePath());
    		if(!file.exists()){
    			file.createNewFile();
    		}
    		FileWriter fileWritter = new FileWriter(file.getName(),true);
    	        BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
    	        bufferWritter.write(addedFilePath);
    	        bufferWritter.close();
    	}catch(IOException e){
    		Log.exception(e);
    	}
	}
	
	/**
	 * Delete a Line (which here equals to the path to a texture added from a user)
	 * @param lineToRemove
	 */
	private void deleteLineInFile(String lineToRemove) { 
	    try { 
	      File inFile = new File(view.getAddedFilePath());  // File ending by Full    	      
	      File tempFile = new File(inFile.getAbsolutePath() + ".tmp");   
	      BufferedReader buffer = new BufferedReader(new FileReader(view.getAddedFilePath()));
	      PrintWriter pw = new PrintWriter(new FileWriter(tempFile));     
	      String line = "";
	      while ((line = buffer.readLine()) != null) {       
	    	  if (!line.trim().equals(lineToRemove)) { 
		          pw.println(line);
		          pw.flush();
		      }
	      }
	      pw.close();
	      buffer.close();
	      String destinationFull=lineToRemove;
	      String destinationMini=lineToRemove.replace("Full", "Mini");
	      File fileFull=new File(destinationFull);
	      File fileMini=new File(destinationMini);
	      fileFull.delete();
	      fileMini.delete();
	      inFile.delete();  // On supprime le Full
	      fileMini.delete(); // Et le Mini
	      
	      tempFile.renameTo(inFile);	      
	    }
	    catch (FileNotFoundException ex) {
	    	Log.exception(ex);
	    }
	    catch (IOException ex) {
	    	Log.exception(ex);
	    }
	  }
	
	/**
	 * Copy an image
	 * @param toBeCopied
	 * @param destination
	 * @throws IOException
	 */
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
	
	/**
	 * Re-scale an image
	 * @param file
	 * @throws IOException
	 */
	private static void reScale(File file) throws IOException{
		BufferedImage originalImage = ImageIO.read(file);
		int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
		BufferedImage resizeImagePng = rescaleImage(originalImage, type);
		ImageIO.write(resizeImagePng,"png",file);
	}
	
	/**
	 * Re-scale
	 * @param originalImage
	 * @param type
	 * @return
	 */
   private static BufferedImage rescaleImage(BufferedImage originalImage, int type){
		BufferedImage resizedImage = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, type);
		Graphics2D image = resizedImage.createGraphics();
		image.drawImage(originalImage, 0, 0, IMG_WIDTH, IMG_HEIGHT, null);
		image.dispose();
		return resizedImage;
   }
   
   /**
    * Delete a File and call the right function
    * @return file to be deleted
    */
   public String deleteFile(){
	   String fileToDelete = this.view.getSelectedTextureName();

	   if(!(classPath.subSequence(0, 3).equals("rsr"))){
		   File fullDimension=new File(System.getProperty("user.dir") + "/src/be/ac/ulb/infof307/g03/assets/"+fileToDelete+".png");
		   fullDimension.delete();
		   fileToDelete=fileToDelete.replace("Textures/Full/", "Textures/");
		   fileToDelete=fileToDelete.replace("Full", "");
		   File miniDimension=new File(System.getProperty("user.dir") + "/src/be/ac/ulb/infof307/g03/assets/"+fileToDelete+".png");
		   miniDimension.delete();
		   fileToDelete =fileToDelete.replace("Textures/", "");
		   return fileToDelete;
	   }
	   else{
		   deleteLineInFile(fileToDelete+".png");
		   return fileToDelete;
	   }
   }
	
	@Override
	public void mouseClicked(MouseEvent e) {
		// Nothing need to be done here
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// Nothing need to be done here
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// Nothing need to be done here
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// Nothing need to be done here
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// Select the texture
		int proachestIndex = this.view.getDisplayedList().locationToIndex(e.getPoint());
		Point proachest = this.view.getDisplayedList().indexToLocation(proachestIndex);
		if (proachest!=null) {
			if (Math.abs(e.getY()-proachest.getY())<20) {
				if (! this.view.getDisplayedList().isSelectedIndex(proachestIndex)) {
					this.view.getDisplayedList().setSelectedIndex(proachestIndex);
				}
			} else {
				this.view.getDisplayedList().clearSelection();
			}
		}
		
		if (SwingUtilities.isLeftMouseButton(e)) {	
			if (!this.view.getSelectedTextureName().equals(this.view.getAddFile())) {
				this.project.config("texture.selected",this.view.getSelectedTextureName());
			} else {
				try {
					this.addNewTexture();
				} catch (IOException e1) {
					Log.exception(e1);
				}
			}
		} else if (SwingUtilities.isRightMouseButton(e)){
			if (this.view.getCurrentMode().equals(TextureView.TEXTUREMODE)){
				JPopupMenu PopupMenu = this.view.createPopupMenu();
				PopupMenu.show(e.getComponent(), e.getX(), e.getY());
			}	
		}			
	}
		

	@Override
	public void actionPerformed(ActionEvent event) {		
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		this.view.updateDisplayedList();
	}

	public void onDelete() {
		String toDelete = deleteFile();
		this.view.deleteTexture(toDelete);
	}
	
}
