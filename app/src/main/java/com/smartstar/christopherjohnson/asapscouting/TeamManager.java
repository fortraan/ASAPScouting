package com.smartstar.christopherjohnson.asapscouting;

import android.content.Context;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.util.Collections;
import java.util.Map;

/**
 * Created by christopher.johnson on 11/24/16.
 *
 * The TeamManager provides an easy way to manage the teams in the database.
 */

public class TeamManager {
    DatabaseReference teamsRoot;

    private ProgressBar submitProgressBar;

    private Context appContext;

    Map<Integer, Team> teams;

    DatabaseUpdater firebaseUpdater;

    /**
     * Constructs a new TeamManager object.
     *
     * @param appC application context
     * @param teamsR DatabaseReference referencing the team database
     * @param submitProgress progress bar that displays submission progress
     */
    public TeamManager(Context appC, DatabaseReference teamsR, ProgressBar submitProgress) {
        this.appContext = appC;
        this.teamsRoot = teamsR;
        this.submitProgressBar = submitProgress;

        teamsRoot.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                // all we want is the data, so we don't do anything to it
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    int teamNumber = (int) child.child("number").getValue();
                    String teamName = (String) child.child("name").getValue();
                    DataSnapshot abilities = child.child("abilities");
                    Map<String, Boolean> abilityMap = Collections.emptyMap();
                    for (DataSnapshot ability : abilities.getChildren()) {
                        abilityMap.put(ability.getKey(), (Boolean) ability.getValue());
                    }
                    TeamAbilities teamAbilities = new TeamAbilities(abilityMap);
                    Team newTeam = new Team(teamNumber, teamName, teamAbilities);
                    teams.put(teamNumber, newTeam);
                }
            }
        });

        firebaseUpdater = new DatabaseUpdater(this.teamsRoot.getDatabase(), this.submitProgressBar);
    }

    /**
     * Add a team to the manager.
     * Please only use it when you KNOW the team has
     * not yet been registered.
     * @param team team to be registered
     */
    public void addTeam(Team team) {
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
    // TODO: 11/24/16 add progress bar feedback
    public void updateTeam(Team newTeamData) {
        firebaseUpdater.commitTeamData(newTeamData);

        if (!teams.containsKey(newTeamData.teamNumber)){
            submitProgressBar.setProgress(13);
            this.addTeam(newTeamData);
        } else {
            submitProgressBar.setProgress(13);
            teams.remove(newTeamData.teamNumber);
            teams.put(newTeamData.teamNumber, newTeamData);
            submitProgressBar.setProgress(15);
        }
    }

    /**
     * Remove a team.
     * @param teamNumber the number of the team to be removed
     */
    public void removeTeam(Integer teamNumber) {
        if (!teams.containsKey(teamNumber)) {
            teams.remove(teamNumber);
        } else {
            Toast.makeText(appContext, "This team doesn't exist\n or has already been removed.", Toast.LENGTH_LONG).show();
        }
    }
}
