package view;


import controller.LoginController;

public class TestAllViews {

    public static void main(String[] args) {

        java.awt.EventQueue.invokeLater(() -> {

            LoginController controller = new LoginController();

            //  LOGIN 
            LoginView login = new LoginView(controller);
            login.setLocation(100, 100);
            login.setVisible(true);

            //  REGISTER 
            RegisterView register = new RegisterView(controller);
            register.setLocation(550, 100);
            register.setVisible(true);

            //  MAIN MENU 
            MainMenuView menu = new MainMenuView();
            menu.setLocation(1000, 100);
            menu.setVisible(true);

        });

    }
}