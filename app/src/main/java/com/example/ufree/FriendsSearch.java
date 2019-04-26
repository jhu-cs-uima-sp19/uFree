package com.example.ufree;

import android.app.SearchManager;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
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

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Setting up recycler view
        recyclerView = (RecyclerView) findViewById(R.id.friendsSearch);
        recyclerView.setLayoutManager(new LinearLayoutManager(FriendsSearch.this));

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) findViewById(R.id.searchView);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // Listening for changes in search view
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                searchView.clearFocus();
                searchResults.clear();

                // TODO. Replace with the actual search araylist
                FriendsSearchData temp = new FriendsSearchData("joanne@cs.jhu.edu");
                searchResults.add(temp);
                temp = new FriendsSearchData("djhfg@dskjf.com");
                searchResults.add(temp);

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
