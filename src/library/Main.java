package library;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import library.repository.BookRepository;
import library.repository.GenreRepository;
import library.repository.LoanRepository;
import library.repository.MemberRepository;
import library.service.LibraryService;
import library.ui.MainFrame;

public class Main {
    public static void main(String[] args) throws IOException {
        Locale.setDefault(Locale.ENGLISH);
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
        }

        Path dataDir = Path.of("data");
        if (!Files.exists(dataDir)) {
            Files.createDirectories(dataDir);
        }

        BookRepository bookRepo = new BookRepository("data/books.tsv");
        MemberRepository memberRepo = new MemberRepository("data/members.tsv");
        LoanRepository loanRepo = new LoanRepository("data/loans.tsv");
        GenreRepository genreRepo = new GenreRepository("data/genres.tsv");

        if (Files.exists(Path.of("data/genres.tsv"))) {
            genreRepo.loadFromFile();
        }
        if (Files.exists(Path.of("data/books.tsv"))) {
            bookRepo.loadFromFile();
        }
        if (Files.exists(Path.of("data/members.tsv"))) {
            memberRepo.loadFromFile();
        }
        if (Files.exists(Path.of("data/loans.tsv"))) {
            loanRepo.loadFromFile();
        }

        LibraryService service = new LibraryService(bookRepo, memberRepo, loanRepo, genreRepo);

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame(service);

            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    try {
                        service.saveAll();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            });

            frame.setVisible(true);
        });
    }
}