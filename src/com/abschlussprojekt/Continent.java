package com.abschlussprojekt;

import java.util.HashSet;

public class Continent {

    private String name;
    private HashSet<Territory> members;
    private final int BONUS;

    public Continent(String name, HashSet<Territory> members, int bonus){
        this.name = name;
        this.members = members;
        this.BONUS = bonus;
    }

    public String getName(){
        return name;
    }

    public HashSet<Territory> getTerritories() {
        return members;
    }

    public boolean isMember(String territory){
        return members.contains(territory);
    }

    public boolean continentConqueredBy(HashSet<String> territories){
        return territories.containsAll(members);
    }

}
