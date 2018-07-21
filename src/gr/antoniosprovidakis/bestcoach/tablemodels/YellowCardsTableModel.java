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
public class YellowCardsTableModel extends AbstractTableModel {

    private Connection connection = null;
    private Statement statement = null;
    private ResultSet resultSet = null;
    private ResultSetMetaData rsMetadata;
    private ArrayList<String> titles;
    private ArrayList data;
    private ArrayList listToInsert;
    private ArrayList listToDelete;
    private ArrayList listInitialTrueState;
    private ArrayList listInitialFalseState;
    private final String SELECTPART1 = "select id as match_id, date, home_or_away, opponent, "
            + "id in ( select match_id from yellow_cards where player_id =";
    private final String SELECTPART2 = ") as yellow_cards from matches";
    private String SELECTFROMTRAININGS;

    private int playerId;

    public YellowCardsTableModel(Connection conn, final int id) {
        playerId = id;
        SELECTFROMTRAININGS = SELECTPART1;
        SELECTFROMTRAININGS += playerId;
        SELECTFROMTRAININGS += SELECTPART2;

        try {
            connection = conn;
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultSet = statement.executeQuery(SELECTFROMTRAININGS);
            rsMetadata = resultSet.getMetaData();

            titles = new ArrayList<>();
            for (int column = 0; column < rsMetadata.getColumnCount(); column++) {
                titles.add(rsMetadata.getColumnLabel(column + 1));
            }

            listInitialTrueState = new ArrayList();
            listInitialFalseState = new ArrayList();

            data = new ArrayList();
            while (resultSet.next()) {
                ArrayList newRow = new ArrayList();
                newRow.add(resultSet.getInt("match_id"));
                newRow.add(resultSet.getDate("date"));
                newRow.add(resultSet.getString("home_or_away"));
                newRow.add(resultSet.getString("opponent"));
                newRow.add(resultSet.getBoolean("yellow_cards"));

                if (resultSet.getBoolean("yellow_cards") == true) {
                    listInitialTrueState.add(resultSet.getInt("id"));
                } else {
                    listInitialFalseState.add(resultSet.getInt("id"));
                }
                data.add(newRow);
            }

            listToInsert = new ArrayList();
            listToDelete = new ArrayList();

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
        ((ArrayList) data.get(rowIndex)).set(columnIndex, aValue);

        if ((boolean) aValue == true) {
            if (listToDelete.contains(((ArrayList) data.get(rowIndex)).get(0))) {
                listToDelete.remove(((ArrayList) data.get(rowIndex)).get(0));
            }
            listToInsert.add(((ArrayList) data.get(rowIndex)).get(0));
        } else {
            if (listToInsert.contains(((ArrayList) data.get(rowIndex)).get(0))) {
                listToInsert.remove(((ArrayList) data.get(rowIndex)).get(0));
            }
            listToDelete.add(((ArrayList) data.get(rowIndex)).get(0));
        }
    }

    public void commitChanges() {
        PreparedStatement deleteYellowCards = null;
        PreparedStatement insertYellowCards = null;
        String deleteQuery = "delete from yellow_cards where match_id=?";
        String insertQuery = "insert into yellow_cards (player_id, match_id) values(" + playerId + ",?)";

        try {
            insertYellowCards = connection.prepareStatement(insertQuery);
            deleteYellowCards = connection.prepareStatement(deleteQuery);

            for (int i = 0; i < listToDelete.size(); i++) {
                if (!listInitialFalseState.contains((Integer) listToDelete.get(i))) {
                    deleteYellowCards.setInt(1, (Integer) listToDelete.get(i));
                    deleteYellowCards.addBatch();
                }
            }
            deleteYellowCards.executeBatch();

            for (int i = 0; i < listToInsert.size(); i++) {
                if (!listInitialTrueState.contains((Integer) listToInsert.get(i))) {
                    insertYellowCards.setInt(1, (Integer) listToInsert.get(i));
                    insertYellowCards.addBatch();
                }
            }
            insertYellowCards.executeBatch();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
