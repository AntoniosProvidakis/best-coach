package gr.antoniosprovidakis.bestcoach.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

/**
 *
 * @author Antonios Providakis <ant.providakis@gmail.com>
 */
public class TrainingPlansDialog extends JDialog {

    private Connection connection = null;
    private Statement statement = null;
    private ResultSet resultSet = null;
    private JPanel panelBottom;
    private JScrollPane scrollPane;
    private TextAreaWithOptions textAreaTrainingPlans;
    private JLabel lblTrainingPlans;
    private JButton btnSaveAndExit;
    private String date;
    private int duration;
    private String trainingPlans;
    private String SELECTALLFROMMATCHESWHEREID = "select date, plans, duration from trainings where id=";

    public TrainingPlansDialog(Connection conn, final int id) {

        SELECTALLFROMMATCHESWHEREID += id;

        // database staff
        try {
            connection = conn;
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            resultSet = statement.executeQuery(SELECTALLFROMMATCHESWHEREID);
            resultSet.next();
            date = resultSet.getDate("date").toString();
            trainingPlans = resultSet.getString("plans");
            duration = resultSet.getInt("duration");
        } catch (SQLException e) {
            System.out.println("from constructor");
            e.printStackTrace();
        }

        // setup ui
        setTitle("Training Plans - " + date + " " + "Duration:" + " " + duration + " " + "minutes");
        setLocation(150, 30);
        setSize(500, 600);
        setModal(true);
        setLocationRelativeTo(rootPane);
        setLayout(new BorderLayout(0, 5));

        textAreaTrainingPlans = new TextAreaWithOptions();
        textAreaTrainingPlans.setFont(new Font("sansserif", Font.PLAIN, 16));
        scrollPane = new JScrollPane(textAreaTrainingPlans);

        lblTrainingPlans = new JLabel("Training Plans");
        lblTrainingPlans.setHorizontalAlignment(SwingConstants.CENTER);
        lblTrainingPlans.setFont(new Font("sansserif", Font.PLAIN, 16));

        panelBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnSaveAndExit = new JButton("Save And Exit");

        add(lblTrainingPlans, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(panelBottom, BorderLayout.SOUTH);
        panelBottom.add(btnSaveAndExit);


        //button listener
        btnSaveAndExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String newTrainingPlans = textAreaTrainingPlans.getText();

                boolean trainingPlansHasChanged = false;

                if (!newTrainingPlans.equals(trainingPlans)) {
                    trainingPlansHasChanged = true;
                }

                PreparedStatement updateTraining = null;

                String updateQuery = null;

                try {
                    if (trainingPlansHasChanged) {
                        updateQuery = "update trainings set plans=? where id=?";
                        updateTraining = connection.prepareStatement(updateQuery);
                        updateTraining.setString(1, newTrainingPlans);
                        updateTraining.setInt(2, id);
                        System.out.println(updateQuery);

                    }
//                    else {
//                        JOptionPane.showMessageDialog(rootPane, "Nothing has changed", "Save", JOptionPane.INFORMATION_MESSAGE);
//                    }

                    if (updateTraining != null) {
                        updateTraining.executeUpdate();
                        updateTraining.close();
                    }

                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                try {
                    statement.close();
                    resultSet.close();
                    System.out.println("trainings plans dialog saved and disposed");
                    dispose();
                } catch (SQLException ex) {
                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    statement.close();
                    resultSet.close();
                    System.out.println("plans dialog disposed");
                    dispose();
                } catch (SQLException ex) {
                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

    }
}
