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
public class TeamCenterTableModel extends AbstractTableModel {

    private Statement statement = null;
    private ResultSet resultSet = null;
    private Connection connection = null;
    private ResultSetMetaData rsMetadata;
    private ArrayList<String> titles;
    private ArrayList data;
    private final String SELECTALLFROMPLAYERS = "SELECT id, last_name, middle_name,first_name,"
            + " nationality, position, preferred_foot, birth_date, age FROM players ORDER BY id";
    //int dateTime = Types.DATE;

    public TeamCenterTableModel(Connection conn) {
        try {
            connection = conn;
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            resultSet = statement.executeQuery(SELECTALLFROMPLAYERS);
            rsMetadata = resultSet.getMetaData();

            //columnCount = getColumnCount();
            //titles = new ArrayList<String>();
            titles = new ArrayList<>();
            for (int column = 0; column < rsMetadata.getColumnCount(); column++) {
                titles.add(rsMetadata.getColumnLabel(column + 1));
            }

            data = new ArrayList();
            while (resultSet.next()) {
                ArrayList newRow = new ArrayList();
                newRow.add(resultSet.getInt("id"));
                newRow.add(resultSet.getString("last_name"));
                newRow.add(resultSet.getString("middle_name"));
                newRow.add(resultSet.getString("first_name"));
                newRow.add(resultSet.getString("nationality"));
                newRow.add(resultSet.getString("position"));
                newRow.add(resultSet.getString("preferred_foot"));
                newRow.add(resultSet.getDate("birth_date"));
                newRow.add(resultSet.getInt("age"));
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
            aRow.set(8, resultSet.getInt("age")); // set new value at age 
            // notify all TableModelListeners about the change
            fireTableCellUpdated(rowIndex, columnIndex);
            fireTableCellUpdated(rowIndex, 8);
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
            /*
             case Types.INTEGER:
             rs.updateInt(columnIndex + 1, ((Integer) aValue).intValue());
             return;

             case Types.SMALLINT:
             rs.updateShort(columnIndex + 1, ((Short) aValue).shortValue());
             return;

             case Types.TINYINT:
             rs.updateByte(columnIndex + 1, ((Byte) aValue).byteValue());
             return;
             case Types.FLOAT:
             case Types.DOUBLE:
             rs.updateDouble(columnIndex + 1, ((Double) aValue).doubleValue());
             return;

             case Types.REAL:
             rs.updateFloat(columnIndex + 1, ((Float) aValue).floatValue());
             return;

             case Types.BIT:
             rs.updateBoolean(columnIndex + 1, ((Boolean) aValue).booleanValue());
             return;

             case Types.BIGINT:
             rs.updateLong(columnIndex + 1, ((Long) aValue).longValue());
             return;

             case Types.NUMERIC:
             case Types.DECIMAL:
             rs.updateBigDecimal(columnIndex + 1, (BigDecimal) aValue);
             return;
             */
            case Types.DATE:
                Date date = new Date(((java.util.Date) aValue).getTime());
                rs.updateDate(columnIndex + 1, date); //(java.sql.Date) aValue);
                return;
            /*
             case Types.TIMESTAMP:
             if (dateTime == Types.TIMESTAMP) {
             rs.updateTimestamp(columnIndex + 1, (Timestamp) aValue);
             }
             if (dateTime == Types.TIME) {
             rs.updateTime(columnIndex + 1, (Time) aValue);
             }
             if (dateTime == Types.DATE) {
             rs.updateDate(columnIndex + 1, (java.sql.Date) aValue);
             }
             return;

             case Types.TIME:
             rs.updateTime(columnIndex + 1, (Time) aValue);
             return;
             */
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
        if (columnIndex == 0 || columnIndex == 8) {
            return false;
        } else {
            return true;
        }
    }

    public void deleteRow(int row) {
        try {
            PreparedStatement deletePlayer = null;
            int id = (Integer) ((ArrayList) data.get(row)).get(0);
            String deleteQuery = "delete from players where id=?";

            deletePlayer = connection.prepareStatement(deleteQuery);
            deletePlayer.setInt(1, id);
            deletePlayer.executeUpdate();
            System.out.println(deleteQuery + " " + id);
            fireTableRowsDeleted(row - 1, row - 1);
            resultSet = statement.executeQuery(SELECTALLFROMPLAYERS);
            rsMetadata = resultSet.getMetaData();
            data.remove(row);
            deletePlayer.close();
        } catch (SQLException ex) {
            System.out.println("from deleteRow in  tablemodel");
            Logger.getLogger(TeamCenterTableModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addRow(ArrayList newRow) {
        try {
            PreparedStatement insertPlayer = null;
            String lastName = (String) newRow.get(0);
            String middleName = (String) newRow.get(1);
            String firstName = (String) newRow.get(2);
            String nationality = (String) newRow.get(3);
            String position = (String) newRow.get(4);
            String prefFoot = (String) newRow.get(5);
            Date birthDate = (Date) newRow.get(6);

            String insertQuery = "insert into players(last_name, middle_name,first_name, nationality, "
                    + "position, preferred_foot, birth_date) values( ?,?,?,?,?,?,? )";
            insertPlayer = connection.prepareStatement(insertQuery);

            insertPlayer.setString(1, lastName);
            insertPlayer.setString(2, middleName);
            insertPlayer.setString(3, firstName);
            insertPlayer.setString(4, nationality);
            insertPlayer.setString(5, position);
            insertPlayer.setString(6, prefFoot);
            insertPlayer.setDate(7, birthDate);

            insertPlayer.executeUpdate();

            fireTableRowsInserted(getRowCount() - 1, getRowCount() - 1);
            resultSet = statement.executeQuery(SELECTALLFROMPLAYERS);
            rsMetadata = resultSet.getMetaData();
            resultSet.last();
            int id = resultSet.getInt("id");
            int age = resultSet.getInt("age");
            System.out.println(" id=" + id);
            resultSet.beforeFirst();
            newRow.add(0, id);
            newRow.add(age);
            data.add(newRow);
        } catch (SQLException ex) {
            System.out.println("from addRow in tablemodel");
            Logger.getLogger(TeamCenterTableModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
