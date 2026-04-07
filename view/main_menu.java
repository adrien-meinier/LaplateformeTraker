package view;

import view.btn.BtnMenu;
import javax.swing.*;
import java.awt.*;

public class main_menu extends AbstractView {

    public main_menu() {
        super("La Plateforme Tracker — Menu");
        initComponents();
    }

    @Override
    protected void initComponents() {
        setSize(800, 600);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 35, 20, 35));
        panel.setBackground(Color.WHITE);

        JLabel title = new JLabel("Menu principal");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setAlignmentX(CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Que souhaitez-vous faire ?");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 12));
        subtitle.setForeground(Color.GRAY);
        subtitle.setAlignmentX(CENTER_ALIGNMENT);

        String[][] boutons = {
            {"👨‍🎓  Etudiants",  "blue"},
            {"📚  Formations",  "blue"},
            {"📊  Statistiques", "blue"},
            {"⚙️  Paramètres", "gray"}
        };

        panel.add(title);
        panel.add(Box.createRigidArea(new Dimension(0, 4)));
        panel.add(subtitle);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        for (String[] b : boutons) {
            BtnMenu btn = new BtnMenu(b[0]);
            btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
            btn.setAlignmentX(CENTER_ALIGNMENT);
            panel.add(btn);
            panel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        panel.add(new JSeparator());
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        BtnMenu btnDeconnexion = new BtnMenu("🚪  Se déconnecter") {
            { setBackground(new Color(231, 76, 60)); }
        };
        btnDeconnexion.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btnDeconnexion.setAlignmentX(CENTER_ALIGNMENT);
        btnDeconnexion.addActionListener(e -> {
            new LoginView().setVisible(true);
            dispose();
        });
        panel.add(btnDeconnexion);

        add(panel);
        pack();
    }
}