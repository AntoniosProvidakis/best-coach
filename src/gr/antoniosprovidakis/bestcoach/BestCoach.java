package gr.antoniosprovidakis.bestcoach;

import gr.antoniosprovidakis.bestcoach.gui.MainFrame;
import java.awt.EventQueue;

/**
 *
 * @author Antonios Providakis <ant.providakis@gmail.com>
 */
public class BestCoach {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        /* Set the Nimbus look and feel */
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
        */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainFrame().setVisible(true);
            }
        });

    }
}
