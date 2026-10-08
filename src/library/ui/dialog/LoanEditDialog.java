package library.ui.dialog;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

import library.model.*;
import library.service.LibraryService;
import library.util.IdFormatter;

public class LoanEditDialog extends JDialog {
    private LibraryService service;
    private boolean saved = false;

    private JTextField titleField;
    private JTextField bookIdField;
    private JTextField nameField;
    private JTextField memberIdField;
    private JLabel titleError;
    private JLabel bookIdError;
    private JLabel nameError;
    private JLabel memberIdError;

    private JTextField bookTitleSearch;
    private JTextField bookAuthorSearch;
    private JComboBox<Genre> bookGenreSearch;
    private DefaultTableModel bookTableModel;
    private JTable bookTable;

    private JTextField memberNameSearch;
    private DefaultTableModel memberTableModel;
    private JTable memberTable;

    public LoanEditDialog(java.awt.Frame owner, LibraryService service) {
        super(owner, true);
        this.service = service;

        setTitle("Lend Book");
        setSize(1000, 500);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                buildFormPanel(),
                buildReferenceTabs());
        splitPane.setDividerLocation(360);
        add(splitPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);

        okButton.addActionListener(e -> onOk());
        cancelButton.addActionListener(e -> dispose());

        loadBookData();
        loadMemberData();
    }

    private JPanel buildFormPanel() {
        JPanel formPanel = new JPanel(new GridLayout(8, 2, 5, 5));

        formPanel.add(new JLabel("Book Title"));
        titleField = new JTextField();
        formPanel.add(titleField);

        formPanel.add(new JLabel(""));
        titleError = new JLabel(" ");
        titleError.setForeground(Color.RED);
        formPanel.add(titleError);

        formPanel.add(new JLabel("Book Id:"));
        bookIdField = new JTextField();
        formPanel.add(bookIdField);

        formPanel.add(new JLabel(""));
        bookIdError = new JLabel(" ");
        bookIdError.setForeground(Color.RED);
        formPanel.add(bookIdError);

        formPanel.add(new JLabel("Member Name:"));
        nameField = new JTextField();
        formPanel.add(nameField);

        formPanel.add(new JLabel(""));
        nameError = new JLabel(" ");
        nameError.setForeground(Color.RED);
        formPanel.add(nameError);

        formPanel.add(new JLabel("Member Id:"));
        memberIdField = new JTextField();
        formPanel.add(memberIdField);

        formPanel.add(new JLabel(""));
        memberIdError = new JLabel(" ");
        memberIdError.setForeground(Color.RED);
        formPanel.add(memberIdError);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(formPanel, BorderLayout.NORTH);
        return wrapper;
    }

    private JTabbedPane buildReferenceTabs() {
        JTabbedPane pane = new JTabbedPane();
        pane.addTab("Books", buildBookReferencePanel());
        pane.addTab("Members", buildMemberReferencePanel());
        return pane;
    }

    private JPanel buildBookReferencePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("title:"));
        bookTitleSearch = new JTextField(10);
        searchPanel.add(bookTitleSearch);
        searchPanel.add(new JLabel("author:"));
        bookAuthorSearch = new JTextField(8);
        searchPanel.add(bookAuthorSearch);
        searchPanel.add(new JLabel("genre:"));
        bookGenreSearch = new JComboBox<>();
        bookGenreSearch.addItem(null);
        for (Genre g : service.findAllGenre()) {
            bookGenreSearch.addItem(g);
        }
        bookGenreSearch.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                Object display = (value == null) ? "All" : value;
                return super.getListCellRendererComponent(list, display, index, isSelected, cellHasFocus);
            }
        });
        searchPanel.add(bookGenreSearch);
        JButton searchButton = new JButton("search");
        JButton clearButton = new JButton("clear");
        searchPanel.add(searchButton);
        searchPanel.add(clearButton);
        panel.add(searchPanel, BorderLayout.NORTH);

        String[] columns = { "ID", "Title", "Author", "Genre", "TotalCopies", "LoanedCopies" };
        bookTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        bookTable = new JTable(bookTableModel);
        panel.add(new JScrollPane(bookTable), BorderLayout.CENTER);

        searchButton.addActionListener(e -> loadBookData());
        clearButton.addActionListener(e -> {
            bookTitleSearch.setText("");
            bookAuthorSearch.setText("");
            bookGenreSearch.setSelectedIndex(0);
            loadBookData();
        });

        bookTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = bookTable.getSelectedRow();
                    if (row < 0) {
                        return;
                    }
                    String id = (String) bookTableModel.getValueAt(row, 0);
                    String title = (String) bookTableModel.getValueAt(row, 1);
                    bookIdField.setText(id);
                    titleField.setText(title);
                }
            }
        });

        return panel;
    }

    private JPanel buildMemberReferencePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("name:"));
        memberNameSearch = new JTextField(15);
        searchPanel.add(memberNameSearch);
        JButton searchButton = new JButton("search");
        JButton clearButton = new JButton("clear");
        searchPanel.add(searchButton);
        searchPanel.add(clearButton);
        panel.add(searchPanel, BorderLayout.NORTH);

        String[] columns = { "ID", "Name" };
        memberTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        memberTable = new JTable(memberTableModel);
        panel.add(new JScrollPane(memberTable), BorderLayout.CENTER);

        searchButton.addActionListener(e -> loadMemberData());
        clearButton.addActionListener(e -> {
            memberNameSearch.setText("");
            loadMemberData();
        });

        memberTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = memberTable.getSelectedRow();
                    if (row < 0) {
                        return;
                    }
                    String id = (String) memberTableModel.getValueAt(row, 0);
                    String name = (String) memberTableModel.getValueAt(row, 1);
                    memberIdField.setText(id);
                    nameField.setText(name);
                }
            }
        });

        return panel;
    }

    private void loadBookData() {
        bookTableModel.setRowCount(0);
        String title = bookTitleSearch.getText().trim();
        String author = bookAuthorSearch.getText().trim();
        Genre genre = (Genre) bookGenreSearch.getSelectedItem();
        List<String[]> rows = service.searchBookRows(title, author, genre);
        for (String[] r : rows) {
            bookTableModel.addRow(r);
        }
    }

    private void loadMemberData() {
        memberTableModel.setRowCount(0);
        String name = memberNameSearch.getText().trim();
        List<String[]> rows = service.searchMemberRows(name);
        for (String[] r : rows) {
            memberTableModel.addRow(r);
        }
    }

    private void onOk() {
        titleError.setText(" ");
        bookIdError.setText(" ");
        nameError.setText(" ");
        memberIdError.setText(" ");

        String title = titleField.getText().trim();
        String bookId = bookIdField.getText().trim();
        String name = nameField.getText().trim();
        String memberId = memberIdField.getText().trim();

        boolean hasError = false;
        if (title.isEmpty()) {
            titleError.setText("Book Title is required");
            hasError = true;
        }
        if (bookId.isEmpty()) {
            bookIdError.setText("BookId is required");
            hasError = true;
        }
        if (name.isEmpty()) {
            nameError.setText("Member Name is required");
            hasError = true;
        }
        if (memberId.isEmpty()) {
            memberIdError.setText("MemberId is required");
            hasError = true;
        }
        if (!hasError) {
            Book book = null;
            try {
                int id = IdFormatter.parseBookId(bookId);
                book = service.findBookById(id);
            } catch (RuntimeException ex) {
                bookIdError.setText("Invalid BookId format");
                hasError = true;
            }
            if (!hasError) {
                if (book == null) {
                    bookIdError.setText("BookId not found");
                    hasError = true;
                } else if (!book.getTitle().equals(title)) {
                    bookIdError.setText("BookId does not match");
                    hasError = true;
                }
            }
        }
        if (!hasError) {
            Member member = null;
            try {
                int id = IdFormatter.parseMemberId(memberId);
                member = service.findMemberById(id);
            } catch (RuntimeException ex) {
                memberIdError.setText("Invalid MemberId format");
                hasError = true;
            }
            if (!hasError) {
                if (member == null) {
                    memberIdError.setText("MemberId not found");
                    hasError = true;
                } else if (!member.getName().equals(name)) {
                    memberIdError.setText("MemberId does not match");
                    hasError = true;
                }
            }
        }

        if (hasError) {
            return;
        }

        int bid = IdFormatter.parseBookId(bookId);
        int mid = IdFormatter.parseMemberId(memberId);
        try {
            service.lendBook(bid, mid);
        } catch (RuntimeException ex) {
            bookIdError.setText(ex.getMessage());
            return;
        }
        saved = true;
        dispose();
    }

    public boolean isSaved() {
        return saved;
    }
}
