package view;

import controller.LoginController;

public class LoginView extends fenetre {

    private LoginController controller;

    private javax.swing.JTextField txtEmail;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JButton btnLogin;

    public LoginView() {
        initComponents();
        controller = new LoginController();
    }

    private void initComponents() {

        txtEmail = new javax.swing.JTextField();
        txtPassword = new javax.swing.JPasswordField();
        btnLogin = new javax.swing.JButton("Se connecter");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Login");

        btnLogin.addActionListener(evt -> btnLoginActionPerformed(evt));

        javax.swing.JPanel panel = new javax.swing.JPanel();
        panel.add(txtEmail);
        panel.add(txtPassword);
        panel.add(btnLogin);

        add(panel);
        pack();
    }

    private void btnLoginActionPerformed(java.awt.event.ActionEvent evt) {
        String email = txtEmail.getText();
        String password = new String(txtPassword.getPassword());

        controller.login(email, password);
    }
}