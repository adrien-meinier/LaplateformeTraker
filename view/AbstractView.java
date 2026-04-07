
package view;

import javax.swing.JFrame;

public abstract class AbstractView extends JFrame {

    public AbstractView(String title) {
        setTitle(title);
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // centre la fenêtre
        setResizable(false);
    }

    // méthode que chaque vue doit implémenter
    protected abstract void initComponents();
}