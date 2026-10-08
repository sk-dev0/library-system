package library.ui.panel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.List;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import library.model.Book;
import library.model.Genre;
import library.service.LibraryService;
import library.ui.dialog.BookEditDialog;
import library.util.IdFormatter;

public class BookPanel extends JPanel {
    private LibraryService service;
    private DefaultTableModel tableModel;
    private JTable table;

    private JTextField titleSearchField;
    private JTextField authorSearchField;
    private JComboBox<Genre> genreSearchField;

    public BookPanel(LibraryService service) {
        this.service = service;
        setLayout(new BorderLayout());

        String[] columns = { "ID", "Title", "Author", "Genre", "TotalCopies", "LoanedCopies" };
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
        JLabel titleSearch = new JLabel("titleSearch:");
        titleSearchField = new JTextField(20);
        JLabel authorSearch = new JLabel("authorSearch");
        authorSearchField = new JTextField(15);
        JLabel genreSearch = new JLabel("GenreSearch");
        genreSearchField = new JComboBox<>();
        genreSearchField.addItem(null);
        for (Genre g : service.findAllGenre()) {
            genreSearchField.addItem(g);
        }
        genreSearchField.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                Object display = (value == null) ? "All" : value;
                return super.getListCellRendererComponent(list, display, index, isSelected, cellHasFocus);
            }
        });
        JButton searchButton = new JButton("search");
        JButton clearButton = new JButton("clear");

        searchPanel.add(titleSearch);
        searchPanel.add(titleSearchField);
        searchPanel.add(authorSearch);
        searchPanel.add(authorSearchField);
        searchPanel.add(genreSearch);
        searchPanel.add(genreSearchField);
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
            titleSearchField.setText("");
            authorSearchField.setText("");
            genreSearchField.setSelectedIndex(0);
            loadData();
        });

        addButton.addActionListener(e -> {
            BookEditDialog dialog = new BookEditDialog((java.awt.Frame) SwingUtilities.getWindowAncestor(this), service,
                    null);
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
            int id = IdFormatter.parseBookId((String) table.getValueAt(selectedRow, 0));
            Book book = service.findBookById(id);
            BookEditDialog dialog = new BookEditDialog((java.awt.Frame) SwingUtilities.getWindowAncestor(this), service,
                    book);
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
            int id = IdFormatter.parseBookId((String) table.getValueAt(selectedRow, 0));
            String name = (String) table.getValueAt(selectedRow, 1);
            String author = (String) table.getValueAt(selectedRow, 2);
            int result = JOptionPane.showConfirmDialog(this, "Delete Book '" + name + "' written by '" + author + "' ?",
                    "Confirm", JOptionPane.YES_NO_OPTION);
            if (result != JOptionPane.YES_OPTION) {
                return;
            }
            try {
                service.deleteBook(id);
                loadData();
            } catch (RuntimeException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        loadData();
    }

    public void loadData() {
        tableModel.setRowCount(0);
        String title = titleSearchField.getText().trim();
        String author = authorSearchField.getText().trim();
        Genre genre = (Genre) genreSearchField.getSelectedItem();
        List<String[]> rows = service.searchBookRows(title, author, genre);
        for (String[] r : rows) {
            tableModel.addRow(r);
        }
    }
}
