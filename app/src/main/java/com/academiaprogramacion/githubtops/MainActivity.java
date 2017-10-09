package com.academiaprogramacion.githubtops;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.academiaprogramacion.githubtops.activities.SearchResult;
import com.academiaprogramacion.githubtops.adapters.GridAdapter;
import com.academiaprogramacion.githubtops.helpers.Utilities;
import com.academiaprogramacion.githubtops.models.LanguageGridItem;
import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    private ArrayList<LanguageGridItem> mMenuItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.app_name);
        }
        setupGridView();
    }

    private void setupGridView() {
        loadDefultData();
        final GridView grid = (GridView) findViewById(R.id.grid_menu);
        GridAdapter adapter = new GridAdapter(getApplicationContext(), mMenuItems);
        grid.setAdapter(adapter);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LanguageGridItem item = mMenuItems.get(position);
                startSearchResultActivity(item.getLanguage());
            }
        });
    }

    /**
     * Load default menu languages
     */
    private void loadDefultData(){
        mMenuItems = new ArrayList<LanguageGridItem>();
        List<String> defaultLanguages =
                Arrays.asList(getResources().getStringArray(R.array.default_languages));
        for(String language : defaultLanguages) {
            mMenuItems.add(new LanguageGridItem(language,
                    Utilities.getDrawableFromLanguage(this.getApplicationContext(), language)));
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

    /**
     * Start search results activity.
     * @param language
     */
    private void startSearchResultActivity(String language){
        Intent intent = SearchResult.getIntentInstance(this.getApplicationContext(), language);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if(!query.equals("")){
            startSearchResultActivity(query);
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        // do nothing
        return false;
    }

}
