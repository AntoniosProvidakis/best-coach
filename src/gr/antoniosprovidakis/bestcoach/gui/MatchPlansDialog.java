package gr.antoniosprovidakis.bestcoach.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
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
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

/**
 *
 * @author Antonios Providakis <ant.providakis@gmail.com>
 */
public class MatchPlansDialog extends JDialog {

    Connection connection = null;
    Statement statement = null;
    ResultSet resultSet = null;
    JPanel panelLeft, panelRight, panelLeftBottom, panelRightBottom;
    JScrollPane scrollPaneLeft, scrollPaneRight;
    TextAreaWithOptions textAreaTeamInstructions, textAreaOpponentComments;
    JLabel lblTeamInstructions, lblOpponentComments, lblFormation;
    String[] arrayFormations;
    JComboBox comboFormation;
    JButton btnSaveAndExit;
    String date, homeOrAway, opponent, formation, teamInstructions, opponentComments;
    String SELECTALLFROMMATCHESWHEREID = "select date, home_or_away, opponent, formation, team_instructions, opponent_comments from matches where id=";

    public MatchPlansDialog(Connection conn, final int id) {
        SELECTALLFROMMATCHESWHEREID += id;
        // database staff
        try {
            connection = conn;
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            resultSet = statement.executeQuery(SELECTALLFROMMATCHESWHEREID);
            resultSet.next();
            date = resultSet.getDate("date").toString();
            homeOrAway = resultSet.getString("home_or_away");
            opponent = resultSet.getString("opponent");
            formation = resultSet.getString("formation");
            teamInstructions = resultSet.getString("team_instructions");
            opponentComments = resultSet.getString("opponent_comments");
        } catch (SQLException e) {
            System.out.println("from constructor");
            e.printStackTrace();
        }

        // setup ui
        setTitle("Match Plans -  " + date + " " + opponent + " " + homeOrAway);
        //setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(900, 600);
        setModal(true);
        setLocationRelativeTo(rootPane);
        setLayout(new GridLayout(1, 2, 20, 20));

        panelLeft = new JPanel(new BorderLayout());
        add(panelLeft);

        textAreaTeamInstructions = new TextAreaWithOptions();
        textAreaTeamInstructions.setFont(new Font("sansserif", Font.PLAIN, 14));
        scrollPaneLeft = new JScrollPane(textAreaTeamInstructions);

        lblTeamInstructions = new JLabel("Team Instructions");
        lblTeamInstructions.setHorizontalAlignment(SwingConstants.CENTER);
        lblTeamInstructions.setFont(new Font("sansserif", Font.PLAIN, 16));

        panelLeftBottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        lblFormation = new JLabel("Formation:");
        lblFormation.setFont(new Font("sansserif", Font.PLAIN, 14));
        lblFormation.setHorizontalAlignment(SwingConstants.CENTER);

        arrayFormations = new String[]{
            "", "4-4-2", "4-4-1-1", "4-1-2-1-2", "4-2-2-2", "4-3-1-2",
            "4-1-3-2", "4-3-3", "4-3-2-1", "4-2-3-1", "4-2-1-3",
            "4-6-0", "3-5-2", "3-6-1", "4-5-1", "3-4-3",
            "1-4-3-2", "5-4-1", "5-3-2", "3-3-1-3", "3-3-3-1"
        };
        comboFormation = new JComboBox(arrayFormations);
        //comboFormation.setSelectedItem(formation);
        comboFormation.setSelectedItem(formation);

        panelLeft.add(lblTeamInstructions, BorderLayout.NORTH);
        panelLeft.add(scrollPaneLeft, BorderLayout.CENTER);
        panelLeft.add(panelLeftBottom, BorderLayout.SOUTH);
        panelLeftBottom.add(lblFormation);
        panelLeftBottom.add(comboFormation);

        panelRight = new JPanel(new BorderLayout());
        add(panelRight);

        textAreaOpponentComments = new TextAreaWithOptions();
        textAreaOpponentComments.setFont(new Font("sansserif", Font.PLAIN, 14));
        scrollPaneRight = new JScrollPane(textAreaOpponentComments);
        lblOpponentComments = new JLabel("Opponent Commments");
        lblOpponentComments.setHorizontalAlignment(SwingConstants.CENTER);
        lblOpponentComments.setFont(new Font("sansserif", Font.PLAIN, 16));

        btnSaveAndExit = new JButton("Save And Exit");
        btnSaveAndExit.setIcon(new ImageIcon(getClass().getResource("/gr/antoniosprovidakis/bestcoach/res/icons/save_icon.png")));

        panelRightBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        panelRight.add(lblOpponentComments, BorderLayout.NORTH);
        panelRight.add(scrollPaneRight, BorderLayout.CENTER);
        panelRight.add(panelRightBottom, BorderLayout.SOUTH);
        panelRightBottom.add(btnSaveAndExit);

        //button listener
        btnSaveAndExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String newFormation = (String) comboFormation.getSelectedItem();
                String newTeamInstructions = textAreaTeamInstructions.getText();
                String newOpponentComments = textAreaOpponentComments.getText();

                boolean formHasChanged = false;
                boolean teamInstrHasChanged = false;
                boolean oppCommHasChanged = false;

                if (!newFormation.equals(formation)) {
                    formHasChanged = true;
                }
                if (!newTeamInstructions.equals(teamInstructions)) {
                    teamInstrHasChanged = true;
                }
                if (!newOpponentComments.equals(opponentComments)) {
                    oppCommHasChanged = true;
                }

                PreparedStatement updateMatch = null;

                String updateQuery = null;

                try {
                    if (formHasChanged && teamInstrHasChanged && oppCommHasChanged) {
                        updateQuery = "update matches set formation=?, team_instructions=?, opponent_comments=? where id=?";
                        updateMatch = connection.prepareStatement(updateQuery);
                        updateMatch.setString(1, newFormation);
                        updateMatch.setString(2, newTeamInstructions);
                        updateMatch.setString(3, newOpponentComments);
                        updateMatch.setInt(4, id);
                        System.out.println(updateQuery);

                    } else if (formHasChanged && teamInstrHasChanged) {
                        updateQuery = "update matches set formation=?, team_instructions=? where id=?";
                        updateMatch = connection.prepareStatement(updateQuery);
                        updateMatch.setString(1, newFormation);
                        updateMatch.setString(2, newTeamInstructions);
                        updateMatch.setInt(3, id);
                        System.out.println(updateQuery);

                    } else if (formHasChanged && oppCommHasChanged) {
                        updateQuery = "update matches set formation=?, opponent_comments=? where id=?";
                        updateMatch = connection.prepareStatement(updateQuery);
                        updateMatch.setString(1, newFormation);
                        updateMatch.setString(2, newOpponentComments);
                        updateMatch.setInt(3, id);
                        System.out.println(updateQuery);

                    } else if (teamInstrHasChanged && oppCommHasChanged) {
                        updateQuery = "update matches set team_instructions=?, opponent_comments=? where id=?";
                        updateMatch = connection.prepareStatement(updateQuery);
                        updateMatch.setString(1, newTeamInstructions);
                        updateMatch.setString(2, newOpponentComments);
                        updateMatch.setInt(3, id);
                        System.out.println(updateQuery);

                    } else if (formHasChanged) {
                        updateQuery = "update matches set formation=? where id=?";
                        updateMatch = connection.prepareStatement(updateQuery);
                        updateMatch.setString(1, newFormation);
                        updateMatch.setInt(2, id);
                        System.out.println(updateQuery);

                    } else if (teamInstrHasChanged) {
                        updateQuery = "update matches set team_instructions=? where id=?";
                        updateMatch = connection.prepareStatement(updateQuery);
                        updateMatch.setString(1, newTeamInstructions);
                        updateMatch.setInt(2, id);
                        System.out.println(updateQuery);

                    } else if (oppCommHasChanged) {
                        updateQuery = "update matches set opponent_comments=? where id=?";
                        updateMatch = connection.prepareStatement(updateQuery);
                        updateMatch.setString(1, newOpponentComments);
                        updateMatch.setInt(2, id);
                        System.out.println(updateQuery);
                    }
//                     else {
//                        JOptionPane.showMessageDialog(rootPane, "Nothing has changed", "Save", JOptionPane.INFORMATION_MESSAGE);
//
//                    }
                    if (updateMatch != null) {
                        updateMatch.executeUpdate();
                        updateMatch.close();
                    }

                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                try {
                    statement.close();
                    resultSet.close();
                    System.out.println("match plans dialog saved and disposed");
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
                    System.out.println("match plans dialog disposed");
                    dispose();
                } catch (SQLException ex) {
                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

    }
}
