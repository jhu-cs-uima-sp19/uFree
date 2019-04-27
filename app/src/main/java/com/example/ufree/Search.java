package com.example.ufree;
import java.util.ArrayList; // import the ArrayList class

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Search {
    String queryText;
    String userId;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();





    public Search(String queryText, String userId) {
        this.queryText = queryText;
        this.userId = userId;
    }



    public ArrayList<String> searchAll() {
        final ArrayList ids = new ArrayList();
        final DatabaseReference allref = database.getInstance().getReference("users");
        //    allref.addValueEventListener(new ValueEventListener() {
        //        @Override
        //        public void onDataChange(DataSnapshot allNameSnapshot) {
        Query query = allref.orderByChild("fullName").startAt(queryText).endAt(queryText + "\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot uniqueKeySnapshot : dataSnapshot.getChildren()) {
                    ids.add(uniqueKeySnapshot.getKey());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });
        //        }
        //        @Override
        //        public void onCancelled(DatabaseError databaseError) {
        // ...
        //        }
        //    });
        return ids;
    }



    public ArrayList<String> searchFriend() {
        final DatabaseReference nameref =database.getInstance().getReference("users");
        final ArrayList ids = new ArrayList();
        final DatabaseReference allref = database.getInstance().getReference("users").child(userId);
        //    allref.addValueEventListener(new ValueEventListener() {
        //        @Override
        //        public void onDataChange(DataSnapshot allNameSnapshot) {
        Query query = allref.orderByChild("friends").startAt(queryText).endAt(queryText + "\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot uniqueKeySnapshot : dataSnapshot.getChildren()) {
                    nameref.child(uniqueKeySnapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            ids.add(dataSnapshot.getValue(User.class).getFullName());
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });
        //        }
        //        @Override
        //        public void onCancelled(DatabaseError databaseError) {
        // ...
        //        }
        //    });
        return ids;
    }






    //search list of all users based on name
    // final FirebaseDatabase database = FirebaseDatabase.getInstance();
    // final DatabaseReference dbref =database.getInstance().getReference("users").orderByChild("fullName")
    //       .startAt(queryText).endAt(queryText+"\uf8ff");


    //search list of all friends based on name
    // final FirebaseDatabase database = FirebaseDatabase.getInstance();
    // final DatabaseReference dbref =database.getInstance().getReference("users").child(userId).orderByChild("friends")
    //         .startAt(queryText).endAt(queryText+"\uf8ff");
    // final DatabaseReference nameref =database.getInstance().getReference("users");
    // Result = nameref.child(dbref.getValue()).child("fullName").getValue();


}



    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference dbref = database.getInstance().getReference("users");
    dbref.child(userId).child("friends").setValue(0);
