package com.smartstar.christopherjohnson.asapscouting;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.util.HashMap;

/**
 * Created by christopher.johnson on 11/28/16.
 */

public class Performance {
    public HashMap<String, Integer> dataMap;

    public Performance(DatabaseReference location) {
        location.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                for (MutableData data : mutableData.getChildren()) {
                    dataMap.put(data.getKey(), (Integer) data.getValue());
                }
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

            }
        });
    }
}
