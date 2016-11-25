package com.smartstar.christopherjohnson.asapscouting;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by christopher.johnson on 11/24/16.
 *
 * The TeamManager provides an easy way to manage the teams in the database.
 */

public class TeamManager {
    DatabaseReference teamsRoot;
    ChildEventListener teamDataListener;

    private ProgressBar submitProgressBar;

    private Context appContext;

    Map<Integer, Team> teams;

    /**
     * Constructs a new TeamManager object.
     *
     * @param appC application context
     * @param teamsR DatabaseReference referencing the team database
     * @param teamDL Data update listener
     * @param submitProgress progress bar that displays submission progress
     */
    public TeamManager(Context appC, DatabaseReference teamsR, ChildEventListener teamDL, ProgressBar submitProgress) {
        this.appContext = appC;
        this.teamsRoot = teamsR;
        this.teamDataListener = teamDL;
        this.submitProgressBar = submitProgress;
    }

    private void addTeam(Team team) {
        if (teams.containsKey(team.teamNumber)) {
            Toast.makeText(appContext, "This team has already been registered!", Toast.LENGTH_SHORT).show();
            return;
        }
        teams.put(team.teamNumber, team);
    }

    /**
     * Update a team's data. If the team hasn't been registered, add them to the database.
     * @param newTeamData Team object for the team to be updated/registered
     */
    public void updateTeam(Team newTeamData) {
        if (!teams.containsKey(newTeamData.teamNumber)){
            this.addTeam(newTeamData);
        } else {
            teams.remove(newTeamData.teamNumber);
            teams.put(newTeamData.teamNumber, newTeamData);
        }
    }
}
