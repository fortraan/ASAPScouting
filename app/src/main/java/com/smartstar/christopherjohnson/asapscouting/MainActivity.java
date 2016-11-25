package com.smartstar.christopherjohnson.asapscouting;

import android.content.Context;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Scouting
 *
 * This app uses Firebase to have an offline database for scouting.
 */
public class MainActivity extends AppCompatActivity {
    // Firebase object
    // TODO: 11/24/16 Connect Firebase to TeamManager
    FirebaseDatabase teamDatabase;
    DatabaseReference teamsRef;

    TeamManager manager;

    // Buttons
    Button viewData;
    Button addOrUpdateData;
    Button findTeam;
    Button submitTeam;

    EditText teamNameEdit;
    EditText teamNumberEdit;

    LinearLayout abilitiesLayout;

    ProgressBar submitProgressBar;

    CheckBox abilitiesChecks[];

    Context context;

    // Click listener for viewData
    Button.OnClickListener viewDataListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            teamsRef = teamDatabase.getReference("teams");
        }
    };

    ChildEventListener newTeamRegistered = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    Button.OnClickListener addOrUpdateTeamListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            MainActivity.super.setContentView(R.layout.changeteamdata);
        }
    };

    Button.OnClickListener findTeamByNumber = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Dat's a lotta casting methods! (meme reference)
            if (manager.teams.containsKey(Integer.decode(teamNumberEdit.getText().toString()))) {
                Team targetTeam = manager.teams.get(Integer.decode(teamNumberEdit.getText().toString()));
                teamNameEdit.setText(targetTeam.teamName);

            }
        }
    };

    Button.OnClickListener submitTeamListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            manager.updateTeam(constructTeam());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        teamDatabase = FirebaseDatabase.getInstance();

        // If online, update the database
        if (isInternetConnected(getApplicationContext())) {
            teamDatabase.goOnline();
        } else {
            teamDatabase.goOffline();
        }

        abilitiesLayout = (LinearLayout) findViewById(R.id.abilities);
        // Loop through the checkboxes and store them in an array
        for (int i = 0; i < abilitiesLayout.getChildCount(); i++) {
            this.abilitiesChecks[i] = (CheckBox) abilitiesLayout.getChildAt(i);
        }

        submitProgressBar = (ProgressBar) findViewById(R.id.submitProgress);

        findTeam = (Button) findViewById(R.id.autoselectTeam);
        findTeam.setOnClickListener(this.findTeamByNumber);

        submitTeam = (Button) findViewById(R.id.submit);
        submitTeam.setOnClickListener(this.submitTeamListener);

        // This makes the progress bar ASAP green
        // It uses the official ASAP green from the duke's light code
        // instead of R.color.betterasapgreen, which exists only
        // as a darker green more suitable for phone screens.
        int color = 0x18ff04;
        submitProgressBar.getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        submitProgressBar.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);

        teamsRef.addChildEventListener(this.newTeamRegistered);

        teamNameEdit = (EditText) findViewById(R.id.nameBox);
        teamNumberEdit = (EditText) findViewById(R.id.numberBox);

        viewData = (Button) findViewById(R.id.viewdata);
        addOrUpdateData = (Button) findViewById(R.id.addOrUpdate);

        viewData.setOnClickListener(this.viewDataListener);
        addOrUpdateData.setOnClickListener(this.addOrUpdateTeamListener);

        manager = new TeamManager(getApplicationContext(), teamsRef, this.newTeamRegistered, submitProgressBar);
    }

    /**
     * Checks for Wifi connectivity
     * @param context application context
     * @return if the network is connected
     */
    boolean isInternetConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private Team constructTeam() {
        TeamAbilities tempTeamAbilities = new TeamAbilities(this.abilitiesChecks);
        return new Team(Integer.decode(this.teamNumberEdit.getText().toString()),
                this.teamNameEdit.getText().toString(), tempTeamAbilities);
    }
}
