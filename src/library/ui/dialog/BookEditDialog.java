package library.ui.dialog;

import java.awt.*;
import javax.swing.*;
import java.util.List;

import library.model.Book;
import library.model.Genre;
import library.service.LibraryService;

public class BookEditDialog extends JDialog {
    private LibraryService service;
    private Book target;
    private boolean saved = false;

    private JTextField titleField;
    private JTextField authorField;
    private JComboBox<Genre> genreField;
    private JTextField totalCopiesField;
    private JLabel titleError;
    private JLabel authorError;
    private JLabel genreError;
    private JLabel totalCopiesError;

    public BookEditDialog(java.awt.Frame owner, LibraryService service, Book target) {
        super(owner, true);
        this.service = service;
        this.target = target;

        setTitle(target == null ? "Add Book" : "Edit Book");
        setSize(600, 400);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(8, 2, 5, 5));

        formPanel.add(new JLabel("Title:"));
        titleField = new JTextField();
        formPanel.add(titleField);

        formPanel.add(new JLabel(""));
        titleError = new JLabel(" ");
        titleError.setForeground(Color.RED);
        formPanel.add(titleError);

        formPanel.add(new JLabel("Author:"));
        authorField = new JTextField();
        formPanel.add(authorField);

        formPanel.add(new JLabel(""));
        authorError = new JLabel(" ");
        authorError.setForeground(Color.RED);
        formPanel.add(authorError);

        formPanel.add(new JLabel("Genre:"));
        genreField = new JComboBox<>();
        for (Genre g : service.findAllGenre()) {
            genreField.addItem(g);
        }
        formPanel.add(genreField);

        formPanel.add(new JLabel(""));
        genreError = new JLabel(" ");
        genreError.setForeground(Color.RED);
        formPanel.add(genreError);

        formPanel.add(new JLabel("TotalCopies:"));
        totalCopiesField = new JTextField();
        formPanel.add(totalCopiesField);

        formPanel.add(new JLabel(""));
        totalCopiesError = new JLabel(" ");
        totalCopiesError.setForeground(Color.RED);
        formPanel.add(totalCopiesError);

        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);

        if (target != null) {
            Genre genre = service.findGenreById(target.getGenreId());
            titleField.setText(target.getTitle());
            authorField.setText(target.getAuthor());
            genreField.setSelectedItem(genre);
            totalCopiesField.setText(String.valueOf(target.getTotalCopies()));
        }

        okButton.addActionListener(e -> onOk());
        cancelButton.addActionListener(e -> dispose());
    }

    private void onOk() {
        titleError.setText(" ");
        authorError.setText(" ");
        genreError.setText(" ");
        totalCopiesError.setText(" ");

        String title = titleField.getText().trim();
        String author = authorField.getText().trim();
        Genre selectedGenre = (Genre) genreField.getSelectedItem();
        String totalCopies = totalCopiesField.getText().trim();

        boolean hasError = false;
        if (title.isEmpty()) {
            titleError.setText("Title is required");
            hasError = true;
        }
        if (author.isEmpty()) {
            authorError.setText("Author is required");
            hasError = true;
        }
        if (totalCopies.isEmpty()) {
            totalCopiesError.setText("TotalCopies is required");
            hasError = true;
        }
        if (hasError) {
            return;
        }

        if (target == null) {
            int id = service.nextBookId();
            service.addBook(new Book(id, title, author, selectedGenre.getId(), Integer.parseInt(totalCopies)));
        } else {
            target.setTitle(title);
            target.setAuthor(author);
            target.setGenreId(selectedGenre.getId());
            target.setTotalCopies(Integer.parseInt(totalCopies));
        }
        saved = true;
        dispose();
    }

    public boolean isSaved() {
        return saved;
    }
}
