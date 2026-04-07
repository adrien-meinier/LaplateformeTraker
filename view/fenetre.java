package view;

package view;

import controller.LoginController;

public class Fenetre {

    public static void main(String[] args) {

        java.awt.EventQueue.invokeLater(() -> {

            LoginController controller = new LoginController();
            LoginView view = new LoginView(controller);

            view.setVisible(true);
        });

    }
}
