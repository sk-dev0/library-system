package library.ui.panel;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import library.model.Genre;
import library.service.LibraryService;
import library.ui.dialog.GenreEditDialog;

public class GenrePanel extends JPanel {
    private LibraryService service;
    private DefaultTableModel tableModel;
    private JTable table;

    public GenrePanel(LibraryService service) {
        this.service = service;
        setLayout(new BorderLayout());

        String[] columns = { "ID", "Name", "Prefix" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add");
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");
        buttonPanel.add(addButton);
        addButton.setPreferredSize(new java.awt.Dimension(80, 30));
        buttonPanel.add(editButton);
        editButton.setPreferredSize(new java.awt.Dimension(80, 30));
        buttonPanel.add(deleteButton);
        deleteButton.setPreferredSize(new java.awt.Dimension(80, 30));
        add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> {
            GenreEditDialog dialog = new GenreEditDialog((java.awt.Frame) SwingUtilities.getWindowAncestor(this),
                    service, null);
            dialog.setVisible(true);
            if (dialog.isSaved()) {
                loadData();
            }
        });

        editButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(this, "Please select a row");
                return;
            }
            int id = Integer.parseInt((String) table.getValueAt(selectedRow, 0));
            Genre genre = service.findGenreById(id);
            GenreEditDialog dialog = new GenreEditDialog((java.awt.Frame) SwingUtilities.getWindowAncestor(this),
                    service, genre);
            dialog.setVisible(true);
            if (dialog.isSaved()) {
                loadData();
            }
        });

        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(this, "Please select a row");
                return;
            }
            int id = Integer.parseInt((String) table.getValueAt(selectedRow, 0));
            String name = (String) table.getValueAt(selectedRow, 1);
            int result = JOptionPane.showConfirmDialog(this, "Delete genre '" + name + "'?", "Confirm",
                    JOptionPane.YES_NO_OPTION);
            if (result != JOptionPane.YES_OPTION) {
                return;
            }
            try {
                service.deleteGenre(id);
                loadData();
            } catch (RuntimeException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        loadData();
    }

    public void loadData() {
        tableModel.setRowCount(0);
        List<String[]> rows = service.getAllGenreRows();
        for (String[] r : rows) {
            tableModel.addRow(r);
        }
    }
}
