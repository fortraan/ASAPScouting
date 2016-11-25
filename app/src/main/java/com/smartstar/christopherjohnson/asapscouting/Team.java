package com.smartstar.christopherjohnson.asapscouting;

import android.support.annotation.NonNull;

/**
 * Created by christopher.johnson on 11/24/16.
 *
 * The standard Team object. Represents a team.
 */

public class Team {
    public TeamAbilities abilities;

    // These variables could be declared final, but
    // they aren't because there could be accidental typos
    // when entering data.
    // So to be on the safe side, things like these are
    // changeable.
    public int teamNumber;
    public String teamName;

    // // TODO: 11/24/16 Find out if an empty string (eg: "") is interpreted as null
    public Team(int number, @NonNull String name, TeamAbilities teamAbilities) {
        this.teamNumber = number;
        this.teamName = name;
        this.abilities = teamAbilities;
    }
}
