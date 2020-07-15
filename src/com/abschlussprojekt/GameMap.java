package com.abschlussprojekt;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class GameMap {

    private Map<String, Territory> allTerritories = new HashMap<>();
    private Map<String, Continent> allContinents = new HashMap<>();

    public Graphic g;

    public GameMap(File file, JPanel rootPanel){
        readFile(file);

        g = new Graphic(this);
        rootPanel.add(g);
    }

    public void addTerritory(Territory terr){
        allTerritories.put(terr.getName(), terr);
    }

    public boolean territoryExists(String name){
        return !(allTerritories.get(name) == null);
    }

    public Territory lookUpTerritory(String name){
        return allTerritories.get(name);
    }

    public Territory getContainingTerritory(Point point){
        for(Map.Entry<String, Territory> entry : allTerritories.entrySet()) {
            Territory terr = entry.getValue();
            for(Polygon patch : terr.getPatches()) {
                if (patch.contains(point)) return terr;
            }
        }
        return null;
    }

    public Set<Line2D> getConnections(){
        Set<Line2D> connections = new HashSet<>();

        for(Map.Entry<String, Territory> entry : allTerritories.entrySet()) {
            Territory terr = entry.getValue();
                for (Territory neighbour : terr.getNeighbours()) {
                    if (terr.getName().equals("Alaska"))
                        connections.add(new Line2D.Float(terr.getCapital().x, terr.getCapital().y, 0, terr.getCapital().y));
                    else if(terr.getName().equals("Kamchatka"))
                        connections.add(new Line2D.Float(terr.getCapital().x, terr.getCapital().y, Setup.SCREEN_SIZE.width, terr.getCapital().y));
                    else connections.add(new Line2D.Float(terr.getCapital().x, terr.getCapital().y,
                            neighbour.getCapital().x, neighbour.getCapital().y));
                }
        }
        return connections;
    }
    public Map<String, Territory> getAllTerritories() { return allTerritories; }

    public void readFile(File file){
        String name;
        Polygon polygon;

        try {
            Scanner s = new Scanner(file);
            String firstWord = s.next();

            while (s.hasNext()){
                if(firstWord.equals("patch-of")){
                    name = nameHelper(s);
                    polygon  = new Polygon();

                    while(s.hasNextInt())
                        polygon.addPoint(s.nextInt(), s.nextInt());

                    if (this.territoryExists(name)){
                        lookUpTerritory(name).addPatch(polygon);
                    } else {
                        Territory terr = new Territory(name, polygon);
                        this.addTerritory(terr);
                    }
                    if(s.hasNext())
                        firstWord = s.next();
                }

                else if(firstWord.equals("capital-of")){
                    name = nameHelper(s);

                    int x = s.nextInt();
                    int y = s.nextInt();

                    this.lookUpTerritory(name).setCapital(new Point(x, y));
                    if(s.hasNext())
                        firstWord = s.next();
                }

                else if(firstWord.equals("neighbors-of") && s.hasNext()) {
                    name = nameHelper(s, ":");
                    String[] neighbours = s.nextLine().split(" - ");
                    HashSet<Territory> neighboursSet = new HashSet<>();

                    for(String tmp: neighbours) {
                        tmp = tmp.trim();
                        if(lookUpTerritory(tmp) != null)
                            neighboursSet.add(lookUpTerritory(tmp));
                    }
                    lookUpTerritory(name).setNeighbours(neighboursSet);

                    if(s.hasNext())
                        firstWord = s.next();
                }

                else if(firstWord.equals("continent") && s.hasNext()) {
                    name = nameHelper(s);
                    int bonus = s.nextInt();
                    s.next();
                    String[] continentMembers = s.nextLine().split(" - ");
                    HashSet<Territory> membersSet = new HashSet<>();

                    for(String tmp: continentMembers) {
                        tmp = tmp.trim();
                        if(lookUpTerritory(tmp) != null)
                            membersSet.add(lookUpTerritory(tmp));
                    }
                    Continent continent = new Continent(name, membersSet, bonus);
                    allContinents.put(name, continent);

                    if(s.hasNext())
                        firstWord = s.next();
                }
            }
        }
        catch (FileNotFoundException e) {
            System.out.println("ERROR (3)");
        }
    }

    public static String nameHelper(Scanner s, String stopper){
        String name = s.next();
        String next = s.next();
        boolean nameFinished = false;
        while(!nameFinished) {
            if(next.equals(stopper))
                nameFinished = true;
            else {
                name += " " +next;
                next = s.next();
            }
        }
        return name;
    }

    public static String nameHelper(Scanner s){
        String name = s.next();
        boolean nameFinished = false;
        while(!nameFinished) {
            if(s.hasNextInt())
                nameFinished = true;
            else
                name += " " + s.next();
        }
        return name;
    }
}