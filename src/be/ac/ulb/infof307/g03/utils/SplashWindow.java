package be.ac.ulb.infof307.g03.utils;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;

/**
 * Splash screen to entertain user when opening the project
 * @author pierre
 *
 */
public class SplashWindow extends JWindow
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param f the parent
	 * @param waitTime timer
	 */
	public SplashWindow(Frame f, int waitTime)
    {
        super(f);
        
        String classPath = getClass().getResource("SplashWindow.class").toString();
        String prefix;
        ImageIcon splash;
        
		if(classPath.subSequence(0, 3).equals("rsr")){
    		prefix = "/";
    		splash = new ImageIcon(getClass().getResource(prefix + "loadingscreen.gif"));
    	} else {
    		prefix = System.getProperty("user.dir") + "/src/be/ac/ulb/infof307/g03/assets/Tools/";
    		splash = new ImageIcon(prefix + "loadingscreen.gif");
    	}
        
        JLabel l = new JLabel(splash);
        getContentPane().add(l, BorderLayout.CENTER);
        pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension labelSize = l.getPreferredSize();
        setLocation(screenSize.width/2 - (labelSize.width/2), screenSize.height/2 - (labelSize.height/2));
        addMouseListener(new MouseAdapter(){
                public void mousePressed(MouseEvent e){
                    setVisible(false);
                    dispose();
                }
            });
        final int pause = waitTime;
        final Runnable closerRunner = new Runnable(){
                public void run(){
                    setVisible(false);
                    dispose();
                }
            };
        Runnable waitRunner = new Runnable(){
                public void run(){
                    try{
                            Thread.sleep(pause);
                            SwingUtilities.invokeAndWait(closerRunner);
                        }
                    catch(Exception ex){
                            Log.exception(ex);
                            // can catch InvocationTargetException
                            // can catch InterruptedException
                        }
                }
            };
        setVisible(true);
        Thread splashThread = new Thread(waitRunner, "SplashThread");
        splashThread.start();
    }
}
