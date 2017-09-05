package com.wahyudieko.bakingapp.ui;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.wahyudieko.bakingapp.R;
import com.wahyudieko.bakingapp.RecipeWidgetProvider;
import com.wahyudieko.bakingapp.adapter.RecipeStepAdapter;
import com.wahyudieko.bakingapp.data.BakingAppContract;
import com.wahyudieko.bakingapp.entities.Step;
import com.wahyudieko.bakingapp.utils.Utility;

import java.util.ArrayList;

import static com.wahyudieko.bakingapp.ui.MainActivity.LOG_TAG;

public class DetailActivity extends AppCompatActivity implements RecipeStepAdapter.RecipeStepAdapterOnClickHandler{

    private TextView mIngredientsTextView;

    private RecyclerView mRecipeStepRecyclerView;

    private RecipeStepAdapter mRecipeStepAdapter;

    private ArrayList<Step> mRecipeStepList = new ArrayList<Step>();

    private static final int INGREDIENT_LOADER_ID = 0;
    private static final int STEP_LOADER_ID = 1;

    private int ingredientId, stepId, recipeId, recipeServings;
    private String title, recipeName, recipeIngredients;

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        recipeId = getIntent().getIntExtra("recipe_id", 0);
        ingredientId = recipeId;
        stepId = recipeId;
        recipeName = getIntent().getStringExtra("recipe_name");
        recipeServings = getIntent().getIntExtra("recipe_servings", 0);

        if(findViewById(R.id.two_pane_detail_fragment_container) != null){
            mTwoPane = true;
        }

        mIngredientsTextView = (TextView) findViewById(R.id.ingredients_textview);
        mRecipeStepRecyclerView = (RecyclerView) findViewById(R.id.recipe_step_recycler_view);

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mRecipeStepRecyclerView.setLayoutManager(mLinearLayoutManager);

        mRecipeStepRecyclerView.setHasFixedSize(true);

        mRecipeStepAdapter = new RecipeStepAdapter(this);

        mRecipeStepRecyclerView.setAdapter(mRecipeStepAdapter);

        mRecipeStepRecyclerView.setNestedScrollingEnabled(false);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        title = recipeName+ " - "+recipeServings+" portion(s)";
        getSupportActionBar().setTitle(title);

        Bundle bundleForIngredientLoader = null;
        getSupportLoaderManager().initLoader(INGREDIENT_LOADER_ID, bundleForIngredientLoader, dataIngredientsLoaderListener);

        Bundle bundleForStepLoader = null;
        getSupportLoaderManager().initLoader(STEP_LOADER_ID, bundleForStepLoader, dataStepsLoaderListener);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            finish();
        }else if(id ==  R.id.add_to_widget){
            Utility.setUpdateSavedRecipe(getApplicationContext(), recipeId, recipeName, recipeServings, recipeIngredients);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
            int[] appWidgetId = appWidgetManager.getAppWidgetIds(new ComponentName(this, RecipeWidgetProvider.class));
            RecipeWidgetProvider.updateRecipeWidget(getApplicationContext(), appWidgetManager, recipeId, recipeName, recipeServings, recipeIngredients, appWidgetId);
            Toast.makeText(this, "Added "+ recipeName +" to widget!", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    private LoaderManager.LoaderCallbacks<Cursor> dataIngredientsLoaderListener
            = new LoaderManager.LoaderCallbacks<Cursor>() {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            Uri ingredientUri = BakingAppContract.IngredientEntry.buildIngredient();
            Log.v(LOG_TAG, "URI Loader: " + ingredientUri);

            String sortOrder = BakingAppContract.IngredientEntry._ID + " ASC";

            String selection = BakingAppContract.IngredientEntry.COLUMN_ID + "=?";
            String[] selectionArgs = { String.valueOf(ingredientId) };


            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    getBaseContext(),
                    ingredientUri,
                    null,
                    selection,
                    selectionArgs,
                    sortOrder
            );
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            setIngredientsData(data);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    };

    private LoaderManager.LoaderCallbacks<Cursor> dataStepsLoaderListener
            = new LoaderManager.LoaderCallbacks<Cursor>() {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            Uri stepUri = BakingAppContract.StepEntry.buildStep();
            Log.v(LOG_TAG, "URI Loader: " + stepUri);

            String sortOrder = BakingAppContract.StepEntry._ID + " ASC";

            String selection = BakingAppContract.StepEntry.COLUMN_ID + "=?";
            String[] selectionArgs = { String.valueOf(stepId) };


            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    getBaseContext(),
                    stepUri,
                    null,
                    selection,
                    selectionArgs,
                    sortOrder
            );
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            setStepsData(data);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    };

    private void setIngredientsData(Cursor cursor){
        if(cursor == null){
            Log.v(LOG_TAG, "No ingredient data");
        }else {
            if(cursor.getCount() > 0){
                cursor.moveToFirst();
                boolean hasCursor = cursor.moveToFirst();
                int nCursor = cursor.getCount();

                Log.v("Total ingredient", ""+nCursor);

                String ingredients = "";

                if(hasCursor){
                    for (int i=0; i<nCursor; i++){

                        int quantity = cursor.getInt(cursor.getColumnIndex(BakingAppContract.IngredientEntry.COLUMN_QUANTITY));
                        String measure = cursor.getString(cursor.getColumnIndex(BakingAppContract.IngredientEntry.COLUMN_MEASURE));
                        String ingredient = cursor.getString(cursor.getColumnIndex(BakingAppContract.IngredientEntry.COLUMN_INGREDIENT));

                        String singleIngredient = "*" +" "+ingredient+" - "+quantity+" "+measure + "\n";
                        ingredients = ingredients + singleIngredient;

                        cursor.moveToNext();

                    }
                }

                recipeIngredients = ingredients;

                mIngredientsTextView.setText(ingredients);

            }else {
                Log.v(LOG_TAG, "No ingredient data");
            }
        }
    }

    private void setStepsData(Cursor cursor){
        mRecipeStepList.clear();
        if(cursor == null){
            Log.v("Res","No recipe step data");
        }else {
            if(cursor.getCount() > 0){
                cursor.moveToFirst();
                boolean hasCursor = cursor.moveToFirst();
                int nCursor = cursor.getCount();

                if(hasCursor){
                    for (int i=0; i<nCursor; i++){
                        Step step = new Step();
                        int sortId = cursor.getInt(cursor.getColumnIndex(BakingAppContract.StepEntry.COLUMN_SORT_ID));
                        String shortDescription = cursor.getString(cursor.getColumnIndex(BakingAppContract.StepEntry.COLUMN_SHORT_DESCRIPTION));
                        String description = cursor.getString(cursor.getColumnIndex(BakingAppContract.StepEntry.COLUMN_DESCRIPTION));
                        String videoUrl = cursor.getString(cursor.getColumnIndex(BakingAppContract.StepEntry.COLUMN_VIDEO_URL));
                        String thumbnailUrl = cursor.getString(cursor.getColumnIndex(BakingAppContract.StepEntry.COLUMN_THUMBNAIL_URL));

                        step.setSortId(sortId);
                        step.setShortDescription(shortDescription);
                        step.setDescription(description);
                        step.setVideoURL(videoUrl);
                        step.setThumbnailURL(thumbnailUrl);

                        mRecipeStepList.add(step);
                        cursor.moveToNext();

                    }

                    mRecipeStepAdapter.setRecipeStepList(mRecipeStepList);
                }
                showRecipeStepData();
            }else {
                Log.v("Res","No recipe step data");
            }
        }

        Log.v("Recipe total",""+mRecipeStepList.size());
        Log.v("Recipe object",""+mRecipeStepList.toString());
    }

    private void showRecipeStepData(){
        mRecipeStepRecyclerView.setVisibility(View.VISIBLE);
        mRecipeStepRecyclerView.clearFocus();
    }



    @Override
    public void onClick(Step step, int position) {
        int stepId = step.getSortId();
        String stepShortDescription = step.getShortDescription();
        String stepDescription = step.getDescription();
        String videoUrl = step.getVideoURL();
        String thumbnailUrl = step.getThumbnailURL();

        if(mTwoPane){
            // new body fragment
            StepFragment stepFragment = new StepFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("step_list", mRecipeStepList);
            bundle.putInt(StepFragment.ARG_SECTION_NUMBER,position);
            stepFragment.setArguments(bundle);
            fragmentManager.beginTransaction()
                    .add(R.id.two_pane_detail_fragment_container, stepFragment)
                    .commit();

        }else {
            Context context = this;
            Intent intent = new Intent(context, StepActivity.class);
            intent.putExtra("title", title);
            intent.putExtra("step_id", stepId);
            intent.putExtra("step_short_description", stepShortDescription);
            intent.putExtra("step_description", stepDescription);
            intent.putExtra("video_url", videoUrl);
            intent.putExtra("thumbnail_url", thumbnailUrl);
            intent.putParcelableArrayListExtra("step_list", mRecipeStepList);
            intent.putExtra("id_step",""+position);
            Toast.makeText(context, stepShortDescription, Toast.LENGTH_SHORT).show();
            startActivity(intent);
        }

    }
}
