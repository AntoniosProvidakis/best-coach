package gr.antoniosprovidakis.bestcoach.tableaddons;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import javax.swing.AbstractCellEditor;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.TableCellEditor;

/**
 *
 * @author Antonios Providakis <ant.providakis@gmail.com>
 */
public class CustomSpinnerEditor extends AbstractCellEditor implements TableCellEditor {

    final JSpinner spinner = new JSpinner();

    public CustomSpinnerEditor(Integer value, Integer minimum, Integer maximum, Integer stepsize) {
        spinner.setModel(new SpinnerNumberModel(value, minimum, maximum, stepsize));
    }

    public CustomSpinnerEditor(Double value, Double minimum, Double maximum, Double stepsize) {
        spinner.setModel(new SpinnerNumberModel(value, minimum, maximum, stepsize));
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
            int row, int column) {
        spinner.setValue(value);
        return spinner;
    }

    @Override
    public boolean isCellEditable(EventObject evt) {
        if (evt instanceof MouseEvent) {
            return ((MouseEvent) evt).getClickCount() >= 2;
        }
        return true;
    }

    @Override
    public Object getCellEditorValue() {
        return spinner.getValue();
    }
}
