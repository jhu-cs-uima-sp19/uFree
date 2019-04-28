package com.example.ufree;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList; // import the ArrayList class

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import static android.support.constraint.Constraints.TAG;

public class Search {
    String queryText;
    String userId;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();





    public Search(String queryText, String userId) {
        this.queryText = queryText;
        this.userId = userId;
    }



    public ArrayList<FriendsSearchData> searchAll() {
        final ArrayList ids = new ArrayList();
        final DatabaseReference allref = database.getInstance().getReference("users");
        //Should it be case insensitive?
        //Is event search case insensitive?
        String caseInsensitive = queryText;
        allref.orderByChild("fullName").startAt(caseInsensitive).endAt(caseInsensitive + "b\uf8ff")
                .addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                String temp = dataSnapshot.getKey();
                if(temp.equals(userId)){

                }
                else {
                    ids.add(new FriendsSearchData(temp));
              //      Log.d("test3",dataSnapshot.getKey());
                }
            }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

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



//    final FirebaseDatabase database = FirebaseDatabase.getInstance();
//    final DatabaseReference dbref = database.getInstance().getReference("users");
//    dbref.child(userId).child("friends").setValue(0);
