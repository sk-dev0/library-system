package library.ui;

import javax.swing.*;

import library.service.LibraryService;
import library.ui.panel.*;

public class MainFrame extends JFrame {
    public MainFrame(LibraryService service) {
        setTitle("Library System");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        BookPanel bookPanel = new BookPanel(service);
        MemberPanel memberPanel = new MemberPanel(service);
        GenrePanel genrePanel = new GenrePanel(service);
        LoanPanel loanPanel = new LoanPanel(service);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Books", bookPanel);
        tabs.addTab("Members", memberPanel);
        tabs.addTab("Genres", genrePanel);
        tabs.addTab("Loans", loanPanel);

        tabs.addChangeListener(e -> {
            int index = tabs.getSelectedIndex();
            switch (index) {
                case 0: bookPanel.loadData(); break;
                case 1: memberPanel.loadData(); break;
                case 2: genrePanel.loadData(); break;
                case 3: loanPanel.loadData(); break;
            }
        });

        add(tabs);
    }
}