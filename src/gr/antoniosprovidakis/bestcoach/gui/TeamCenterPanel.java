package gr.antoniosprovidakis.bestcoach.gui;

import gr.antoniosprovidakis.bestcoach.tablemodels.TeamCenterTableModel;
import gr.antoniosprovidakis.bestcoach.tableaddons.LineNumberTableRowHeader;
import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JDateChooserCellEditor;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import org.joda.time.DateTime;

/**
 *
 * @author Antonios Providakis <ant.providakis@gmail.com>
 */
public class TeamCenterPanel extends JPanel {

    private Connection connection;
    private JTable playersTable;
    private LineNumberTableRowHeader lntrh;
    private JScrollPane scrollpane;
    private TeamCenterTableModel teamCenterTableModel;
    private JPanel panelCenter, panelLeft, panelFields, panelExtra;
    private JLabel lblLastName,
            lblMiddleName, lblFirstName, lblNationality, lblPosition,
            lblPrefFoot, lblBirthDate, lblGoalsScoredSum, lblGoalsScoredSumValue,
            lblGoalsScoredAverage, lblGoalsScoredAverageValue, lblMinutesPlayedSum,
            lblMinutesPlayedSumValue, lblMinutesPlayedAverage,
            lblMinutesPlayedAverageValue, lblRatingAverage, lblRatingAverageValue,
            lblYellowCardsSum, lblYellowCardsSumValue, lblRedCardsSum,
            lblRedCardsSumValue, lblUnattededTimes, lblUnattendedTimesValue,
            lblAppearances, lblAppearancesValue;
    private JTextField fieldLastName, fieldMiddleName, fieldFirstName;
    private JComboBox fieldNationality;
    private String[] arrayPositions, arrayPrefFoot, arrayNationalities;
    private JComboBox comboPosition, comboPrefFoot;
    private JDateChooser birthDateChooser;
    private JButton btnAdd, btnDelete, btnClear;
    private JPopupMenu rcMenu;
    private JMenuItem mItemGoalsScored, mItemMinutesPlayed,
            mItemRating, mItemYellowCards, mItemRedCards;

    private int rowId;

    public TeamCenterPanel(Connection conn) {
        connection = conn;

        // setup ui
        setLayout(new BorderLayout());

        panelCenter = new JPanel();
        add(panelCenter, BorderLayout.CENTER);

        panelLeft = new JPanel(new GridLayout(2, 1));
        add(panelLeft, BorderLayout.WEST);

        panelFields = new JPanel(new GridLayout(11, 2));
        panelLeft.add(panelFields);

        lblLastName = new JLabel("Last Name:");
        lblLastName.setHorizontalAlignment(SwingConstants.CENTER);
        lblMiddleName = new JLabel("Middle Name:");
        lblMiddleName.setHorizontalAlignment(SwingConstants.CENTER);
        lblFirstName = new JLabel("First Name:");
        lblFirstName.setHorizontalAlignment(SwingConstants.CENTER);
        lblNationality = new JLabel("Nationality:");
        lblNationality.setHorizontalAlignment(SwingConstants.CENTER);
        lblBirthDate = new JLabel("Birth Date:");
        lblBirthDate.setHorizontalAlignment(SwingConstants.CENTER);
        lblPosition = new JLabel("Position:");
        lblPosition.setHorizontalAlignment(SwingConstants.CENTER);
        lblPrefFoot = new JLabel("Pref. Foot:");
        lblPrefFoot.setHorizontalAlignment(SwingConstants.CENTER);

        fieldLastName = new JTextField(10);
        fieldMiddleName = new JTextField(new LimitedPlainDocument(1), null, 10);
        fieldFirstName = new JTextField(10);

        // TODO: change fieldNationality to a combobox that contains countries from resource bundle
        arrayNationalities = new String[]{"Greece", "Brazil", "Portugal"};
        fieldNationality = new JComboBox(arrayNationalities);

        // TODO: change strings of comboPositions to strings of resource bundle
        arrayPositions = new String[]{"GK", "CB", "SW", "RB", "LB", "RWB", "LWB", "DM", "CM", "AM", "RM", "LM", "RW", "LW", "SS", "CF"};
        comboPosition = new JComboBox(arrayPositions);
        comboPosition.setToolTipText("<html><head></head><body>GK - Goalkeeper<br>"
                + "CB - Centre Back<br>"
                + "SW - Sweeper<br>"
                + "RB - Right Back<br>"
                + "LB - Left Back<br>"
                // TODO: fix tooltip, use String.format()
                + "LB - Left Back<br>"
                + "LB - Left Back<br>"
                + "LB - Left Back<br>"
                + "LB - Left Back<br>"
                + "LB - Left Back<br>"
                + "LB - Left Back<br>"
                + "LB - Left Back<br>"
                + "LB - Left Back<br>"
                + "LB - Left Back<br>"
                + "LB - Left Back<br>"
                + "CF - Centre Forward</body></html>");

        // TODO: change strings of comboPrefFoot to strings of resource bundle
        arrayPrefFoot = new String[]{"R", "L", "R&L"};
        comboPrefFoot = new JComboBox(arrayPrefFoot);

        birthDateChooser = new JDateChooser();

        btnAdd = new JButton("Add Player");
        btnAdd.setEnabled(false);
        btnAdd.setToolTipText("Add a player with the specified information.");
        btnDelete = new JButton("Delete Player(s)");
        btnDelete.setEnabled(false);
        btnDelete.setToolTipText("Delete the selected player.");
        btnClear = new JButton("Clear Fields");
        btnClear.setToolTipText("Clear the fields from above.");

        btnAdd.setIcon(new ImageIcon(getClass().getResource("/gr/antoniosprovidakis/bestcoach/res/icons/add_icon.png")));
        btnDelete.setIcon(new ImageIcon(getClass().getResource("/gr/antoniosprovidakis/bestcoach/res/icons/minus_icon.png")));
        btnClear.setIcon(new ImageIcon(getClass().getResource("/gr/antoniosprovidakis/bestcoach/res/icons/eraser_icon.png")));

        panelFields.add(lblLastName);
        panelFields.add(fieldLastName);
        panelFields.add(lblMiddleName);
        panelFields.add(fieldMiddleName);
        panelFields.add(lblFirstName);
        panelFields.add(fieldFirstName);
        panelFields.add(lblNationality);
        panelFields.add(fieldNationality);
        panelFields.add(lblBirthDate);
        panelFields.add(birthDateChooser);
        panelFields.add(lblPosition);
        panelFields.add(comboPosition);
        panelFields.add(lblPrefFoot);
        panelFields.add(comboPrefFoot);
        panelFields.add(new Box.Filler(null, null, null));
        panelFields.add(new Box.Filler(null, null, null));
        panelFields.add(new Box.Filler(null, null, null));
        panelFields.add(btnAdd);
        panelFields.add(new Box.Filler(null, null, null));
        panelFields.add(btnDelete);
        panelFields.add(new Box.Filler(null, null, null));
        panelFields.add(btnClear);

        panelExtra = new JPanel(new GridLayout(11, 2));
        panelLeft.add(panelExtra);

        lblAppearances = new JLabel("Appearances:");
        lblAppearances.setHorizontalAlignment(SwingConstants.RIGHT);
        lblGoalsScoredSum = new JLabel("Goals Scored Sum:");
        lblGoalsScoredSum.setHorizontalAlignment(SwingConstants.RIGHT);
        lblGoalsScoredAverage = new JLabel("Goals Scored Average:");
        lblGoalsScoredAverage.setHorizontalAlignment(SwingConstants.RIGHT);
        lblMinutesPlayedSum = new JLabel("Minutes Played Sum:");
        lblMinutesPlayedSum.setHorizontalAlignment(SwingConstants.RIGHT);
        lblMinutesPlayedAverage = new JLabel("Minutes Played Average:");
        lblMinutesPlayedAverage.setHorizontalAlignment(SwingConstants.RIGHT);
        lblRatingAverage = new JLabel("Rating Average:");
        lblRatingAverage.setHorizontalAlignment(SwingConstants.RIGHT);
        lblYellowCardsSum = new JLabel("Yellow Cards Sum:");
        lblYellowCardsSum.setHorizontalAlignment(SwingConstants.RIGHT);
        lblRedCardsSum = new JLabel("Red Cards Sum:");
        lblRedCardsSum.setHorizontalAlignment(SwingConstants.RIGHT);
        lblUnattededTimes = new JLabel("Unattended Times:");
        lblUnattededTimes.setHorizontalAlignment(SwingConstants.RIGHT);

        lblAppearancesValue = new JLabel("");
        //TODO: find an icon for appearances
        //lblAppearancesValue.setIcon(new ImageIcon(getClass().getResource("/gr/antoniosprovidakis/bestcoach/res/icons/ball_icon.png")));
        lblAppearancesValue.setHorizontalAlignment(SwingConstants.CENTER);
        lblAppearancesValue.setHorizontalTextPosition(SwingConstants.LEFT);
        lblGoalsScoredSumValue = new JLabel("");
        //lblGoalsScoredSumValue.setIcon(new ImageIcon(getClass().getResource("/gr/antoniosprovidakis/bestcoach/res/icons/ball_icon.png")));
        lblGoalsScoredSumValue.setHorizontalAlignment(SwingConstants.CENTER);
        lblGoalsScoredSumValue.setHorizontalTextPosition(SwingConstants.LEFT);
        lblGoalsScoredAverageValue = new JLabel("");
        //lblGoalsScoredAverageValue.setIcon(new ImageIcon(getClass().getResource("/gr/antoniosprovidakis/bestcoach/res/icons/ball_icon.png")));
        lblGoalsScoredAverageValue.setHorizontalAlignment(SwingConstants.CENTER);
        lblGoalsScoredAverageValue.setHorizontalTextPosition(SwingConstants.LEFT);
        lblMinutesPlayedSumValue = new JLabel("");
        //lblMinutesPlayedSumValue.setIcon(new ImageIcon(getClass().getResource("/gr/antoniosprovidakis/bestcoach/res/icons/stopwatch_icon.png")));
        lblMinutesPlayedSumValue.setHorizontalAlignment(SwingConstants.CENTER);
        lblMinutesPlayedSumValue.setHorizontalTextPosition(SwingConstants.LEFT);
        lblMinutesPlayedAverageValue = new JLabel("");
        //lblMinutesPlayedAverageValue.setIcon(new ImageIcon(getClass().getResource("/gr/antoniosprovidakis/bestcoach/res/icons/stopwatch_icon.png")));
        lblMinutesPlayedAverageValue.setHorizontalAlignment(SwingConstants.CENTER);
        lblMinutesPlayedAverageValue.setHorizontalTextPosition(SwingConstants.LEFT);
        lblRatingAverageValue = new JLabel("");
        //lblRatingAverageValue.setIcon(new ImageIcon(getClass().getResource("/gr/antoniosprovidakis/bestcoach/res/icons/ten_icon.png")));
        lblRatingAverageValue.setHorizontalAlignment(SwingConstants.CENTER);
        lblRatingAverageValue.setHorizontalTextPosition(SwingConstants.LEFT);
        lblYellowCardsSumValue = new JLabel("");
        //lblYellowCardsSumValue.setIcon(new ImageIcon(getClass().getResource("/gr/antoniosprovidakis/bestcoach/res/icons/yellow_card_icon.png")));
        lblYellowCardsSumValue.setHorizontalAlignment(SwingConstants.CENTER);
        lblYellowCardsSumValue.setHorizontalTextPosition(SwingConstants.LEFT);
        lblRedCardsSumValue = new JLabel("");
        //lblRedCardsSumValue.setIcon(new ImageIcon(getClass().getResource("/gr/antoniosprovidakis/bestcoach/res/icons/red_card_icon.png")));
        lblRedCardsSumValue.setHorizontalAlignment(SwingConstants.CENTER);
        lblRedCardsSumValue.setHorizontalTextPosition(SwingConstants.LEFT);
        lblUnattendedTimesValue = new JLabel("");
        //lblUnattendedTimesValue.setIcon(new ImageIcon(getClass().getResource("/gr/antoniosprovidakis/bestcoach/res/icons/address_book-icon.png")));
        lblUnattendedTimesValue.setHorizontalAlignment(SwingConstants.CENTER);
        lblUnattendedTimesValue.setHorizontalTextPosition(SwingConstants.LEFT);

        panelExtra.add(new Box.Filler(null, null, null));
        panelExtra.add(new Box.Filler(null, null, null));
        panelExtra.add(lblAppearances);
        panelExtra.add(lblAppearancesValue);
        panelExtra.add(lblGoalsScoredSum);
        panelExtra.add(lblGoalsScoredSumValue);
        panelExtra.add(lblGoalsScoredAverage);
        panelExtra.add(lblGoalsScoredAverageValue);
        panelExtra.add(lblMinutesPlayedSum);
        panelExtra.add(lblMinutesPlayedSumValue);
        panelExtra.add(lblMinutesPlayedAverage);
        panelExtra.add(lblMinutesPlayedAverageValue);
        panelExtra.add(lblRatingAverage);
        panelExtra.add(lblRatingAverageValue);
        panelExtra.add(lblYellowCardsSum);
        panelExtra.add(lblYellowCardsSumValue);
        panelExtra.add(lblRedCardsSum);
        panelExtra.add(lblRedCardsSumValue);
        panelExtra.add(lblUnattededTimes);
        panelExtra.add(lblUnattendedTimesValue);

        teamCenterTableModel = new TeamCenterTableModel(connection);
        playersTable = new JTable(teamCenterTableModel);
        playersTable.setAutoCreateRowSorter(true);

        DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();
        TableColumn colAge = playersTable.getColumnModel().getColumn(8);
        dtcr.setHorizontalAlignment(SwingConstants.LEFT);
        colAge.setCellRenderer(dtcr);

        JDateChooserCellEditor cellEditorDateChooser = new JDateChooserCellEditor();
        TableColumn colDate = playersTable.getColumnModel().getColumn(7);
        colDate.setCellEditor(cellEditorDateChooser);

        // TODO: remove id column
        //playersTable.removeColumn(playersTable.getColumnModel().getColumn(0));
        playersTable.setRowHeight(25);
        playersTable.getColumnModel().getColumn(6).setPreferredWidth(85);
        playersTable.getColumnModel().getColumn(7).setPreferredWidth(95);
        scrollpane = new JScrollPane(playersTable);

        TableColumn colNation = playersTable.getColumnModel().getColumn(4);
        colNation.setCellEditor(new DefaultCellEditor(new JComboBox(arrayNationalities)));
        TableColumn colPos = playersTable.getColumnModel().getColumn(5);
        colPos.setCellEditor(new DefaultCellEditor(new JComboBox(arrayPositions)));
        TableColumn colPrefFoot = playersTable.getColumnModel().getColumn(6);
        colPrefFoot.setCellEditor(new DefaultCellEditor(new JComboBox(arrayPrefFoot)));

        lntrh = new LineNumberTableRowHeader(scrollpane, playersTable);
        lntrh.setBackground(Color.LIGHT_GRAY);
        scrollpane.setRowHeaderView(lntrh);
        add(scrollpane, BorderLayout.CENTER);

        rcMenu = new JPopupMenu();
        mItemGoalsScored = new JMenuItem(new AbstractAction("Goals Scored") {
            @Override
            public void actionPerformed(ActionEvent e) {
                GoalsScoredDialog goalsScoredDialog = new GoalsScoredDialog(connection, rowId);
                goalsScoredDialog.setVisible(true);
            }
        });
        mItemGoalsScored.setIcon(new ImageIcon(getClass().getResource("/gr/antoniosprovidakis/bestcoach/res/icons/ball_icon.png")));
        rcMenu.add(mItemGoalsScored);

        mItemMinutesPlayed = new JMenuItem(new AbstractAction("Minutes Played") {
            @Override
            public void actionPerformed(ActionEvent e) {
                MinutesPlayedDialog minutesPlayedDialog = new MinutesPlayedDialog(connection, rowId);
                minutesPlayedDialog.setVisible(true);
            }
        });
        mItemMinutesPlayed.setIcon(new ImageIcon(getClass().getResource("/gr/antoniosprovidakis/bestcoach/res/icons/stopwatch_icon.png")));
        rcMenu.add(mItemMinutesPlayed);

        mItemRating = new JMenuItem(new AbstractAction("Rating") {
            @Override
            public void actionPerformed(ActionEvent e) {
                new RatingDialog(connection, rowId);
            }
        });
        mItemRating.setIcon(new ImageIcon(getClass().getResource("/gr/antoniosprovidakis/bestcoach/res/icons/ten_icon.png")));
        rcMenu.add(mItemRating);

        mItemYellowCards = new JMenuItem(new AbstractAction("Yellow Cards") {
            @Override
            public void actionPerformed(ActionEvent e) {
                new YellowCardsDialog(connection, rowId);
            }
        });
        mItemYellowCards.setIcon(new ImageIcon(getClass().getResource("/gr/antoniosprovidakis/bestcoach/res/icons/yellow_card_icon.png")));
        rcMenu.add(mItemYellowCards);

        mItemRedCards = new JMenuItem(new AbstractAction("Red Cards") {
            @Override
            public void actionPerformed(ActionEvent e) {
                new RedCardsDialog(connection, rowId);
            }
        });
        mItemRedCards.setIcon(new ImageIcon(getClass().getResource("/gr/antoniosprovidakis/bestcoach/res/icons/red_card_icon.png")));
        rcMenu.add(mItemRedCards);

        // jtable listeners
        playersTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                int r = playersTable.rowAtPoint(e.getPoint());
                if (r >= 0 && r < playersTable.getRowCount()) {
                    playersTable.setRowSelectionInterval(r, r);
                } else {
                    playersTable.clearSelection();
                }

                int rowindex = playersTable.getSelectedRow();
                rowId = (Integer) playersTable.getModel().getValueAt(playersTable.convertRowIndexToModel(rowindex), 0);

                setExtraFieldsInfo();

                if (rowindex < 0) {
                    return;
                }
                if (e.isPopupTrigger() && e.getComponent() instanceof JTable) {
                    JPopupMenu popup = rcMenu;
                    popup.show(e.getComponent(), e.getX(), e.getY());
                }
            }

            // this runs on Mac OS X
            @Override
            public void mousePressed(MouseEvent e) {
                int r = playersTable.rowAtPoint(e.getPoint());
                if (r >= 0 && r < playersTable.getRowCount()) {
                    playersTable.setRowSelectionInterval(r, r);
                } else {
                    playersTable.clearSelection();
                }

                int rowindex = playersTable.getSelectedRow();
                rowId = (Integer) playersTable.getModel().getValueAt(playersTable.convertRowIndexToModel(rowindex), 0);

                if (rowindex < 0) {
                    return;
                }
                if (e.isPopupTrigger() && e.getComponent() instanceof JTable) {
                    JPopupMenu popup = rcMenu;
                    popup.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });

        playersTable.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    if ((playersTable.getSelectedRow() - 1) < 0) { //make sure you are not out of table
                        return;
                    }
                    rowId = (Integer) playersTable.getModel().getValueAt(playersTable.convertRowIndexToModel(playersTable.getSelectedRow() - 1), 0);
                    setExtraFieldsInfo();
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    if ((playersTable.getSelectedRow() + 1) == playersTable.getRowCount()) {//make sure you are not out of table
                        return;
                    }
                    rowId = (Integer) playersTable.getModel().getValueAt(playersTable.convertRowIndexToModel(playersTable.getSelectedRow() + 1), 0);
                    setExtraFieldsInfo();
                }
            }
        });

        ListSelectionModel listSelectionModel = playersTable.getSelectionModel();
        listSelectionModel.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                ListSelectionModel lsm = (ListSelectionModel) e.getSource();
                btnDelete.setEnabled(!lsm.isSelectionEmpty());
            }
        });

        // buttons listeners
        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String lastName = fieldLastName.getText();
                String middleName = fieldMiddleName.getText();
                String firstName = fieldFirstName.getText();
                String nationality = (String) fieldNationality.getSelectedItem();
                String position = (String) comboPosition.getSelectedItem();
                String prefFoot = (String) comboPrefFoot.getSelectedItem();
                Date birthDate = null;
                if (birthDateChooser.getDate() == null) {
                    birthDate = new java.sql.Date(DateTime.now().getMillis());
                } else {
                    birthDate = new java.sql.Date(birthDateChooser.getDate().getTime());
                }

                ArrayList newRow = new ArrayList();

                newRow.add(lastName);
                newRow.add(middleName);
                newRow.add(firstName);
                newRow.add(nationality);
                newRow.add(position);
                newRow.add(prefFoot);
                newRow.add(birthDate);

                teamCenterTableModel.addRow(newRow);
            }
        });

        btnDelete.addActionListener(
                new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int option = JOptionPane.showConfirmDialog(getParent(), "Are you sure you want to delete selected player(s)?", "Player(s) deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (option == 0) {
                    int[] rowsToDelete = playersTable.getSelectedRows();
                    for (int i = rowsToDelete.length - 1; i >= 0; i--) {
                        teamCenterTableModel.deleteRow(rowsToDelete[i]);
                    }
                    //TODO: fix position of selection after deletions
                    if (playersTable.getRowCount() != 0) { // check if table isn't empty
                        playersTable.setRowSelectionInterval(playersTable.getRowCount() - 1, playersTable.getRowCount() - 1);
                    }
                }
            }
        });

        btnClear.addActionListener(
                new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fieldFirstName.setText("");
                fieldMiddleName.setText("");
                fieldLastName.setText("");
                fieldNationality.setSelectedIndex(0);
                comboPosition.setSelectedIndex(0);
                comboPrefFoot.setSelectedIndex(0);
                birthDateChooser.setDate(null);
            }
        });

        // input fields listeners
        KeyListener inputFieldsListener = new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                //if (!(!Character.isDigit(c) || !Character.isAlphabetic(e.getKeyChar())  || c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE)) {
                if (!(Character.isAlphabetic(e.getKeyChar()) || c == KeyEvent.VK_BACK_SPACE
                        || c == KeyEvent.VK_DELETE || e.getKeyChar() == ' ' || e.getKeyChar() == '-' || e.isControlDown())) {
                    getToolkit().beep();
                    e.consume();
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        };

        fieldLastName.addKeyListener(inputFieldsListener);

        fieldMiddleName.addKeyListener(inputFieldsListener);

        fieldFirstName.addKeyListener(inputFieldsListener);
        // Listen for changes in the text(input fields)
        DocumentListener inputTextFieldsListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                enableDisableBtnNew();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                enableDisableBtnNew();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }

            public void enableDisableBtnNew() {
                if (fieldLastName.getText().isEmpty() || fieldMiddleName.getText().isEmpty()
                        || fieldFirstName.getText().isEmpty() || birthDateChooser.getDate() == null) {//one of five is empty or five of them
                    btnAdd.setEnabled(false);
                } else { // all five have values
                    btnAdd.setEnabled(true);
                }
            }
        };

        fieldLastName.getDocument()
                .addDocumentListener(inputTextFieldsListener);
        fieldMiddleName.getDocument()
                .addDocumentListener(inputTextFieldsListener);
        fieldFirstName.getDocument()
                .addDocumentListener(inputTextFieldsListener);
        ((JTextField) birthDateChooser.getDateEditor()).getDocument().addDocumentListener(inputTextFieldsListener);

    }

    public void setExtraFieldsInfo() {
        setAppearancesValue(rowId);
        setGoalsScoredSumValue(rowId);
        setGoalsScoredAverageValue(rowId);
        setMinutesPlayedSumValue(rowId);
        setMinutesPlayedAverageValue(rowId);
        setRatingAverageValue(rowId);
        setYellowCardsSumValue(rowId);
        setRedCardsSumValue(rowId);
        setUnattededTimesSumValue(rowId);
    }

    public void setAppearancesValue(int playerId) {
        Statement statement = null;
        ResultSet rs = null;
        try {
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = statement.executeQuery("select COUNT(*) AS appearances FROM minutes_played WHERE player_id=" + playerId);
            rs.next();
            lblAppearancesValue.setText(Integer.toString(rs.getInt("appearances")));
        } catch (SQLException ex) {
            Logger.getLogger(TeamCenterPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            if (statement != null) {
                statement.close();
            }
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setGoalsScoredSumValue(int playerId) {
        Statement statement = null;
        ResultSet rs = null;
        try {
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = statement.executeQuery("select SUM(goals) AS goals_sum FROM goals_scored WHERE player_id=" + playerId);
            rs.next();
            lblGoalsScoredSumValue.setText(Integer.toString(rs.getInt("goals_sum")));
        } catch (SQLException ex) {
            Logger.getLogger(TeamCenterPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            if (statement != null) {
                statement.close();
            }
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setGoalsScoredAverageValue(int playerId) {
        Statement statement = null;
        ResultSet rs = null;
        try {
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = statement.executeQuery("select sum(goals)*1.0 / count(*) as goals_avg from goals_scored where player_id =" + playerId);
            rs.next();
            double value = rs.getDouble("goals_avg");
            DecimalFormat df = new DecimalFormat("####.###");
            lblGoalsScoredAverageValue.setText(df.format(value));
        } catch (SQLException ex) {
            Logger.getLogger(TeamCenterPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            if (statement != null) {
                statement.close();
            }
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setMinutesPlayedSumValue(int playerId) {
        Statement statement = null;
        ResultSet rs = null;
        try {
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = statement.executeQuery("select SUM(minutes) AS minutes_sum FROM minutes_played WHERE player_id=" + playerId);
            rs.next();
            lblMinutesPlayedSumValue.setText(Integer.toString(rs.getInt("minutes_sum")));
        } catch (SQLException ex) {
            Logger.getLogger(TeamCenterPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            if (statement != null) {
                statement.close();
            }
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setMinutesPlayedAverageValue(int playerId) {
        Statement statement = null;
        ResultSet rs = null;
        try {
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = statement.executeQuery("select sum(minutes)*1.0 / count(*) as minutes_avg from minutes_played where player_id =" + playerId);
            rs.next();
            double value = rs.getDouble("minutes_avg");
            DecimalFormat df = new DecimalFormat("####.###");
            lblMinutesPlayedAverageValue.setText(df.format(value));
        } catch (SQLException ex) {
            Logger.getLogger(TeamCenterPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            if (statement != null) {
                statement.close();
            }
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setRatingAverageValue(int playerId) {
        Statement statement = null;
        ResultSet rs = null;
        try {
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = statement.executeQuery("select AVG(rating) AS rating_avg FROM ratings WHERE player_id=" + playerId);
            rs.next();
            lblRatingAverageValue.setText(Double.toString(rs.getInt("rating_avg")));
        } catch (SQLException ex) {
            Logger.getLogger(TeamCenterPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            if (statement != null) {
                statement.close();
            }
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setYellowCardsSumValue(int playerId) {
        Statement statement = null;
        ResultSet rs = null;
        try {
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = statement.executeQuery("select COUNT(*) AS cards_sum FROM yellow_cards WHERE player_id =" + playerId);
            rs.next();
            lblYellowCardsSumValue.setText(Integer.toString(rs.getInt("cards_sum")));
        } catch (SQLException ex) {
            Logger.getLogger(TeamCenterPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            if (statement != null) {
                statement.close();
            }
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setRedCardsSumValue(int playerId) {
        Statement statement = null;
        ResultSet rs = null;
        try {
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = statement.executeQuery("select COUNT(*) AS cards_sum FROM red_cards WHERE player_id =" + playerId);
            rs.next();
            lblRedCardsSumValue.setText(Integer.toString(rs.getInt("cards_sum")));
        } catch (SQLException ex) {
            Logger.getLogger(TeamCenterPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            if (statement != null) {
                statement.close();
            }
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setUnattededTimesSumValue(int playerId) {
        Statement statement = null;
        ResultSet rs = null;
        try {
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = statement.executeQuery("select COUNT(*) AS times_sum FROM unattended WHERE player_id =" + playerId);
            rs.next();
            lblUnattendedTimesValue.setText(Integer.toString(rs.getInt("times_sum")));
        } catch (SQLException ex) {
            Logger.getLogger(TeamCenterPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            if (statement != null) {
                statement.close();
            }
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
