/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package zpp;

import javax.swing.SwingUtilities;

/**
 *
 * @author akasza
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
          SwingUtilities.invokeLater(new Runnable(){
            public void run() {
                Gui gui=new Gui();
                gui.initGUI();
            }

        });
    }

}
