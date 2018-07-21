package gr.antoniosprovidakis.bestcoach.gui;

import gr.antoniosprovidakis.bestcoach.tablemodels.YellowCardsTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

/**
 *
 * @author Antonios Providakis <ant.providakis@gmail.com>
 */
public class YellowCardsDialog extends JDialog {

    private Connection connection;
    private Statement statement;
    private ResultSet resultSet;
    private YellowCardsTableModel yellowCardsTableModel;
    private JTable yellowCardsTable;
    private JScrollPane scrollpane;
    private JPanel panelBottom;
    private JButton btnSaveAndExit;
    private String lastName;
    private String middleName;
    private String firstName;
    private String SELECTALLFROMMATCHESWHEREID = "select last_name, middle_name, first_name from players where id=";

    public YellowCardsDialog(Connection conn, final int id) {
        SELECTALLFROMMATCHESWHEREID += id;

        // database staff
        try {
            connection = conn;
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultSet = statement.executeQuery(SELECTALLFROMMATCHESWHEREID);
            resultSet.next();
            lastName = resultSet.getString("last_name");
            middleName = resultSet.getString("middle_name");
            firstName = resultSet.getString("first_name");
        } catch (SQLException e) {
            System.out.println("from constructor");
            e.printStackTrace();
        }

        //setup ui
        setTitle("Yellow Cards" + " - " + lastName + " " + middleName + "." + " " + firstName);
        setSize(600, 650);
        setModal(true);
        setLocationRelativeTo(rootPane);
        setLayout(new BorderLayout());
        setIconImage(new ImageIcon(getClass().getResource("/gr/antoniosprovidakis/bestcoach/res/icons/yellow_card_icon.png")).getImage());

        yellowCardsTableModel = new YellowCardsTableModel(conn, id);
        yellowCardsTable = new JTable(yellowCardsTableModel);
        scrollpane = new JScrollPane(yellowCardsTable);

        panelBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnSaveAndExit = new JButton(new AbstractAction("Save And Exit") {
            @Override
            public void actionPerformed(ActionEvent e) {
                yellowCardsTableModel.commitChanges();
                System.out.println("yellow cards dialog saved and disposed");
                dispose();
            }
        });

        btnSaveAndExit.setIcon(new ImageIcon(getClass().getResource("/gr/antoniosprovidakis/bestcoach/res/icons/save_icon.png")));

        add(scrollpane, BorderLayout.CENTER);
        add(panelBottom, BorderLayout.SOUTH);
        panelBottom.add(btnSaveAndExit);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    statement.close();
                    resultSet.close();
                    System.out.println("rating dialog disposed");
                    dispose();
                } catch (SQLException ex) {
                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

    }
}
