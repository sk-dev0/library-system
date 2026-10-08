package library.ui.panel;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import library.model.Member;
import library.service.LibraryService;
import library.ui.dialog.MemberEditDialog;
import library.util.IdFormatter;

public class MemberPanel extends JPanel {
    private LibraryService service;
    private DefaultTableModel tableModel;
    private JTable table;

    private JTextField nameSearchField;

    public MemberPanel(LibraryService service) {
        this.service = service;
        setLayout(new BorderLayout());

        String[] columns = { "ID", "Name" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JPanel searchPanel = new JPanel();
        JLabel nameSearch = new JLabel("nameSearch:");
        nameSearchField = new JTextField(10);
        JButton searchButton = new JButton("search");
        JButton clearButton = new JButton("clear");
        searchPanel.add(nameSearch);
        searchPanel.add(nameSearchField);
        searchPanel.add(searchButton);
        searchPanel.add(clearButton);
        add(searchPanel, BorderLayout.NORTH);

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

        searchButton.addActionListener(e -> {
            loadData();
        });

        clearButton.addActionListener(e -> {
            nameSearchField.setText("");
            loadData();
        });

        addButton.addActionListener(e -> {
            MemberEditDialog dialog = new MemberEditDialog((java.awt.Frame) SwingUtilities.getWindowAncestor(this),
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
            int id = IdFormatter.parseMemberId((String) table.getValueAt(selectedRow, 0));
            Member member = service.findMemberById(id);
            MemberEditDialog dialog = new MemberEditDialog((java.awt.Frame) SwingUtilities.getWindowAncestor(this),
                    service, member);
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
            int id = IdFormatter.parseMemberId((String) table.getValueAt(selectedRow, 0));
            String name = (String) table.getValueAt(selectedRow, 1);
            int result = JOptionPane.showConfirmDialog(this, "Delete mamber '" + name + "'?", "Confirm",
                    JOptionPane.YES_NO_OPTION);
            if (result != JOptionPane.YES_OPTION) {
                return;
            }
            try {
                service.deleteMember(id);
                loadData();
            } catch (RuntimeException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        loadData();
    }

    public void loadData() {
        tableModel.setRowCount(0);
        String name = nameSearchField.getText().trim();
        List<String[]> rows = service.searchMemberRows(name);
        for (String[] r : rows) {
            tableModel.addRow(r);
        }
    }
}
