
package view;

import javax.swing.JFrame;

public abstract class AbstractView extends JFrame {
    public AbstractView(String title) {
        setTitle(title);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    protected abstract void initComponents();

}