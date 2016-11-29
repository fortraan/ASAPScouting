package com.smartstar.christopherjohnson.asapscouting;

import android.content.Context;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Scouting
 *
 * This app uses Firebase to have an offline database for scouting.
 */
public class MainActivity extends AppCompatActivity {
    // Firebase objects
    FirebaseDatabase teamDatabase;
    DatabaseReference teamsRef;

    TeamManager manager;

    // Buttons
    Button viewData;
    Button addOrUpdateData;
    Button findTeam;
    Button findTeamForMatch;
    Button submitTeam;
    Button backFromUpdate;
    Button backFromMatch;

    Button scoringButtons[];
    Button scoringDecrement[];
    TextView scoringTextViews[];

    EditText teamNameEdit;
    EditText teamNumberEdit;
    EditText teamNameEditMatch;
    EditText teamNumberEditMatch;

    LinearLayout abilitiesLayout;
    LinearLayout scoringContainer;
    LinearLayout scoringLayouts[];

    ProgressBar submitProgressBar;

    CheckBox abilitiesChecks[];

    // This was used for making "Toasts" to display error notices, but not anymore
    //Context context;

    // Click listener for viewData
    Button.OnClickListener viewDataListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            teamsRef = teamDatabase.getReference("teams");
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

    Button.OnClickListener backFromUpdateListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            setContentView(R.layout.activity_main);
        }
    };

    Button.OnClickListener backFromMatchListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            setContentView(R.layout.activity_main);
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

        scoringContainer = (LinearLayout) findViewById(R.id.container);
        for (int i = 0; i < scoringContainer.getChildCount(); i++) {
            scoringLayouts[i] = (LinearLayout) scoringContainer.getChildAt(i);
            scoringButtons[i] = (Button) scoringLayouts[i].getChildAt(0);
            scoringDecrement[i] = (Button) scoringLayouts[i].getChildAt(1);
            scoringTextViews[i] = (TextView) scoringLayouts[i].getChildAt(2);

            final int finalI = i;
            scoringButtons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    scoringTextViews[finalI].setText(String.valueOf(Integer.decode(String.valueOf(scoringTextViews[finalI].getText())) + 1));
                }
            });
            scoringDecrement[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    scoringTextViews[finalI].setText(String.valueOf(Integer.decode(String.valueOf(scoringTextViews[finalI].getText())) - 1));
                }
            });
        }

        submitProgressBar = (ProgressBar) findViewById(R.id.submitProgress);

        findTeam = (Button) findViewById(R.id.autoselectTeam);
        findTeam.setOnClickListener(this.findTeamByNumber);

        findTeamForMatch = (Button) findViewById(R.id.autofindteam);
        findTeamForMatch.setOnClickListener(this.findTeamByNumber);

        submitTeam = (Button) findViewById(R.id.submit);
        submitTeam.setOnClickListener(this.submitTeamListener);

        backFromUpdate = (Button) findViewById(R.id.back);
        backFromUpdate.setOnClickListener(this.backFromUpdateListener);

        backFromMatch = (Button) findViewById(R.id.backFromMatch);
        backFromMatch.setOnClickListener(this.backFromMatchListener);



        // This makes the progress bar ASAP green
        // It uses the official ASAP green from the duke's light code
        // instead of R.color.betterasapgreen, which exists only
        // as a darker green more suitable for phone screens.
        int color = 0x18ff04;
        submitProgressBar.getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        submitProgressBar.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);

        teamNameEdit = (EditText) findViewById(R.id.nameBox);
        teamNumberEdit = (EditText) findViewById(R.id.numberBox);

        teamNameEditMatch = (EditText) findViewById(R.id.teamNameEdit);
        teamNumberEditMatch = (EditText) findViewById(R.id.teamNumberEdit);

        viewData = (Button) findViewById(R.id.viewdata);
        addOrUpdateData = (Button) findViewById(R.id.addOrUpdate);

        viewData.setOnClickListener(this.viewDataListener);
        addOrUpdateData.setOnClickListener(this.addOrUpdateTeamListener);

        manager = new TeamManager(getApplicationContext(), teamsRef, submitProgressBar);
    }

    @Override
    protected void onPause() {
        super.onPause();
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
