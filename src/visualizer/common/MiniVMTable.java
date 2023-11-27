package visualizer.common;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class MiniVMTable extends JScrollPane {

    private final DefaultTableModel model;

    public MiniVMTable(String[] columnNames) {
        JTable table = new JTable(model = new DefaultTableModel(null, columnNames));
        setViewportView(table);
    }

    public void clear() {
        model.setRowCount(0);
    }

    public void add(String[] row) {
        model.addRow(row);
        model.fireTableDataChanged();
    }

}
