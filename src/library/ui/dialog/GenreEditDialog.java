package library.ui.dialog;

import java.awt.*;
import javax.swing.*;

import library.model.Genre;
import library.service.LibraryService;

public class GenreEditDialog extends JDialog {
    private LibraryService service;
    private Genre target;
    private boolean saved = false;

    private JTextField nameField;
    private JTextField prefixField;
    private JLabel nameError;
    private JLabel prefixError;

    public GenreEditDialog(java.awt.Frame owner, LibraryService service, Genre target) {
        super(owner, true);
        this.service = service;
        this.target = target;

        setTitle(target == null ? "Add Genre" : "Edit Genre");
        setSize(400, 250);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 5, 5));

        formPanel.add(new JLabel("Name:"));
        nameField = new JTextField();
        formPanel.add(nameField);

        formPanel.add(new JLabel(""));
        nameError = new JLabel(" ");
        nameError.setForeground(Color.RED);
        formPanel.add(nameError);

        formPanel.add(new JLabel("Prefix (2 uppercase letters):"));
        prefixField = new JTextField();
        formPanel.add(prefixField);

        formPanel.add(new JLabel(""));
        prefixError = new JLabel(" ");
        prefixError.setForeground(Color.RED);
        formPanel.add(prefixError);

        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);

        if (target != null) {
            nameField.setText(target.getGenreName());
            prefixField.setText(target.getGenrePrefix());
        }

        okButton.addActionListener(e -> onOk());
        cancelButton.addActionListener(e -> dispose());
    }

    private void onOk() {
        nameError.setText(" ");
        prefixError.setText(" ");

        String name = nameField.getText().trim();
        String prefix = prefixField.getText().trim();

        boolean hasError = false;
        if (name.isEmpty()) {
            nameError.setText("Name is required");
            hasError = true;
        }
        if (!prefix.matches("[A-Z]{2}")) {
            prefixError.setText("Must be 2 uppercase letters");
            hasError = true;
        }
        if (!hasError) {
            Genre existing = service.findGenreByPrefix(prefix);
            if (existing != null && (target == null || existing.getId() != target.getId())) {
                prefixError.setText("Prefix already used");
                hasError = true;
            }
        }
        if (!hasError) {
            Genre existing = service.findGenreByExactName(name);
            if (existing != null && (target == null || existing.getId() != target.getId())) {
                nameError.setText("Name already used");
                hasError = true;
            }
        }
        if (hasError) {
            return;
        }

        if (target == null) {
            int id = service.nextGenreId();
            service.addGenre(new Genre(id, name, prefix));
        } else {
            target.setGenreName(name);
            target.setGenrePrefix(prefix);
            service.updateGenre(target);
        }
        saved = true;
        dispose();
    }

    public boolean isSaved() {
        return saved;
    }
}
