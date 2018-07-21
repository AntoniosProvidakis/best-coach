package gr.antoniosprovidakis.bestcoach.tablemodels;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Antonios Providakis <ant.providakis@gmail.com>
 */
public class MatchCalenderTableModel extends AbstractTableModel {

    private Connection connection = null;
    private Statement statement = null;
    private ResultSet resultSet = null;
    private ResultSetMetaData rsMetadata;
    private ArrayList<String> titles;
    private ArrayList data;
    private final String SELECTFROMMATCHES = "SELECT id, date, home_or_away, opponent FROM matches ORDER BY date ASC";

    public MatchCalenderTableModel(Connection conn) {
        try {
            connection = conn;
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            resultSet = statement.executeQuery(SELECTFROMMATCHES);
            rsMetadata = resultSet.getMetaData();

            titles = new ArrayList<>();
            for (int column = 0; column < rsMetadata.getColumnCount(); column++) {
                titles.add(rsMetadata.getColumnLabel(column + 1));
            }

            data = new ArrayList();
            while (resultSet.next()) {
                ArrayList newRow = new ArrayList();
                newRow.add(resultSet.getInt("id"));
                newRow.add(resultSet.getDate("date"));
                newRow.add(resultSet.getString("home_or_away"));
                newRow.add(resultSet.getString("opponent"));
                data.add(newRow);
            }
            resultSet.beforeFirst();
        } catch (SQLException ex) {
            System.out.println("from costructor");
            Logger.getLogger(TeamCenterTableModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return titles.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return ((ArrayList) data.get(rowIndex)).get(columnIndex);
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        try {
            Object oldValue = getValueAt(rowIndex, columnIndex);
            // only update the data if the new value is different
            // from the old one
            if (oldValue == null && aValue == null) {
                return;
            }
            // can use equals() only if oldValue is not null
            if (oldValue != null) {
                if (oldValue.equals(aValue)) {
                    return;
                }
            }
            // JTable is 0 based while ResultSet is 1 based
            resultSet.absolute(rowIndex + 1);
            updateResultSet(resultSet, aValue, rowIndex, columnIndex);

            resultSet.updateRow();

            // save the change
            connection.commit();

            ArrayList aRow = (ArrayList) data.get(rowIndex);
            aRow.set(columnIndex, aValue);
            // notify all TableModelListeners about the change
            fireTableCellUpdated(rowIndex, columnIndex);
        } catch (SQLException ex) {
            System.out.println(ex);

            try {
                // undo the change
                connection.rollback();
            } catch (SQLException ex2) {
                System.out.println(ex2);
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }

    }

    private void updateResultSet(ResultSet rs, Object aValue, int rowIndex, int columnIndex) throws Exception {
        // if cell is empty insert null
        if (aValue == null || aValue.toString().trim().length() == 0) {
            rs.updateNull(columnIndex + 1);
            return;
        }

        switch (rsMetadata.getColumnType(columnIndex + 1)) {
            case Types.CHAR:
            case Types.VARCHAR:
            case Types.LONGVARCHAR:
                rs.updateString(columnIndex + 1, (String) aValue);
                return;

            case Types.DATE:
                Date date = new Date(((java.util.Date) aValue).getTime());
                rs.updateDate(columnIndex + 1, date); //(java.sql.Date) aValue);
                return;

            default:
                rs.updateObject(columnIndex + 1, aValue);
                return;
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        try {
            String className = rsMetadata.getColumnClassName(columnIndex + 1);
            return Class.forName(className);
        } catch (SQLException | ClassNotFoundException exception) {
            System.out.println("from getColumnClass()");
            exception.printStackTrace();
        }
        return Object.class;
    }

    @Override
    public String getColumnName(int column) {
        return titles.get(column);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            return false;
        } else {
            return true;
        }
    }

    public void deleteRow(int row) {
        try {
            PreparedStatement deleteMatch = null;
            int id = (Integer) ((ArrayList) data.get(row)).get(0);
            String deleteQuery = "delete from matches where id=?";

            deleteMatch = connection.prepareStatement(deleteQuery);
            deleteMatch.setInt(1, id);
            deleteMatch.executeUpdate();
            System.out.println(deleteQuery + " " + id);
            fireTableRowsDeleted(row - 1, row - 1);
            resultSet = statement.executeQuery(SELECTFROMMATCHES);
            rsMetadata = resultSet.getMetaData();
            data.remove(row);
            deleteMatch.close();
        } catch (SQLException ex) {
            System.out.println("from deleteRow in  tablemodel");
            Logger.getLogger(TeamCenterTableModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addRow(ArrayList newRow) {
        try {
            PreparedStatement insertMatch = null;
            Date date = (Date) newRow.get(0);
            String homeOrAway = (String) newRow.get(1);
            String opponent = (String) newRow.get(2);

            String insertQuery = "insert into matches(date, home_or_away, opponent) values( ?,?,?)";
            insertMatch = connection.prepareStatement(insertQuery);

            insertMatch.setDate(1, date);
            insertMatch.setString(2, homeOrAway);
            insertMatch.setString(3, opponent);

            insertMatch.executeUpdate();

            fireTableRowsInserted(getRowCount() - 1, getRowCount() - 1);
            resultSet = statement.executeQuery(SELECTFROMMATCHES);
            rsMetadata = resultSet.getMetaData();
            resultSet.last();
            int id = resultSet.getInt("id");
            System.out.println(" id=" + id);
            resultSet.beforeFirst();
            newRow.add(0, id);
            data.add(newRow);
        } catch (SQLException ex) {
            System.out.println("from addRow in tablemodel");
            Logger.getLogger(TeamCenterTableModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
