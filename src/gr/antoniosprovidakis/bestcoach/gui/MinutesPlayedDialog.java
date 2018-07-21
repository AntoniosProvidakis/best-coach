package gr.antoniosprovidakis.bestcoach.gui;

import gr.antoniosprovidakis.bestcoach.tableaddons.CustomSpinnerEditor;
import gr.antoniosprovidakis.bestcoach.tablemodels.MinutesPlayedTableModel;
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
import javax.swing.table.TableColumn;

/**
 *
 * @author Antonios Providakis <ant.providakis@gmail.com>
 */
public class MinutesPlayedDialog extends JDialog {

    private Connection connection;
    private Statement statement;
    private ResultSet resultSet;
    private MinutesPlayedTableModel minutesPlayedTableModel;
    private JTable minutesPlayedTable;
    private JScrollPane scrollpane;
    private JPanel panelBottom;
    private JButton btnSaveAndExit;
    private String lastName;
    private String middleName;
    private String firstName;
    private String SELECTALLFROMMATCHESWHEREID = "select last_name, middle_name, first_name from players where id=";

    public MinutesPlayedDialog(Connection conn, final int id) {
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
        setTitle("Minutes Played" + " - " + lastName + " " + middleName + "." + " " + firstName);
        setSize(600, 400);
        setModal(true);
        setLocationRelativeTo(rootPane);
        setIconImage(new ImageIcon(getClass().getResource("/gr/antoniosprovidakis/bestcoach/res/icons/stopwatch_icon.png")).getImage());

        minutesPlayedTableModel = new MinutesPlayedTableModel(connection, id);
        minutesPlayedTable = new JTable(minutesPlayedTableModel);
        scrollpane = new JScrollPane(minutesPlayedTable);

        minutesPlayedTable.setRowHeight(25);

        TableColumn colMins = minutesPlayedTable.getColumnModel().getColumn(4);
        colMins.setCellEditor(new CustomSpinnerEditor(0, 0, 120, 1));

        panelBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnSaveAndExit = new JButton(new AbstractAction("Save And Exit") {
            @Override
            public void actionPerformed(ActionEvent e) {
                minutesPlayedTableModel.commitChanges();
                System.out.println("minutes played dialog saved and disposed");
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
                    System.out.println("minutes played dialog disposed");
                    dispose();
                } catch (SQLException ex) {
                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }
}
