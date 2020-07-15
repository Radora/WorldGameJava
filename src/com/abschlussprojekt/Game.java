package com.abschlussprojekt;

import javax.swing.*;
import java.io.File;

public class Game {

    public GameMap map;

    public Game(File file, JPanel rootPanel){
        map = new GameMap(file, rootPanel);



        /**if(file.getName().equals("world.map")) {
            map.lookUpTerritory("Greenland").setConqueredBy(1);
            map.lookUpTerritory("Brazil").setConqueredBy(2);
            map.lookUpTerritory("Brazil").setTroops(7);
        }*/

    }
}