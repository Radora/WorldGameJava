package com.abschlussprojekt;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class Setup {

    public static final Dimension SCREEN_SIZE = new Dimension(1250, 650);

    private JFrame frame;
    private JPanel rootPanel;
    private JLabel lbl_maps;
    private JComboBox cb_maps;
    private JMenu menu_file;
    private JMenuItem menu_file_newGame;
    private JMenuItem menu_file_exit;
    private Game game;

    public Setup() {
        initComponents();
    }

    private void initComponents() {


        frame = new JFrame("All Those Territories");
        frame.setLayout(new BorderLayout());

        rootPanel = new JPanel();
        rootPanel.setPreferredSize(SCREEN_SIZE);

        frame.add(rootPanel, BorderLayout.NORTH);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.pack();

        //Starts in the middle of the screen
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(dim.width / 2 - frame.getSize().width / 2, dim.height / 2 - frame.getSize().height / 2);

        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);

        menu_file = new JMenu("File");

        menuBar.add(menu_file);

        menu_file_newGame = new JMenuItem("New Game");
        menu_file_exit = new JMenuItem("Exit");

       menu_file_newGame.setEnabled(false);

        menu_file.add(menu_file_newGame);
        menu_file.add(menu_file_exit);

        menu_file_newGame.addActionListener(e -> {
            int response = JOptionPane.showConfirmDialog(null, "Restart the game?", "Are you sure?", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
            if (response == JOptionPane.YES_OPTION) {
                rootPanel.remove(game.map.g);
                frame.repaint();
                lbl_maps.setVisible(true);
                cb_maps.setVisible(true);
                menu_file_newGame.setEnabled(false);
            }
        });

        menu_file_exit.addActionListener(e ->  {
            System.exit(0);
        });

        lbl_maps = new JLabel("Choose a map: ");
        cb_maps = new JComboBox();

        rootPanel.add(lbl_maps);
        rootPanel.add(cb_maps);

        File myFolder = new File("res");

        try {
            File[] files = myFolder.listFiles();
            for(File file: files)
                cb_maps.addItem(file.getName());
        } catch(Exception err) {
            System.out.println("ERROR (listing map files) !");
        }

        cb_maps.setSelectedItem(null);

        cb_maps.addActionListener(e -> {
            String selectedItem = cb_maps.getSelectedItem().toString();
            try {
                File file = new File("res/" + selectedItem);
                setupGame(file, rootPanel);

            } catch (Exception err) {
                System.out.println("ERROR (loading map) !");
            }
        });

        frame.setVisible(true);
    }

    private void setupGame(File file, JPanel rootPanel) {
        lbl_maps.setVisible(false);
        cb_maps.setVisible(false);
        menu_file_newGame.setEnabled(true);
        game = new Game(file, rootPanel);
    }
}