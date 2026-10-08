package library.ui.panel;

import java.awt.BorderLayout;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import library.model.*;
import library.service.LibraryService;
import library.ui.dialog.LoanEditDialog;
import library.util.IdFormatter;

public class LoanPanel extends JPanel {
    private LibraryService service;
    private DefaultTableModel tableModel;
    private JTable table;

    private JTextField titleSearchField;
    private JTextField nameSearchField;
    private JToggleButton activeOnlyToggle;

    public LoanPanel(LibraryService service) {
        this.service = service;
        setLayout(new BorderLayout());

        String[] columns = { "ID", "Book", "Member", "LoanedAt", "ReturnedAt" };
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
        JLabel nameSearch = new JLabel("nameSearch");
        nameSearchField = new JTextField(15);
        activeOnlyToggle = new JToggleButton("Active only");
        JButton searchButton = new JButton("search");
        JButton clearButton = new JButton("clear");
        searchPanel.add(titleSearch);
        searchPanel.add(titleSearchField);
        searchPanel.add(nameSearch);
        searchPanel.add(nameSearchField);
        searchPanel.add(activeOnlyToggle);
        searchPanel.add(searchButton);
        searchPanel.add(clearButton);
        add(searchPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        JButton loanButton = new JButton("Loan");
        JButton returnButton = new JButton("Return");
        buttonPanel.add(loanButton);
        loanButton.setPreferredSize(new java.awt.Dimension(80, 30));
        buttonPanel.add(returnButton);
        returnButton.setPreferredSize(new java.awt.Dimension(80, 30));
        add(buttonPanel, BorderLayout.SOUTH);

        activeOnlyToggle.addActionListener(e -> {
            loadData();
        });

        searchButton.addActionListener(e -> {
            loadData();
        });

        clearButton.addActionListener(e -> {
            titleSearchField.setText("");
            nameSearchField.setText("");
            activeOnlyToggle.setSelected(false);
            loadData();
        });

        loanButton.addActionListener(e -> {
            LoanEditDialog dialog = new LoanEditDialog((java.awt.Frame) SwingUtilities.getWindowAncestor(this),
                    service);
            dialog.setVisible(true);
            if (dialog.isSaved()) {
                loadData();
            }
        });

        returnButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(this, "Please select a row");
                return;
            }

            int loanId = IdFormatter.parseLoanId((String) table.getValueAt(selectedRow, 0));

            Loan loan = service.findLoanById(loanId);
            Book book = service.findBookById(loan.getBookId());
            Genre genre = service.findGenreById(book.getGenreId());
            Member member = service.findMemberById(loan.getMemberId());

            String bookDisplay = book.getTitle() + " (" + IdFormatter.formatBookId(book.getId(), genre.getGenrePrefix())
                    + ")";
            String memberDisplay = member.getName() + " (" + IdFormatter.formatMemberId(member.getId()) + ")";
            String loanedAtStr = loan.getLoanedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String loanIdDisplay = IdFormatter.formatLoanId(loanId);

            String message = "Return this loan?\n\n"
                    + "Loan ID: " + loanIdDisplay + "\n"
                    + "Book: " + bookDisplay + "\n"
                    + "Member: " + memberDisplay + "\n"
                    + "Loaned at: " + loanedAtStr;

            int result = JOptionPane.showConfirmDialog(this, message, "Confirm", JOptionPane.YES_NO_OPTION);
            if (result != JOptionPane.YES_OPTION) {
                return;
            }

            try {
                service.returnBook(loanId);
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
        String name = nameSearchField.getText().trim();
        boolean activeOnly = activeOnlyToggle.isSelected();
        List<String[]> rows = service.searchLoanRows(title, name, activeOnly);
        for (String[] r : rows) {
            tableModel.addRow(r);
        }
    }
}
