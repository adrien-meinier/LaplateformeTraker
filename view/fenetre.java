package view;

import view.main_menu;

//creation de la fenetre de l'application
public class fenetre {
     public static void main(String[] args)
    {
        //creation de la fenetre dimmension 800*600
        java.awt.Dimension dimension = new java.awt.Dimension(800, 600);
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new main_menu().setVisible(true);
            }
        });
        java.awt.Frame body =new main_menu();
        body.setSize(dimension);
        body.setVisible (true);


    
    }
