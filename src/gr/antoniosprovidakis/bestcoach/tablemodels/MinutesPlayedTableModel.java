package gr.antoniosprovidakis.bestcoach.tablemodels;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Antonios Providakis <ant.providakis@gmail.com>
 */
public class MinutesPlayedTableModel extends AbstractTableModel {

    private Connection connection = null;
    private Statement statement = null;
    private ResultSet resultSet = null;
    private ResultSetMetaData rsMetadata;
    private ArrayList<String> titles;
    private ArrayList data;
    private ArrayList listToInsert;
    private ArrayList listToDelete;
    private ArrayList listToUpdate;
    private ArrayList listInitialZeroValues;
    private ArrayList listInitialPositiveValues;
    private String SELECTFROMJOIN = "select id as match_id, date, home_or_away, opponent, minutes from matches "
            + "left join minutes_played on matches.id = match_id and player_id = ";
    
    private int playerId;

    public MinutesPlayedTableModel(Connection conn, final int id) {
        playerId = id;
        SELECTFROMJOIN += playerId;

        try {
            connection = conn;
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultSet = statement.executeQuery(SELECTFROMJOIN);
            rsMetadata = resultSet.getMetaData();

            titles = new ArrayList<>();
            for (int column = 0; column < rsMetadata.getColumnCount(); column++) {
                titles.add(rsMetadata.getColumnLabel(column + 1));
            }

            listInitialZeroValues = new ArrayList();
            listInitialPositiveValues = new ArrayList();

            data = new ArrayList();
            while (resultSet.next()) {
                ArrayList newRow = new ArrayList();
                newRow.add(resultSet.getInt("match_id"));
                newRow.add(resultSet.getDate("date"));
                newRow.add(resultSet.getString("home_or_away"));
                newRow.add(resultSet.getString("opponent"));
                newRow.add(resultSet.getInt("minutes"));

                if (resultSet.getInt("minutes") == 0) {
                    listInitialZeroValues.add(resultSet.getInt("match_id"));
                } else {
                    listInitialPositiveValues.add(resultSet.getInt("match_id"));
                }

                data.add(newRow);
            }

            listToInsert = new ArrayList();
            listToDelete = new ArrayList();
            listToUpdate = new ArrayList();

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
        if (columnIndex == 4) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if ((int) aValue != 0) {
            if ((Integer) getValueAt(rowIndex, columnIndex) == 0) {
                if (listToDelete.contains(((ArrayList) data.get(rowIndex)).get(0))) {
                    listToDelete.remove(((ArrayList) data.get(rowIndex)).get(0));
                }
                if (listToUpdate.contains(((ArrayList) data.get(rowIndex)).get(0))) {
                    listToUpdate.remove(((ArrayList) data.get(rowIndex)).get(0));
                }
                listToInsert.add(((ArrayList) data.get(rowIndex)).get(0));
            } else {
                if (!listInitialZeroValues.contains(((ArrayList) data.get(rowIndex)).get(0))) {
                    listToDelete.remove(((ArrayList) data.get(rowIndex)).get(0));
                    listToInsert.remove(((ArrayList) data.get(rowIndex)).get(0));
                    if (!listToUpdate.contains(((ArrayList) data.get(rowIndex)).get(0))) {
                        listToUpdate.add(((ArrayList) data.get(rowIndex)).get(0));
                    }
                }
            }
        } else {
            if (listToInsert.contains(((ArrayList) data.get(rowIndex)).get(0))) {
                listToInsert.remove(((ArrayList) data.get(rowIndex)).get(0));
            }
            if (listToUpdate.contains(((ArrayList) data.get(rowIndex)).get(0))) {
                listToUpdate.remove(((ArrayList) data.get(rowIndex)).get(0));
            }

            listToDelete.add(((ArrayList) data.get(rowIndex)).get(0));
        }

        ((ArrayList) data.get(rowIndex)).set(columnIndex, aValue);
    }

    public void commitChanges() {
        PreparedStatement deleteMinutesPlayedEntry = null;
        PreparedStatement insertMinutesPlayedEntry = null;
        PreparedStatement updateMinutesPlayedEntry = null;
        String deleteQuery = "delete from minutes_played where player_id=" + playerId + " and match_id=?";
        String insertQuery = "insert into minutes_played (player_id, match_id, minutes) values(" + playerId + ",?,?)";
        String updateQuery = "update minutes_played set minutes=? where player_id = " + playerId + " and match_id=?";

        try {
            insertMinutesPlayedEntry = connection.prepareStatement(insertQuery);
            deleteMinutesPlayedEntry = connection.prepareStatement(deleteQuery);
            updateMinutesPlayedEntry = connection.prepareStatement(updateQuery);

            for (int i = 0; i < listToDelete.size(); i++) {
                if (!listInitialZeroValues.contains((Integer) listToDelete.get(i))) {
                    deleteMinutesPlayedEntry.setInt(1, (Integer) listToDelete.get(i));
                    deleteMinutesPlayedEntry.addBatch();
                }
            }
            deleteMinutesPlayedEntry.executeBatch();

            for (int i = 0; i < listToInsert.size(); i++) {
                if (!listInitialPositiveValues.contains((Integer) listToInsert.get(i))) {
                    insertMinutesPlayedEntry.setInt(1, (Integer) listToInsert.get(i));
                    int row = -1;
                    for (int j = 0; j < data.size(); j++) {
                        if ((Integer) ((ArrayList) data.get(j)).get(0) == (Integer) listToInsert.get(i)) {
                            row = j;
                        }
                    }
                    insertMinutesPlayedEntry.setInt(2, ((Integer) ((ArrayList) data.get(row)).get(4)));
                    insertMinutesPlayedEntry.addBatch();
                } else {
                    int row = -1;
                    for (int j = 0; j < data.size(); j++) {
                        if ((Integer) ((ArrayList) data.get(j)).get(0) == (Integer) listToInsert.get(i)) {
                            row = j;
                        }
                    }
                    updateMinutesPlayedEntry.setInt(1, (Integer) (((ArrayList) data.get(row)).get(4)));
                    updateMinutesPlayedEntry.setInt(2, (Integer) listToInsert.get(i));
                    updateMinutesPlayedEntry.addBatch();
                }
            }
            insertMinutesPlayedEntry.executeBatch();

            for (int i = 0; i < listToUpdate.size(); i++) {
                int row = -1;
                for (int j = 0; j < data.size(); j++) {
                    if ((Integer) ((ArrayList) data.get(j)).get(0) == (Integer) listToUpdate.get(i)) {
                        row = j;
                    }
                }
                updateMinutesPlayedEntry.setInt(1, (Integer) (((ArrayList) data.get(row)).get(4)));
                updateMinutesPlayedEntry.setInt(2, (Integer) listToUpdate.get(i));
                updateMinutesPlayedEntry.addBatch();
            }
            updateMinutesPlayedEntry.executeBatch();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
