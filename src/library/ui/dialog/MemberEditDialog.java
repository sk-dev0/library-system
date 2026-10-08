package library.ui.dialog;

import java.awt.*;
import javax.swing.*;

import library.model.Member;
import library.service.LibraryService;

public class MemberEditDialog extends JDialog {
    private LibraryService service;
    private Member target;
    private boolean saved = false;

    private JTextField nameField;
    private JLabel nameError;

    public MemberEditDialog(java.awt.Frame owner, LibraryService service, Member target) {
        super(owner, true);
        this.service = service;
        this.target = target;

        setTitle(target == null ? "Add Member" : "Edit Member");
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

        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);

        if (target != null) {
            nameField.setText(target.getName());
        }

        okButton.addActionListener(e -> onOk());
        cancelButton.addActionListener(e -> dispose());
    }
    
    private void onOk() {
        nameError.setText(" ");

        String name = nameField.getText().trim();

        boolean hasError = false;
        if (name.isEmpty()) {
            nameError.setText("Name is required");
            hasError = true;
        }
        if (hasError) {
            return;
        }

        if (target == null) {
            int id = service.nextMemberId();
            service.addMember(new Member(id, name));
        } else {
            target.setName(name);
            service.updateMember(target);
        }
        saved = true;
        dispose();
    }

    public boolean isSaved() {
        return saved;
    }
}
