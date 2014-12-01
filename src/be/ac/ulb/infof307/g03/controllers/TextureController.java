package be.ac.ulb.infof307.g03.controllers;

import be.ac.ulb.infof307.g03.utils.Log;
import be.ac.ulb.infof307.g03.views.TextureView;
import be.ac.ulb.infof307.g03.models.Config;
import be.ac.ulb.infof307.g03.models.Project;

import java.awt.Graphics2D;
import java.awt.Point;
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
	private TextureView _view;
	private Project _project;
	
	// Static value
	private final static int _IMG_WIDTH = 20;
	private final static int _IMG_HEIGHT = 20;
	
	static final public String _CHANGETEXTURE	= "Change Texture";
	
	
	
	/**
	 * @param aProject
	 */
	public TextureController(Project aProject){	
		_project = aProject; 
		_project.addObserver(this);
	}
	
	/**
	 * Run the View
	 */
	public void run(){
		initView(_project);
	}
	
	/**
	 * @param aProject
	 */
	public void initView(Project aProject){
		_view = new TextureView(this,aProject);
	}
	
	/**
	 * @return The controller view 
	 */
	public TextureView getView(){
		return _view;
	}
/*
	public void actionPerformed(ActionEvent action) {
		String cmd = action.getActionCommand();
		if (cmd.equals(_CHANGETEXTURE)) {
			Log.debug("fdp");
		}
	}
*/
	@Override
	public void update(Observable obs, Object arg) {
		Log.debug("UPDATE");
		if (obs instanceof Project){
			Config conf = (Config) arg ;
			if (conf.getName().equals("texture.mode") ){
				//updateTextureMode(conf.getValue());
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
	    fc.showOpenDialog(_view);
	    try{
			File fileToMove = fc.getSelectedFile();
			File destinationMini = new File(System.getProperty("user.dir") + "/src/be/ac/ulb/infof307/g03/assets/Textures/"+fileToMove.getName());
			File destinationFull = new File(System.getProperty("user.dir") + "/src/be/ac/ulb/infof307/g03/assets/Textures/Full/"+fileToMove.getName());

			copyImage(fileToMove,destinationFull); // On récupère l'image avec sa taille originale
			
			String filename=fileToMove.getName();
			if (!(filename).equals(_view.getAddFile()) && (filename.contains(".png"))){
				if(fileToMove.renameTo(destinationMini)){
					reScale(destinationMini); // Set image to 20x20 format
					_view.updatePanel(filename);
				}
				else{
					Log.debug("The new texture has not been imported. Error.");
				}
			}
	    }
	    catch (NullPointerException e){
	    	Log.warn("NullPointerException catched.");
	    	//e.printStackTrace();	
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
			if (_view.getCurrentMode().equals("Colors")){
				_project.config("texture.selected",_view.getSelectedColorAsString());
			}
			else{
				if(!(_view.getSelectedTexture().equals(_view.getAddFile()))){
					_project.config("texture.selected",_view.getSelectedTexture());
				}
				else{
					try {
						this.addNewTexture();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						//e1.printStackTrace();
					}
				}
			}
		}
		
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	
	

}
