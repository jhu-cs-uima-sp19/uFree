package com.example.ufree;

import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import java.util.ArrayList;

public class FriendsSearch extends AppCompatActivity {

    SearchView searchView;
    RecyclerView recyclerView;

    FriendsExistingAdaptor adaptor;

    ArrayList<FriendsSearchData> searchResults = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Setting up recycler view
        recyclerView = (RecyclerView) findViewById(R.id.friendsSearch);
        recyclerView.setLayoutManager(new LinearLayoutManager(FriendsSearch.this));

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) findViewById(R.id.searchView);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // Listening for changes in search view
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setIconified(false);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                searchView.clearFocus();
                searchResults.clear();
             //   SharedPreferences sp = getSharedPreferences("User", MODE_PRIVATE);
             //   String userid = sp.getString("userID", "empty");
                Search sea = new Search(s,"not_needed");
                searchResults = sea.searchAll();
                FriendsSearchAdaptor myAdaptor = new FriendsSearchAdaptor(searchResults, FriendsSearch.this);
                recyclerView.setAdapter(myAdaptor);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }
}
