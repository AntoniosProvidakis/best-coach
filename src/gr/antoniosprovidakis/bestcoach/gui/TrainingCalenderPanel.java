package gr.antoniosprovidakis.bestcoach.gui;

import gr.antoniosprovidakis.bestcoach.tableaddons.CustomSpinnerEditor;
import gr.antoniosprovidakis.bestcoach.tableaddons.LineNumberTableRowHeader;
import gr.antoniosprovidakis.bestcoach.tablemodels.TrainingCalenderTableModel;
import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JDateChooserCellEditor;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.Date;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;

/**
 *
 * @author Antonios Providakis <ant.providakis@gmail.com>
 */
public class TrainingCalenderPanel extends JPanel {

    private Connection connection;
    private JTable trainingsTable;
    private TrainingCalenderTableModel trainingCalenderTableModel;
    private LineNumberTableRowHeader lntrh;
    private JScrollPane scrollpane;
    private JPopupMenu rcMenu;
    private JMenuItem mItemOpenPlans;
    private JMenuItem mItemOpenAttendanceSheet;
    private JPanel panelCenter;
    private JPanel panelLeft;
    private JPanel panelFields;
    private JLabel lblDate;
    private JLabel lblDuration;
    private JSpinner fieldDuration;
    private JDateChooser trainingDateChooser;
    private JButton btnAdd;
    private JButton btnDelete;
    private JButton btnClear;

    private int rowId;

    public TrainingCalenderPanel(Connection conn) {
        connection = conn;

        setLayout(new BorderLayout());

        panelCenter = new JPanel();
        add(panelCenter, BorderLayout.CENTER);

        panelLeft = new JPanel(new GridLayout(2, 1));
        add(panelLeft, BorderLayout.WEST);

        panelFields = new JPanel(new GridLayout(11, 2));
        panelLeft.add(panelFields);

        lblDate = new JLabel("Training Date:");
        lblDate.setHorizontalAlignment(SwingConstants.CENTER);
        lblDuration = new JLabel("Duration (mins) :");
        lblDuration.setHorizontalAlignment(SwingConstants.CENTER);

        fieldDuration = new JSpinner(new SpinnerNumberModel(0, 0, null, 1));

        trainingDateChooser = new JDateChooser();

        btnAdd = new JButton("Add Training");
        btnAdd.setEnabled(false);
        btnDelete = new JButton("Delete Training(s)");
        btnDelete.setEnabled(false);
        btnClear = new JButton("Clear Fields");

        btnAdd.setIcon(new ImageIcon(getClass().getResource("/gr/antoniosprovidakis/bestcoach/res/icons/add_icon.png")));
        btnDelete.setIcon(new ImageIcon(getClass().getResource("/gr/antoniosprovidakis/bestcoach/res/icons/minus_icon.png")));
        btnClear.setIcon(new ImageIcon(getClass().getResource("/gr/antoniosprovidakis/bestcoach/res/icons/eraser_icon.png")));

        panelFields.add(lblDate);
        panelFields.add(trainingDateChooser);
        panelFields.add(lblDuration);
        panelFields.add(fieldDuration);
        panelFields.add(new Box.Filler(null, null, null));
        panelFields.add(new Box.Filler(null, null, null));
        panelFields.add(new Box.Filler(null, null, null));
        panelFields.add(btnAdd);
        panelFields.add(new Box.Filler(null, null, null));
        panelFields.add(btnDelete);
        panelFields.add(new Box.Filler(null, null, null));
        panelFields.add(btnClear);

        trainingCalenderTableModel = new TrainingCalenderTableModel(conn);
        trainingsTable = new JTable(trainingCalenderTableModel);
        trainingsTable.setAutoCreateRowSorter(true);
        trainingsTable.getTableHeader().setReorderingAllowed(false);

        TableColumn colGoals = trainingsTable.getColumnModel().getColumn(2);
        colGoals.setCellEditor(new CustomSpinnerEditor(0, 0, null, 1));

        JDateChooserCellEditor cellEditorDateChooser = new JDateChooserCellEditor();
        TableColumn colDate = trainingsTable.getColumnModel().getColumn(1);
        colDate.setCellEditor(cellEditorDateChooser);

        // TODO: remove id column
        trainingsTable.setRowHeight(25);
        scrollpane = new JScrollPane(trainingsTable);

        lntrh = new LineNumberTableRowHeader(scrollpane, trainingsTable);
        lntrh.setBackground(Color.LIGHT_GRAY);
        scrollpane.setRowHeaderView(lntrh);
        add(scrollpane, BorderLayout.CENTER);

        rcMenu = new JPopupMenu();
        mItemOpenPlans = new JMenuItem(new AbstractAction("Open Training Plans") {
            @Override
            public void actionPerformed(ActionEvent e) {
                TrainingPlansDialog trainingPlansDialog = new TrainingPlansDialog(connection, rowId);
                trainingPlansDialog.setVisible(true);
            }
        });
        rcMenu.add(mItemOpenPlans);

        mItemOpenAttendanceSheet = new JMenuItem(new AbstractAction("Open Attendance Sheet") {
            @Override
            public void actionPerformed(ActionEvent e) {
                TrainingAttendanceSheetDialog trainingAttendanceSheetDialog = new TrainingAttendanceSheetDialog(connection, rowId);
                trainingAttendanceSheetDialog.setVisible(true);
            }
        });
        rcMenu.add(mItemOpenAttendanceSheet);

        // jtable listeners
        trainingsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                int r = trainingsTable.rowAtPoint(e.getPoint());
                if (r >= 0 && r < trainingsTable.getRowCount()) {
                    trainingsTable.setRowSelectionInterval(r, r);
                } else {
                    trainingsTable.clearSelection();
                }

                int rowindex = trainingsTable.getSelectedRow();
                rowId = (Integer) trainingsTable.getModel().getValueAt(trainingsTable.convertRowIndexToModel(rowindex), 0);

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
                int r = trainingsTable.rowAtPoint(e.getPoint());
                if (r >= 0 && r < trainingsTable.getRowCount()) {
                    trainingsTable.setRowSelectionInterval(r, r);
                } else {
                    trainingsTable.clearSelection();
                }

                int rowindex = trainingsTable.getSelectedRow();
                rowId = (Integer) trainingsTable.getModel().getValueAt(trainingsTable.convertRowIndexToModel(rowindex), 0);
                if (rowindex < 0) {
                    return;
                }
                if (e.isPopupTrigger() && e.getComponent() instanceof JTable) {
                    JPopupMenu popup = rcMenu;
                    popup.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });

        ListSelectionModel listSelectionModel = trainingsTable.getSelectionModel();
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
                Date birthDate = new java.sql.Date(trainingDateChooser.getDate().getTime());
                int duration = (int) fieldDuration.getValue();

                ArrayList newRow = new ArrayList();

                newRow.add(birthDate);
                newRow.add(duration);

                trainingCalenderTableModel.addRow(newRow);
            }
        });

        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int option = JOptionPane.showConfirmDialog(getParent(), "Are you sure you want to delete selected training(s)?", "Training(s) deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (option == 0) {
                    int[] rowsToDelete = trainingsTable.getSelectedRows();
                    for (int i = rowsToDelete.length - 1; i >= 0; i--) {
                        trainingCalenderTableModel.deleteRow(rowsToDelete[i]);
                    }
                    //TODO: fix position of selection after deletions
                    if (trainingsTable.getRowCount() != 0) { // check if table isn't empty
                        trainingsTable.setRowSelectionInterval(trainingsTable.getRowCount() - 1, trainingsTable.getRowCount() - 1);
                    }
                }
            }
        });

        btnClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                trainingDateChooser.setDate(null);
                fieldDuration.setValue(0);
            }
        });

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
                if (trainingDateChooser.getDate() == null || (int) fieldDuration.getValue() == 0) {
                    btnAdd.setEnabled(false);
                } else {
                    btnAdd.setEnabled(true);
                }
            }
        };

        ((JTextField) trainingDateChooser.getDateEditor()).getDocument().addDocumentListener(inputTextFieldsListener);

        fieldDuration.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                enableDisableBtnNew();
            }

            public void enableDisableBtnNew() {
                if (trainingDateChooser.getDate() == null || (int) fieldDuration.getValue() == 0) {
                    btnAdd.setEnabled(false);
                } else {
                    btnAdd.setEnabled(true);
                }
            }
        });
    }
}
