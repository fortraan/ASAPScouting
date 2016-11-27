package com.smartstar.christopherjohnson.asapscouting;

import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by christopher.johnson on 11/25/16.
 *
 * The MainActivity file was getting a little messy and unorganized,
 * so this class acts like a parser - {@link TeamManager team manager} data goes in,
 * parsed data readable by Firebase comes out.
 */

public class DatabaseUpdater {
    private DatabaseReference dbTeamsRoot;
    private HashMap<String, Object> teamDataHashMap = new HashMap<>();
    private ProgressBar pb;

    public DatabaseUpdater(FirebaseDatabase appDB, ProgressBar pB) {
        dbTeamsRoot = appDB.getReference("teams");
        pb = pB;
    }

    public void commitTeamData(final Team teamToParse) {
        pb.setProgress(0);
        teamDataHashMap.clear();
        teamDataHashMap.put("number", teamToParse.teamNumber);
        pb.setProgress(1);
        teamDataHashMap.put("name", teamToParse.teamName);
        pb.setProgress(2);
        final HashMap<String, Boolean> abilitiesHashMap = new HashMap<>();
        for (Map.Entry<String, Boolean> stringBooleanEntry : teamToParse.abilities.getAbilityMapperData().entrySet()) {
            abilitiesHashMap.put(stringBooleanEntry.getKey(), stringBooleanEntry.getValue());
        }
        pb.setProgress(5);
        // A HashMap in a HashMap. Not sure if terrible mistake or just a sketchy solution. (yet another meme reference)
        teamDataHashMap.put("abilities", abilitiesHashMap);
        pb.setProgress(7);

        // Pushing the data makes a new child under dbTeamsRoot, and then said child's data is set to
        // teamDataHashMap
        dbTeamsRoot.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                pb.setProgress(8);
                for (MutableData data : mutableData.getChildren()) {
                    if (data.child("number").getValue() == teamToParse.teamNumber) {
                        pb.setProgress(10);
                        // Their abilities will change, but name changes are rare
                        // and number changes are not allowed.
                        data.child("abilities").setValue(abilitiesHashMap);
                        return Transaction.success(mutableData);
                    }
                }
                pb.setProgress(12);
                dbTeamsRoot.push().setValue(teamDataHashMap);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

            }
        });
    }
}
