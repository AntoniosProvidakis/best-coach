package gr.antoniosprovidakis.bestcoach.gui;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;

/**
 *
 * @author Antonios Providakis <ant.providakis@gmail.com>
 */
public class MainFrame extends JFrame {

    private Connection connection = null;
    private JTabbedPane mainTabbedPanel;

    public MainFrame() {
        connectToDB();

        /*Frame setup*/
        setTitle("Best Coach");
        setMinimumSize(new Dimension(900, 650));
        setLocationRelativeTo(null);
        setIconImage(new ImageIcon(getClass().getResource("/gr/antoniosprovidakis/bestcoach/res/icons/best_coach_icon.png")).getImage());

        mainTabbedPanel = new JTabbedPane();
        setContentPane(mainTabbedPanel);

        mainTabbedPanel.add("Team Center", new TeamCenterPanel(connection));
        mainTabbedPanel.add("Training calender", new TrainingCalenderPanel(connection));
        mainTabbedPanel.add("Match Calender", new MatchCalenderPanel(connection));

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    connection.close(); //terminate connection

                    System.out.println("Disconnected from database");
                    dispose();
                    System.exit(0); //calling the method is a must
                } catch (SQLException ex) {
                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        setVisible(true);
    }

    private void connectToDB() {

        // TODO: implement tables creation if database not found

        try {
            Class.forName("org.h2.Driver");
            connection = DriverManager.getConnection("jdbc:h2:./db/best_coach");
            System.out.println("Connected to database");
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Could't connect to database");
            System.out.println("Now exiting");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
