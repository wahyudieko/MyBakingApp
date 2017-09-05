package com.wahyudieko.bakingapp.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.wahyudieko.bakingapp.R;
import com.wahyudieko.bakingapp.adapter.RecipeAdapter;
import com.wahyudieko.bakingapp.data.BakingAppContract;
import com.wahyudieko.bakingapp.entities.Recipe;
import com.wahyudieko.bakingapp.utils.Utility;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        RecipeAdapter.RecipeAdapterOnClickHandler, LoaderManager.LoaderCallbacks<Cursor>{

    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;

    private LinearLayout mRecipeInfoLinearLayout;

    private RecipeAdapter mRecipeAdapter;

    private List<Recipe> mRecipeList = new ArrayList<>();

    private static final int RECIPE_LOADER_ID = 0;

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRecyclerView = (RecyclerView) findViewById(R.id.recipe_recycler_view);
        mRecipeInfoLinearLayout = (LinearLayout) findViewById(R.id.update_recipe_linear_layout);

        if(findViewById(R.id.two_pane_view) != null){
            mTwoPane = true;
        }

        GridLayoutManager mGridLayoutManager;

        if(mTwoPane){
            mGridLayoutManager =
                    new GridLayoutManager(this, 3);
        }else {
            mGridLayoutManager =
                    new GridLayoutManager(this, 1);
        }

        mRecyclerView.setLayoutManager(mGridLayoutManager);

        mRecyclerView.setHasFixedSize(true);

        mRecipeAdapter = new RecipeAdapter(this);

        mRecyclerView.setAdapter(mRecipeAdapter);

        LoaderManager.LoaderCallbacks<Cursor> callback = MainActivity.this;

        Bundle bundleForLoader = null;

        getSupportLoaderManager().initLoader(RECIPE_LOADER_ID, bundleForLoader, callback);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Button updateButton = (Button) findViewById(R.id.update_recipe_button);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.getRecipeData(MainActivity.this);
                Utility.setUpdateDate(getApplicationContext());
            }
        });


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_recipe) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_share) {
            shareIntent();
        }

        return true;
    }

    private void shareIntent(){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Share Baking App");
        intent.putExtra(Intent.EXTRA_TEXT, "Find your favorite recipe in Android Baking App \n\nYou can download the app in http://wahyudieko.com (when the app is released)");
        intent.setType("text/plain");
        startActivity(Intent.createChooser(intent, "How do you want to share?"));
    }

    @Override
    public void onClick(Recipe recipe) {
        int recipeId = recipe.getId();
        String recipeName = recipe.getName();
        int recipeServings = recipe.getServings();

        Context context = this;
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra("recipe_id", recipeId);
        intent.putExtra("recipe_name", recipeName);
        intent.putExtra("recipe_servings", recipeServings);
        startActivity(intent);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri recipeUri = BakingAppContract.RecipeEntry.buildRecipe();

        String sortOrder = BakingAppContract.RecipeEntry._ID + " ASC";

        return new CursorLoader(
                getBaseContext(),
                recipeUri,
                null,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        setRecipeData(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void showRecipeData(){
        mRecipeInfoLinearLayout.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showEmptyMessage(){
        mRecyclerView.setVisibility(View.GONE);
        mRecipeInfoLinearLayout.setVisibility(View.VISIBLE);
    }

    private void setRecipeData(Cursor cursor){
        mRecipeList.clear();
        if(cursor == null){
            showEmptyMessage();
        }else {
            if(cursor.getCount() > 0){
                cursor.moveToFirst();
                boolean hasCursor = cursor.moveToFirst();
                int nCursor = cursor.getCount();

                if(hasCursor){
                    for (int i=0; i<nCursor; i++){
                        Recipe recipe = new Recipe();
                        int recipeId = cursor.getInt(cursor.getColumnIndex(BakingAppContract.RecipeEntry.COLUMN_ID));
                        String recipeName = cursor.getString(cursor.getColumnIndex(BakingAppContract.RecipeEntry.COLUMN_NAME));
                        int recipeServings = cursor.getInt(cursor.getColumnIndex(BakingAppContract.RecipeEntry.COLUMN_SERVINGS));
                        String recipeImage = cursor.getString(cursor.getColumnIndex(BakingAppContract.RecipeEntry.COLUMN_IMAGE));

                        recipe.setId(recipeId);
                        recipe.setName(recipeName);
                        recipe.setServings(recipeServings);
                        recipe.setImage(recipeImage);

                        mRecipeList.add(recipe);
                        cursor.moveToNext();

                    }

                    mRecipeAdapter.setRecipeList(mRecipeList);
                }
                showRecipeData();
            }else {
                showEmptyMessage();
            }
        }

        Log.v("Recipe total",""+mRecipeList.size());
        Log.v("Recipe object",""+mRecipeList.toString());
    }
}
