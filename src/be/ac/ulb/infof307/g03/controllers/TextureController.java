package be.ac.ulb.infof307.g03.controllers;

import be.ac.ulb.infof307.g03.utils.Log;
import be.ac.ulb.infof307.g03.views.TextureView;
import be.ac.ulb.infof307.g03.models.Config;
import be.ac.ulb.infof307.g03.models.Project;

import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.JFileChooser;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

/**
 * @author wmoulart
 * @brief Controller of the JPanel that will open when user wants to change the texture of a group.
 */
public class TextureController implements ActionListener,MouseListener, Observer {
	// Attributes
	private TextureView view;
	private Project project;
	
	// Static value
	private final static int _IMG_WIDTH = 20;
	private final static int _IMG_HEIGHT = 20;
	
	//private final static String _CHANGETEXTURE	= "Change Texture";
	private static String fileToDelete= new String();
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
		initView(this.project);
	}
	
	/**
	 * @param aProject
	 */
	public void initView(Project aProject){
		this.view = new TextureView(this,aProject);
	}
	
	/**
	 * @return The controller view 
	 */
	public TextureView getView(){
		return this.view;
	}

	@Override
	public void update(Observable obs, Object arg) {
		Log.debug("UPDATE");
		if (obs instanceof Project){
			Config conf = (Config) arg ;
			if (conf.getName().equals("texture.mode") ){
				//TODO updateTextureMode(conf.getValue());
			}
		}
		
	}

	/*
	@SuppressWarnings("deprecation")
	private void updateTextureMode(String value) {
		if (value.equals("shown")){
			_view.show();
			Log.debug("SHOW");
		}
		else{
			Log.debug("HIDE");

			_view.hide();
		}
		else if (value.equals("hidden")){
			_view.hide();
		}	
	}
*/
	/**
	 * Add a new texture 
	 * @throws IOException 
	 */
	public void addNewTexture() throws IOException{
		final JFileChooser fc = new JFileChooser();
		int returnVal = fc.showOpenDialog(this.view);
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			if(!(classPath.subSequence(0, 3).equals("rsr"))){	
			    try{
					File fileToImport = fc.getSelectedFile();
					File destinationMini = new File(System.getProperty("user.dir") + "/src/be/ac/ulb/infof307/g03/assets/Textures/"+fileToImport.getName());
					File destinationFull = new File(System.getProperty("user.dir") + "/src/be/ac/ulb/infof307/g03/assets/Textures/Full/"+fileToImport.getName());
					copyImage(fileToImport, destinationFull); // On récupère l'image avec sa taille originale
					
					String filename = fileToImport.getName();
					if (!(filename).equals(this.view.getAddFile()) && (filename.endsWith(".png"))){
						if(fileToImport.renameTo(destinationMini)){
							reScale(destinationMini); // Set image to 20x20 format
							this.view.updatePanel(filename);
						}
						else{
							Log.debug("The new texture has not been imported. Error.");
						}
					}
					else{
						Log.debug("Only PNG allowed");
					}
			    }
			    catch (NullPointerException ex){
			    	Log.exception(ex);	
			    }   
			}
			else{
				try{
					File fileToImport = fc.getSelectedFile();
					File destinationMini = new File(fileToImport.getAbsolutePath().replace(".png", "") + "Mini.png");
					File destinationFull = new File(fileToImport.getAbsolutePath().replace(".png", "") + "Full.png");
					copyImage(fileToImport, destinationFull); // On récupère l'image avec sa taille originale
					String filename = fileToImport.getName();
					if (!(filename).equals(this.view.getAddFile()) && (filename.endsWith(".png"))){
						if(fileToImport.renameTo(destinationMini)){
							reScale(destinationMini); // Set image to 20x20 format
							this.view.updatePanel(destinationFull.getAbsolutePath());
						}
						else{
							Log.debug("The new texture has not been imported. Error.");
						}
					}
					else{
						Log.debug("Only PNG allowed");
					}
				}
				catch(NullPointerException ex){
			    	Log.exception(ex);				
				}
			}
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
		int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
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
 * @return the name of the file to be deleted
 */
   public String deleteFile(){
	   File fullDimension=new File(System.getProperty("user.dir") + "/src/be/ac/ulb/infof307/g03/assets/"+fileToDelete+".png");
	   fullDimension.delete();
	   fileToDelete=fileToDelete.replace("Textures/Full/", "Textures/");
	   File miniDimension=new File(System.getProperty("user.dir") + "/src/be/ac/ulb/infof307/g03/assets/"+fileToDelete+".png");
	   miniDimension.delete();
	   fileToDelete =fileToDelete.replace("Textures/", "");
	   return fileToDelete;
   }
	
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e)) {			
			if (this.view.getCurrentMode().equals("Colors")){
				this.project.config("texture.selected",this.view.getSelectedColorAsString());
			}
			else{
				if(!(this.view.getSelectedTexture().equals(this.view.getAddFile()))){
					this.project.config("texture.selected",this.view.getSelectedTexture());
				}
				else{
					try {
						this.addNewTexture();
					} catch (IOException e1) {
						Log.exception(e1);
						// TODO prévenir l'utilisateur pourquoi ça a pas marché
					}
				}
			}
		}
		else if (SwingUtilities.isRightMouseButton(e)){
			if((this.view.getCurrentMode().equals("Textures"))){
				if(!(this.view.getSelectedTexture().equals(this.view.getAddFile()))){
					JPopupMenu PopupMenu = this.view.createPopupMenu();
					fileToDelete=this.view.getSelectedTexture(); // On a le nom comme Textures/Full/xxx
					if (PopupMenu != null) {
						PopupMenu.show(e.getComponent(), e.getX(), e.getY());
					}					
				}
				
			}		
		}			
	}
		

	@Override
	public void actionPerformed(ActionEvent event) {
		// TODO Auto-generated method stub
		
	}
	
	
	

}
