package gr.antoniosprovidakis.bestcoach.gui;

import gr.antoniosprovidakis.bestcoach.tablemodels.AttendanceTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

/**
 *
 * @author Antonios Providakis <ant.providakis@gmail.com>
 */
public class TrainingAttendanceSheetDialog extends JDialog {

    private AttendanceTableModel attTableModel;
    private JTable unattendedTable;
    private JScrollPane scrollpane;
    private JPanel panelBottom;
    private JButton btnSaveAndExit;

    public TrainingAttendanceSheetDialog(Connection conn, final int id) {
        setTitle("Unattended Players");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(600, 650);
        setModal(true);
        setLocationRelativeTo(rootPane);
        setLayout(new BorderLayout());

        attTableModel = new AttendanceTableModel(conn, id);
        unattendedTable = new JTable(attTableModel);
        scrollpane = new JScrollPane(unattendedTable);

        unattendedTable.setRowHeight(20);
        
        panelBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnSaveAndExit = new JButton(new AbstractAction("Save And Exit") {
            @Override
            public void actionPerformed(ActionEvent e) {
                attTableModel.commitChanges();
                System.out.println("trainings attendance dialog saved and disposed");
                dispose();
            }
        });

        add(scrollpane, BorderLayout.CENTER);
        add(panelBottom, BorderLayout.SOUTH);
        panelBottom.add(btnSaveAndExit);
    }
}
