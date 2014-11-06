package be.ac.ulb.infof307.g03.GUI;

/**
 * @author fhennecker
 * 
 */
public class ToolsBarController {
	public ToolsBarView view;
	
	ToolsBarController(){
		view = new ToolsBarView(this);
	}
	
	/**
     * The private method is called when the button undo
     * is clicked. It will communicate with the controller
     */
    void onUndo(){
    	System.out.println("[DEBUG] User clicked on : undo");
        
    }
    
    /**
     * The private method is called when the button redo
     * is clicked. It will communicate with the controller
     */ 
    void onRedo(){
    	System.out.println("[DEBUG] User clicked on : redo");
    	
    }
    /**
     * The private method is called when the button line
     * is clicked. It will communicate with the controller
     */
    void onLine(){
    	// TODO define how shape will be implemented
    	System.out.println("[DEBUG] User clicked on : line");
    }
    
    /**
     * The private method is called when the button group
     * is clicked. It will communicate with the controller
     */
    void onGroup(){
    	// TODO define how shape will be implemented
    	System.out.println("[DEBUG] User clicked on : group");
    }
    
    /**
     * The private method is called when the button floor up
     * is clicked. It will communicate with the controller
     */
    void onFloorUp(){
    	System.out.println("[DEBUG] User clicked on : floorUp");	
    }
    
    /**
     * The private method is called when the button floor down
     * is clicked. It will communicate with the controller
     */
    void onFloorDown(){
    	System.out.println("[DEBUG] User clicked on : floor down");
    }
    
    /**
     * The private method is called when the button 2D
     * is clicked. It will communicate with the controller
     */
    void on2d(){
    	System.out.println("[DEBUG] User clicked on : go2D");
    }
    
    /**
     * The private method is called when the button 3D
     * is clicked. It will communicate with the controller
     */
    void on3d(){
    	System.out.println("[DEBUG] User clicked on : go3D");
    }

}
