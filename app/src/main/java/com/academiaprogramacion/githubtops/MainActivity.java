package com.academiaprogramacion.githubtops;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.academiaprogramacion.githubtops.activities.SearchResult;
import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    private Object mMyDataStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.app_name);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchItem.setIcon(
                new IconDrawable(this, Iconify.IconValue.zmdi_search)
                        .colorRes(R.color.colorAccent)
                        .actionBarSize());

        SearchView searchView = (SearchView) searchItem.getActionView();
        if (searchView != null) {
            searchView.setOnQueryTextListener(this);
            searchView.setQueryHint(getString(R.string.search_language));
        }
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if(!query.equals("")){
            Intent i = SearchResult.getIntentInstance(this.getApplicationContext(), query);
            startActivity(i);
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        // do nothing
        return false;
    }

}
