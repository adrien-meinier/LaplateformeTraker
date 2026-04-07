package view;

import javax.swing.*;
import java.awt.*;

public class RegisterView extends AbstractView {

    private JTextField txtPrenom, txtNom, txtEmail;
    private JPasswordField txtPassword, txtConfirm;
    private JButton btnRegister, btnGoLogin;

    public RegisterView() {
        super("La Plateforme Tracker — Inscription");
        initComponents();
    }

    @Override
    protected void initComponents() {
        setSize(800, 600);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 40, 25, 40));
        panel.setBackground(Color.WHITE);

        JLabel title = new JLabel("Inscription");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setAlignmentX(CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Créez votre compte");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 12));
        subtitle.setForeground(Color.GRAY);
        subtitle.setAlignmentX(CENTER_ALIGNMENT);

        // Ligne Prénom + Nom côte à côte
        txtPrenom = new JTextField();
        txtNom    = new JTextField();
        JPanel rowName = new JPanel(new GridLayout(1, 2, 10, 0));
        rowName.setBackground(Color.WHITE);
        rowName.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        rowName.add(txtPrenom);
        rowName.add(txtNom);

        txtEmail    = new JTextField();
        txtPassword = new JPasswordField();
        txtConfirm  = new JPasswordField();

        for (JTextField f : new JTextField[]{txtEmail, txtPassword, txtConfirm}) {
            f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
            f.setFont(new Font("Arial", Font.PLAIN, 13));
        }

        btnRegister = new view.btn.BtnMenu("Créer le compte");
        btnRegister.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btnRegister.addActionListener(e -> {
            // TODO : brancher RegisterController
            System.out.println("Register: " + txtEmail.getText());
        });

        btnGoLogin = new JButton("Déjà un compte ? Se connecter");
        btnGoLogin.setBorderPainted(false);
        btnGoLogin.setContentAreaFilled(false);
        btnGoLogin.setForeground(new Color(52, 152, 219));
        btnGoLogin.setFont(new Font("Arial", Font.PLAIN, 11));
        btnGoLogin.setAlignmentX(CENTER_ALIGNMENT);
        btnGoLogin.addActionListener(e -> {
            new LoginView().setVisible(true);
            dispose();
        });

        panel.add(title);
        panel.add(Box.createRigidArea(new Dimension(0, 4)));
        panel.add(subtitle);
        panel.add(Box.createRigidArea(new Dimension(0, 18)));

        JPanel rowLabels = new JPanel(new GridLayout(1, 2, 10, 0));
        rowLabels.setBackground(Color.WHITE);
        rowLabels.setMaximumSize(new Dimension(Integer.MAX_VALUE, 16));
        rowLabels.add(makeLabel("Prénom"));
        rowLabels.add(makeLabel("Nom"));
        panel.add(rowLabels);
        panel.add(rowName);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(makeLabel("Email"));
        panel.add(txtEmail);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(makeLabel("Mot de passe"));
        panel.add(txtPassword);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(makeLabel("Confirmer mot de passe"));
        panel.add(txtConfirm);
        panel.add(Box.createRigidArea(new Dimension(0, 16)));
        panel.add(btnRegister);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(btnGoLogin);

        add(panel);
        pack();
    }

    private JLabel makeLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Arial", Font.PLAIN, 11));
        l.setForeground(Color.GRAY);
        return l;
    }
}