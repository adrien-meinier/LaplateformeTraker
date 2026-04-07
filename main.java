import view. main_menu;



public class Main {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            //new LoginView().setVisible(true);
            new main_menu().setVisible(true);
        });
    
    }
}