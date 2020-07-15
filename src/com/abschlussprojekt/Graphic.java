package com.abschlussprojekt;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.Timer;

final class Colors {
    final static Color BACKGROUND_COLOR =  new Color(0, 150, 250);
    final static Color BORDER_COLOR = new Color(0, 0, 0);
    final static Color CONNECTION_COLOR = new Color(255, 255, 255);
    final static Color PLAYER0 = new Color(150, 150, 150);
    final static Color PLAYER1 = new Color(200,0,0);
    final static Color PLAYER2 = new Color(0,150,0);
}

public class Graphic extends JPanel {
    private GameMap map;
    private Set<Line2D> connections;
    private Territory highlightedTerritory;
    private String infoText = "";
    private int chooseTerritoryCount = 0;
    boolean boolAIChoice;
    boolean phaseReinforcement = true;
    boolean amountOfReinforcement = true;
    int reinforcments = 0;
    boolean attackedTerritory = false;
    Territory attacker;
    // New items
    BufferedImage d1 = null ,d2 = null ,d3 = null,d4 = null,d5 = null,d6 = null;
    public boolean showDiceBox = false;
    long timestamp = -1;
    public int playerOneTroops = 0;
    public int playerTwoTroops = 0;
    private Rectangle rollButton = new Rectangle(10,430,105,30);
    private Rectangle okButton = new Rectangle(10,398,105,30);
    private Rectangle diceBox = new Rectangle(10, 465, 230, 150);
    private Rectangle playerOneTroopsBox = new Rectangle(55, 508, 25, 20);
    private Rectangle playerTwoTroopsBox = new Rectangle(170,508,25,20);
    private Rectangle middleLane = new Rectangle(125,465,2,150);
    private Shape highlightedButton;


    public Graphic(GameMap map) {
        this.map = map;
        this.connections = map.getConnections();

        setBackground(Colors.BACKGROUND_COLOR);

        try {
            d1 = ImageIO.read(new File("dice/dice_1.png"));
            d2 = ImageIO.read(new File("dice/dice_2.png"));
            d3 = ImageIO.read(new File("dice/dice_3.png"));
            d4 = ImageIO.read(new File("dice/dice_4.png"));
            d5 = ImageIO.read(new File("dice/dice_5.png"));
            d6 = ImageIO.read(new File("dice/dice_6.png"));
        } catch (Exception e) {
            System.out.println("Error loading dice images!");
        }

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                repaint();
            }
        }, 0, 10);


        MouseAdapter ma = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                //Player's moves
                if (e.getButton() == 1) {
                    Territory terr = map.getContainingTerritory(e.getPoint());
                    if (terr != null) {
                        //Choosing territories in the beginning
                        if (terr.getConqueredBy() == 0 && chooseTerritoryCount < map.getAllTerritories().size() && chooseTerritoryCount % 2 == 0) {
                            playerChoosing(terr);
                            opponentChoosing();
                        }
                        else if(chooseTerritoryCount >= map.getAllTerritories().size()) {
                            //reinforcement and attack
                            playerReinforcement(terr);
                            playerAttack(terr);
                        }
                    }
                    if (rollButton.contains(e.getPoint())) {
                        if (showDiceBox)
                            showDiceBox = false;
                        else
                            showDiceBox = true;
                    }
                }

                //opponent's moves --> mouse second button
                else if (e.getButton() == 3 && chooseTerritoryCount >= map.getAllTerritories().size()) {
                    phaseReinforcement = true;
                    amountOfReinforcement = true;
                    opponentReinforcement();
                    opponentAttack();
                }
                else {
                    System.out.println("Error - Clicking");
                }
            }

            public void mouseMoved(MouseEvent e) {
                Territory terr = map.getContainingTerritory(e.getPoint());
                if(terr != null) {
                    highlightedTerritory = terr;
                    infoText = terr.getName();
                }else if (okButton.contains(e.getPoint())){
                    highlightedButton = okButton;
                }else if (rollButton.contains(e.getPoint())){
                    highlightedButton = rollButton;
                }
                else {
                    highlightedTerritory = null;
                    highlightedButton = null;
                    infoText = "";
                }
            }
        };
        addMouseListener(ma);
        addMouseMotionListener(ma);
    }

    public void playerChoosing (Territory terr) {
        terr.setConqueredBy(1);
        terr.setTroops(3);
        countTroops();
        chooseTerritoryCount++;
    }

    public void opponentChoosing () {
        if (chooseTerritoryCount < map.getAllTerritories().size() && chooseTerritoryCount % 2 == 1) {
            boolAIChoice = false;
            while (!boolAIChoice) {
                Random r1 = new Random();
                int aiChoice1 = r1.nextInt(1250);
                int aiChoice2 = r1.nextInt(650);
                Territory terr1 = map.getContainingTerritory(new Point(aiChoice1, aiChoice2));
                if (terr1 != null && terr1.getConqueredBy() == 0) {
                    terr1.setConqueredBy(2);
                    terr1.setTroops(3);
                    boolAIChoice = true;
                    chooseTerritoryCount++;
                }
            }
            countTroops();
        }
    }

    public void playerReinforcement(Territory terr) {
        if (phaseReinforcement && terr.getConqueredBy() == 1) {
            if (amountOfReinforcement) {
                for (Map.Entry<String, Territory> entry : map.getAllTerritories().entrySet()) {
                    Territory t = entry.getValue();
                    if (t.getConqueredBy() == 1) {
                        reinforcments++;
                    }
                }
                reinforcments = reinforcments / 3;
                amountOfReinforcement = false;
            }
            if (reinforcments > 0) {
                terr.addTroops();
                reinforcments--;
                System.out.println("Verbleibender Nachschub: " + reinforcments);
            } else {
                phaseReinforcement = false;
            }
        }
        countTroops();
    }

    public void playerAttack(Territory terr) {
        if (!attackedTerritory && terr.getConqueredBy() == 1) {
            if (terr.getTroops() - 1 > 0) {
                attacker = terr;
                attackedTerritory = true;
            }
        }
        else if (attackedTerritory) { //ToDo: Nachbarn koennen nur Nachbarn angreifen -->  else if (attackedTerritory && terr.isNeighbour(attacker)) { <-- ???
            //Eigenes Gebiet
            if (terr.getConqueredBy() == 1) {
                if(attacker.getTroops() > 1) {
                    attacker.setTroops(attacker.getTroops() - 1);
                    terr.addTroops();
                    attacker = null;
                    attackedTerritory = false;
                }
            }
            //Gegnerisches Gebiet
            else if (terr.getConqueredBy() == 2) {
                rollDices(attacker, terr);
                attacker = null;
                attackedTerritory = false;
            }
        }
    }

    public void opponentReinforcement () {
        for (Map.Entry<String, Territory> entry : map.getAllTerritories().entrySet()) {
            Territory value = entry.getValue();
            if (value.getConqueredBy() == 2) {
                reinforcments++;
            }
        }
        reinforcments = reinforcments / 3;

        while (reinforcments > 0) {
            Random r1a = new Random();
            int aiChoice1a = r1a.nextInt(1250);
            int aiChoice2a = r1a.nextInt(650);
            Territory terr2 = map.getContainingTerritory(new Point(aiChoice1a, aiChoice2a));
            if (terr2 != null && terr2.getConqueredBy() == 2) {
                terr2.addTroops();
                reinforcments--;
            }
        }
        reinforcments = 0;
        countTroops();
    }

    public void opponentAttack () {
        boolAIChoice = false;
        Territory attacker = null;
        Territory defender = null;
        while (!boolAIChoice) {
            Random r1 = new Random();
            int aiChoice1 = r1.nextInt(1250);
            int aiChoice2 = r1.nextInt(650);
            Territory terr1 = map.getContainingTerritory(new Point(aiChoice1, aiChoice2));
            if (terr1 != null && terr1.getConqueredBy() == 2) {
                attacker = terr1;
                boolAIChoice = true;
            }
        }
        while (boolAIChoice) {
            Random r1 = new Random();
            int aiChoice1 = r1.nextInt(1250);
            int aiChoice2 = r1.nextInt(650);
            Territory terr1 = map.getContainingTerritory(new Point(aiChoice1, aiChoice2));
            if (terr1 != null && terr1.getConqueredBy() == 1) {
                defender = terr1;
                boolAIChoice = false;
            }
        }
        rollDices(attacker, defender);
    }

    public void countTroops() {
        playerOneTroops = 0;
        playerTwoTroops = 0;
        for(Map.Entry<String, Territory> entry : map.getAllTerritories().entrySet()) {
            Territory t = entry.getValue();
            if (t.getConqueredBy() == 1)
                playerOneTroops += t.getTroops();
            else if (t.getConqueredBy() == 2)
                playerTwoTroops += t.getTroops();
        }
    }

    public void rollDices(Territory attacker, Territory defender) {
        Random r = new Random();


        while (attacker.getTroops() > 1 && defender.getTroops() > 0) {
            int r1 = r.nextInt(6) + 1;
            int r2 = r.nextInt(6) + 1;
            if (r1 > r2) {
                defender.setTroops(defender.getTroops() - 1);
            } else {
                attacker.setTroops(attacker.getTroops() - 1);
            }
        }
        if(defender.getTroops() == 0) {
            defender.setTroops(attacker.getTroops() - 1);
            attacker.setTroops(1);
            if(defender.getConqueredBy() == 2) {
                defender.setConqueredBy(1);
            }
            else {
                defender.setConqueredBy(2);
            }
        }
    }

    public void rollDices (Graphics2D g2) {
        if (showDiceBox) {
            Random r = new Random();
            int r1 = r.nextInt(6) + 1;
            int r2 = r.nextInt(6) + 1;
            BufferedImage dd1 = null;
            BufferedImage dd2 = null;
            switch (r1) {
                case 1: dd1 = d1;
                    break;
                case 2: dd1 = d2;
                    break;
                case 3: dd1 = d3;
                    break;
                case 4: dd1 = d4;
                    break;
                case 5: dd1 = d5;
                    break;
                case 6: dd1 = d6;
                    break;
                default:
                    break;
            }
            switch (r2) {
                case 1: dd2 = d1;
                    break;
                case 2: dd2 = d2;
                    break;
                case 3: dd2 = d3;
                    break;
                case 4: dd2 = d4;
                    break;
                case 5: dd2 = d5;
                    break;
                case 6: dd2 = d6;
                    break;
                default:
                    break;
            }
            g2.drawImage(dd1, 25, 560, 110, 610, 0, 0, d1.getWidth(), d1.getHeight(), null);
            g2.drawImage(dd2, 145, 560, 225, 610, 0, 0, d2.getWidth(), d2.getHeight(), null);
        }
    }

    public Dimension getPreferredSize() {
        return Setup.SCREEN_SIZE;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g2.setColor(Colors.CONNECTION_COLOR);
        g2.setStroke(new BasicStroke(3));
        connections.forEach(g2::draw);

        g2.setStroke(new BasicStroke(2));
        for(Map.Entry<String, Territory> entry : map.getAllTerritories().entrySet()){
            Territory t = entry.getValue();

            Color terrColor;
            if (t.getConqueredBy() == 1) {
                terrColor = Colors.PLAYER1;
            }
            else if (t.getConqueredBy() == 2) {
                terrColor = Colors.PLAYER2;
            }
            else {
                terrColor = Colors.PLAYER0;
            }
            if (t == highlightedTerritory) {
                terrColor = terrColor.brighter();
            }

            for(Polygon patch : t.getPatches()){
                g2.setColor(terrColor);
                g2.fill(patch);
                g2.setColor(Colors.BORDER_COLOR);
                g2.draw(patch);
                g2.setFont(new Font("default", Font.BOLD, 15));
                g2.setColor(new Color(255, 255, 255));
                g2.drawString(String.valueOf(t.getTroops()), t.getCapital().x, t.getCapital().y);
                g2.setFont(new Font("default", Font.BOLD, 16));
                g2.setColor(Color.green);
                g2.drawString(infoText, Setup.SCREEN_SIZE.width/2, 600);
            }
        }

        //  Buttons Roll & OK - setting Pos & Color

        if (highlightedButton == okButton){
            g2.setColor(new Color(158,158,158));
            g2.fill(okButton);
            g2.setColor(Color.BLACK);
            g2.fill(rollButton);

        }else if (highlightedButton == rollButton){
            g2.setColor(new Color(158,158,158));
            g2.fill(rollButton);
            g2.setColor(Color.BLACK);
            g2.fill(okButton);

        }else {
            g2.setColor(Color.BLACK);
            g2.fill(rollButton);
            g2.fill(okButton);
        }

        // The dice box
        g2.setColor(Color.WHITE);
        g2.fill(diceBox);

        // Strings
        g2.setFont(new Font("default", Font.BOLD, 20));
        g2.drawString("Roll!", 42, 453);
        g2.drawString("Ok", 44, 420);

        g2.setFont(new Font("default", Font.ITALIC, 17));
        g2.setColor(Color.BLACK);
        g2.drawString("Player 1", 37, 485);
        g2.drawString("Player 2", 152, 485);

        g2.setFont(new Font("default", Font.BOLD, 10));
        g2.drawString("Troops", 50, 505);
        g2.drawString("Troops", 165, 505);
        g2.drawString("Dice Result", 39, 547);
        g2.drawString("Dice Result",157,547);

        g2.fill(middleLane);
        g2.fill(playerOneTroopsBox);
        g2.fill(playerTwoTroopsBox);

        // Number of troops
        g2.setFont(new Font("default", Font.BOLD, 15));
        g2.setColor(Color.WHITE);
        g2.drawString(Integer.toString(playerOneTroops), 57, 523);
        g2.drawString(Integer.toString(playerTwoTroops),172,523);

        rollDices(g2);

        int conqueredAll = 0;
        for(Map.Entry<String, Territory> entry : map.getAllTerritories().entrySet()) {
            Territory t = entry.getValue();
            if (t.getConqueredBy() == 1)
                conqueredAll += 1;
            else if (t.getConqueredBy() == 2) {
                conqueredAll -= 1;
            }
        }
        if (conqueredAll == map.getAllTerritories().size())
            System.out.println("WIN");
        else if(conqueredAll * -1 == map.getAllTerritories().size())
            System.out.println("LOSE");
    }
}