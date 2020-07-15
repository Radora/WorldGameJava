package com.abschlussprojekt;

import java.awt.*;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

public class Territory {

    private String name;
    private Queue<Polygon> point;
    private Point capital;
    private HashSet<Territory> neighbours;
    private int troops;
    private int conqueredBy;

    public Territory(String name, Polygon patch){
        this.point = new LinkedList<>();
        this.neighbours = new HashSet<>();
        this.name = name;
        this.point.add(patch);
        this.troops = 0;
        this.conqueredBy = 0; // 0-none | 1-player one | 2-player two
    }

    public void setNeighbours(HashSet<Territory> neighbours){
        this.neighbours = neighbours;
    }

    public void setCapital(Point capital){
        this.capital = capital;
    }

    public void addPatch(Polygon patch){
        this.point.add(patch);
    }

    public String getName(){
        return name;
    }

    public Queue<Polygon> getPatches(){
        return point;
    }

    public Point getCapital(){
        return capital;
    }

    public boolean hasNeighbours(){
        return !(neighbours == null);
    }

    public HashSet<Territory> getNeighbours(){
        return neighbours;
    }

    public boolean isNeighbour(Territory terr){
        return hasNeighbours() && neighbours.contains(terr.getName());
    }

    public void addTroops () {
        this.troops++;
    }

    public int getTroops(){
        return troops;
    }

    public void setTroops (int troops) {
        this.troops = troops;
    }

    public void setConqueredBy (int playerNumber) {
        this.conqueredBy = playerNumber;
    }

    public int getConqueredBy () {
        return conqueredBy;
    }
}