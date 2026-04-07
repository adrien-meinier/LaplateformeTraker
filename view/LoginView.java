package view;

//import view.btn.animation;
//import controller.LoginController;
import javax.swing.*;
import java.awt.*;

public class LoginView extends AbstractView {

    //private LoginController controller;
    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnGoRegister;

    public LoginView() {
        super("La Plateforme Tracker — Connexion");
        // controller = new LoginController();
        initComponents();
    }

    @Override
    protected void initComponents() {
        setSize(800, 600);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 40, 25, 40));
        panel.setBackground(Color.WHITE);

        JLabel title = new JLabel("Connexion");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setAlignmentX(CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Entrez vos identifiants");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 12));
        subtitle.setForeground(Color.GRAY);
        subtitle.setAlignmentX(CENTER_ALIGNMENT);

        txtEmail    = createField("Email", false);
        txtPassword = (JPasswordField) createField("Mot de passe", true);

        btnLogin = new view.btn.BtnMenu("Se connecter");
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btnLogin.addActionListener(e -> {
            //String email = txtEmail.getText();
            //String password = new String(txtPassword.getPassword());
            // controller.login(email, password);
        });

        btnGoRegister = new JButton("Pas encore de compte ? S'inscrire");
        btnGoRegister.setBorderPainted(false);
        btnGoRegister.setContentAreaFilled(false);
        btnGoRegister.setForeground(new Color(52, 152, 219));
        btnGoRegister.setFont(new Font("Arial", Font.PLAIN, 11));
        btnGoRegister.setAlignmentX(CENTER_ALIGNMENT);
        btnGoRegister.addActionListener(e -> {
            new RegisterView().setVisible(true);
            dispose();
        });

        panel.add(title);
        panel.add(Box.createRigidArea(new Dimension(0, 4)));
        panel.add(subtitle);
        panel.add(Box.createRigidArea(new Dimension(0, 18)));
        panel.add(makeLabel("Email"));
        panel.add(txtEmail);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(makeLabel("Mot de passe"));
        panel.add(txtPassword);
        panel.add(Box.createRigidArea(new Dimension(0, 16)));
        panel.add(btnLogin);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(btnGoRegister);

        add(panel);
        pack();
    }

    private JTextField createField(String placeholder, boolean password) {
        JTextField field = password ? new JPasswordField() : new JTextField();
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        field.setFont(new Font("Arial", Font.PLAIN, 13));
        return field;
    }

    private JLabel makeLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Arial", Font.PLAIN, 11));
        l.setForeground(Color.GRAY);
        return l;
    }
}