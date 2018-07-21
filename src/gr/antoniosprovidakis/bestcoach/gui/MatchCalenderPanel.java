package gr.antoniosprovidakis.bestcoach.gui;

import gr.antoniosprovidakis.bestcoach.tableaddons.LineNumberTableRowHeader;
import gr.antoniosprovidakis.bestcoach.tablemodels.MatchCalenderTableModel;
import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JDateChooserCellEditor;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.Date;
import java.util.ArrayList;
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
import javax.swing.table.TableColumn;

/**
 *
 * @author Antonios Providakis <ant.providakis@gmail.com>
 */
public class MatchCalenderPanel extends JPanel {

    private Connection connection;
    private JTable matchesTable;
    private JPopupMenu rcMenu;
    private JMenuItem mItemOpenPlans;
    private LineNumberTableRowHeader lntrh;
    private JScrollPane scrollpane;
    private MatchCalenderTableModel matchCalenderTableModel;
    private JPanel panelCenter, panelLeft, panelFields;
    private JLabel lblDate, lblHomeOrAway, lblOpponent;
    private JTextField fieldOpponent;
    private String[] arrayHomeOrAway;
    private JComboBox comboHomeOrAway;
    private JDateChooser matchDateChooser;
    private JButton btnAdd, btnDelete, btnClear;

    private int rowId;

    public MatchCalenderPanel(Connection conn) {
        connection = conn;

        setLayout(new BorderLayout());

        panelCenter = new JPanel();
        add(panelCenter, BorderLayout.CENTER);

        panelLeft = new JPanel(new GridLayout(2, 1));
        add(panelLeft, BorderLayout.WEST);

        panelFields = new JPanel(new GridLayout(11, 2));
        panelLeft.add(panelFields);

        lblDate = new JLabel("Match Date:");
        lblDate.setHorizontalAlignment(SwingConstants.CENTER);
        lblHomeOrAway = new JLabel("Home Or Away:");
        lblHomeOrAway.setHorizontalAlignment(SwingConstants.CENTER);
        lblOpponent = new JLabel("Opponent:");
        lblOpponent.setHorizontalAlignment(SwingConstants.CENTER);

        fieldOpponent = new JTextField(10);

        // TODO: change strings of comboHomeOrAway to strings of resource bundle
        arrayHomeOrAway = new String[]{"HOME", "AWAY"};
        comboHomeOrAway = new JComboBox(arrayHomeOrAway);

        matchDateChooser = new JDateChooser();

        btnAdd = new JButton("Add Match");
        btnAdd.setEnabled(false);
        btnDelete = new JButton("Delete Match(es)");
        btnDelete.setEnabled(false);
        btnClear = new JButton("Clear Fields");

        btnAdd.setIcon(new ImageIcon(getClass().getResource("/gr/antoniosprovidakis/bestcoach/res/icons/add_icon.png")));
        btnDelete.setIcon(new ImageIcon(getClass().getResource("/gr/antoniosprovidakis/bestcoach/res/icons/minus_icon.png")));
        btnClear.setIcon(new ImageIcon(getClass().getResource("/gr/antoniosprovidakis/bestcoach/res/icons/eraser_icon.png")));

        panelFields.add(lblDate);
        panelFields.add(matchDateChooser);
        panelFields.add(lblHomeOrAway);
        panelFields.add(comboHomeOrAway);
        panelFields.add(lblOpponent);
        panelFields.add(fieldOpponent);
        panelFields.add(new Box.Filler(null, null, null));
        panelFields.add(new Box.Filler(null, null, null));
        panelFields.add(new Box.Filler(null, null, null));
        panelFields.add(btnAdd);
        panelFields.add(new Box.Filler(null, null, null));
        panelFields.add(btnDelete);
        panelFields.add(new Box.Filler(null, null, null));
        panelFields.add(btnClear);

        matchCalenderTableModel = new MatchCalenderTableModel(conn);
        matchesTable = new JTable(matchCalenderTableModel);
        matchesTable.setAutoCreateRowSorter(true);

        TableColumn col = matchesTable.getColumnModel().getColumn(2);
        col.setCellEditor(new DefaultCellEditor(new JComboBox(arrayHomeOrAway)));

        JDateChooserCellEditor cellEditorDateChooser = new JDateChooserCellEditor();
        TableColumn colDate = matchesTable.getColumnModel().getColumn(1);
        colDate.setCellEditor(cellEditorDateChooser);

        // TODO: remove id column
        matchesTable.setRowHeight(25);
        scrollpane = new JScrollPane(matchesTable);

        lntrh = new LineNumberTableRowHeader(scrollpane, matchesTable);
        lntrh.setBackground(Color.LIGHT_GRAY);
        scrollpane.setRowHeaderView(lntrh);
        add(scrollpane, BorderLayout.CENTER);

        rcMenu = new JPopupMenu();
        mItemOpenPlans = new JMenuItem(new AbstractAction("Open Match Plans") {
            @Override
            public void actionPerformed(ActionEvent e) {
                MatchPlansDialog matchPlansDialog = new MatchPlansDialog(connection, rowId);
                matchPlansDialog.setVisible(true);
            }
        });
        rcMenu.add(mItemOpenPlans);

        // jtable listeners
        matchesTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                int r = matchesTable.rowAtPoint(e.getPoint());
                if (r >= 0 && r < matchesTable.getRowCount()) {
                    matchesTable.setRowSelectionInterval(r, r);
                } else {
                    matchesTable.clearSelection();
                }

                int rowindex = matchesTable.getSelectedRow();
                rowId = (Integer) matchesTable.getModel().getValueAt(matchesTable.convertRowIndexToModel(rowindex), 0);

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
                int r = matchesTable.rowAtPoint(e.getPoint());
                if (r >= 0 && r < matchesTable.getRowCount()) {
                    matchesTable.setRowSelectionInterval(r, r);
                } else {
                    matchesTable.clearSelection();
                }

                int rowindex = matchesTable.getSelectedRow();
                rowId = (Integer) matchesTable.getModel().getValueAt(matchesTable.convertRowIndexToModel(rowindex), 0);
                if (rowindex < 0) {
                    return;
                }
                if (e.isPopupTrigger() && e.getComponent() instanceof JTable) {
                    JPopupMenu popup = rcMenu;
                    popup.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });

        ListSelectionModel listSelectionModel = matchesTable.getSelectionModel();
        listSelectionModel.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                ListSelectionModel lsm = (ListSelectionModel) e.getSource();
                btnDelete.setEnabled(!lsm.isSelectionEmpty());
            }
        });

        // buttons listeners
        btnAdd.addActionListener(
                new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Date birthDate = new java.sql.Date(matchDateChooser.getDate().getTime());
                String homeOrAway = (String) comboHomeOrAway.getSelectedItem();
                String opponent = fieldOpponent.getText();

                ArrayList newRow = new ArrayList();

                newRow.add(birthDate);
                newRow.add(homeOrAway);
                newRow.add(opponent);

                matchCalenderTableModel.addRow(newRow);
            }
        });

        btnDelete.addActionListener(
                new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int option = JOptionPane.showConfirmDialog(getParent(), "Are you sure you want to delete selected match(es)?", "Match(es) deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (option == 0) {
                    int[] rowsToDelete = matchesTable.getSelectedRows();
                    for (int i = rowsToDelete.length - 1; i >= 0; i--) {
                        matchCalenderTableModel.deleteRow(rowsToDelete[i]);
                    }
                    //TODO: fix position of selection after deletions
                    if (matchesTable.getRowCount() != 0) { // check if table isn't empty
                        matchesTable.setRowSelectionInterval(matchesTable.getRowCount() - 1, matchesTable.getRowCount() - 1);
                    }
                }
            }
        });

        btnClear.addActionListener(
                new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                matchDateChooser.setDate(null);
                comboHomeOrAway.setSelectedIndex(0);
                fieldOpponent.setText("");
            }
        });

        // input fields listeners
        KeyListener inputFieldsListener = new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                //if (!(!Character.isDigit(c) || !Character.isAlphabetic(e.getKeyChar())  || c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE)) {
                if (!(Character.isAlphabetic(e.getKeyChar()) || Character.isDigit(e.getKeyChar()) || c == KeyEvent.VK_BACK_SPACE
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
        fieldOpponent.addKeyListener(inputFieldsListener);

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
                if (matchDateChooser.getDate() == null || fieldOpponent.getText().isEmpty()) {
                    btnAdd.setEnabled(false);
                } else {
                    btnAdd.setEnabled(true);
                }
            }
        };

        fieldOpponent.getDocument()
                .addDocumentListener(inputTextFieldsListener);
        ((JTextField) matchDateChooser.getDateEditor()).getDocument().addDocumentListener(inputTextFieldsListener);
    }
}
