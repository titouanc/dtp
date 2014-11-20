

package be.ac.ulb.infof307.g03.views;


import java.awt.*;
import java.awt.event.*;

public class LoadingScreen extends Frame{
	private SplashScreen _splash;
	

    public LoadingScreen() {
    	_splash = SplashScreen.getSplashScreen();
        
        if (_splash == null) {
            System.out.println("SplashScreen.getSplashScreen() returned null");
            return;
        }
        Graphics2D g = _splash.createGraphics();
        if (g == null) {
            System.out.println("g is null");
            return;
        }
        _splash.update();

        try {
          Thread.sleep(2000);
        }
        catch(InterruptedException ex) {
          System.out.println(ex);
        }
        _splash.close();
        setVisible(true);
        toFront();
    }
    public void close(){
        _splash.close();
    }
    
}