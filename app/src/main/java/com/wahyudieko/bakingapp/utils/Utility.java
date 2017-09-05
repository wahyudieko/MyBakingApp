package com.wahyudieko.bakingapp.utils;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.wahyudieko.bakingapp.connection.BakingAppClient;
import com.wahyudieko.bakingapp.connection.BakingAppService;
import com.wahyudieko.bakingapp.data.BakingAppContract;
import com.wahyudieko.bakingapp.entities.Ingredient;
import com.wahyudieko.bakingapp.entities.Recipe;
import com.wahyudieko.bakingapp.entities.Step;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by EKO on 04/09/2017.
 */

public class Utility {

    public static boolean isConnected(Context context) {
        ConnectivityManager
                cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();
    }

    public static String simpleDateFormat(String dateStr){

        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        SimpleDateFormat output = new SimpleDateFormat("dd MMMM yyyy", Locale.US);
        Date inputDate;
        String outputDate;
        try {
            inputDate = input.parse(dateStr);
            outputDate = output.format(inputDate);
            return outputDate;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void deleteAllRecipeRecords(Context context) {
        context.getContentResolver().delete(
                BakingAppContract.RecipeEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = context.getContentResolver().query(
                BakingAppContract.RecipeEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        if (cursor != null) {
            cursor.close();
        }
    }

    public static void deleteAllIngredientRecords(Context context) {
        context.getContentResolver().delete(
                BakingAppContract.IngredientEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = context.getContentResolver().query(
                BakingAppContract.RecipeEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        if (cursor != null) {
            cursor.close();
        }
    }

    public static void deleteAllStepRecords(Context context) {
        context.getContentResolver().delete(
                BakingAppContract.StepEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = context.getContentResolver().query(
                BakingAppContract.RecipeEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        if (cursor != null) {
            cursor.close();
        }
    }


    public static void getRecipeData(final Context context){

        final ProgressDialog pDialog;

        if(Utility.isConnected(context)){

            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Updating recipe data...");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.show();

            BakingAppService bakingAppService = BakingAppClient.getClient().create(BakingAppService.class);

            Call<List<Recipe>> call = bakingAppService.getRecipes();

            Log.v("Res:", bakingAppService.getRecipes().request().url().toString());


            call.enqueue(new Callback<List<Recipe>>() {
                @Override
                public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {

                    List<Recipe> bakingAppResponse = response.body();

                    if(bakingAppResponse != null){
                        Vector<ContentValues> cVVectorRecipe = new Vector<ContentValues>(bakingAppResponse.size());
                        Vector<ContentValues> cVVectorIngredients;
                        Vector<ContentValues> cVVectorStep;

                        deleteAllStepRecords(context);
                        Log.v("Res steps", "All databases was deleted");
                        deleteAllIngredientRecords(context);
                        Log.v("Res ingredients", "All databases was deleted");

                        for (int i = 0; i <bakingAppResponse.size(); i++){

                            // recipe
                            int recipeId = bakingAppResponse.get(i).getId();
                            String recipeName = bakingAppResponse.get(i).getName();
                            int ingredientsId = bakingAppResponse.get(i).getId();
                            int stepsId = bakingAppResponse.get(i).getId();
                            int recipeServings = bakingAppResponse.get(i).getServings();
                            String recipeImage = bakingAppResponse.get(i).getImage();

                            List<Ingredient> ingredients = bakingAppResponse.get(i).getIngredients();
                            cVVectorIngredients = new Vector<ContentValues>(ingredients.size());

                            for (int j = 0; j <ingredients.size(); j++){

                                // ingredient
                                double quantity = ingredients.get(j).getQuantity();
                                String measure = ingredients.get(j).getMeasure();
                                String ingredient = ingredients.get(j).getIngredient();

                                ContentValues ingredientValues = new ContentValues();

                                ingredientValues.put(BakingAppContract.IngredientEntry.COLUMN_ID, ingredientsId);
                                ingredientValues.put(BakingAppContract.IngredientEntry.COLUMN_QUANTITY, quantity);
                                ingredientValues.put(BakingAppContract.IngredientEntry.COLUMN_MEASURE, measure);
                                ingredientValues.put(BakingAppContract.IngredientEntry.COLUMN_INGREDIENT, ingredient);

                                cVVectorIngredients.add(ingredientValues);

                            }

                            List<Step> steps = bakingAppResponse.get(i).getSteps();
                            cVVectorStep = new Vector<ContentValues>(steps.size());

                            for (int k = 0; k <steps.size(); k++){

                                // Step
                                int sortId = steps.get(k).getId();
                                String shortDescription = steps.get(k).getShortDescription();
                                String description = steps.get(k).getDescription();
                                String videoURL = steps.get(k).getVideoURL();
                                String thumbnailURL = steps.get(k).getThumbnailURL();


                                ContentValues stepValues = new ContentValues();

                                stepValues.put(BakingAppContract.StepEntry.COLUMN_ID, stepsId);
                                stepValues.put(BakingAppContract.StepEntry.COLUMN_SORT_ID, sortId);
                                stepValues.put(BakingAppContract.StepEntry.COLUMN_SHORT_DESCRIPTION, shortDescription);
                                stepValues.put(BakingAppContract.StepEntry.COLUMN_DESCRIPTION, description);
                                stepValues.put(BakingAppContract.StepEntry.COLUMN_VIDEO_URL, videoURL);
                                stepValues.put(BakingAppContract.StepEntry.COLUMN_THUMBNAIL_URL, thumbnailURL);

                                cVVectorStep.add(stepValues);

                            }

                            ContentValues recipeValues = new ContentValues();

                            recipeValues.put(BakingAppContract.RecipeEntry.COLUMN_ID, recipeId);
                            recipeValues.put(BakingAppContract.RecipeEntry.COLUMN_NAME, recipeName);
                            recipeValues.put(BakingAppContract.RecipeEntry.COLUMN_INGREDIENTS, ingredientsId);
                            recipeValues.put(BakingAppContract.RecipeEntry.COLUMN_STEPS, stepsId);
                            recipeValues.put(BakingAppContract.RecipeEntry.COLUMN_SERVINGS, recipeServings);
                            recipeValues.put(BakingAppContract.RecipeEntry.COLUMN_IMAGE, recipeImage);

                            cVVectorRecipe.add(recipeValues);

                            if(cVVectorIngredients.size() > 0){
                                ContentValues[] cvArrayIngredients = new ContentValues[cVVectorIngredients.size()];
                                cVVectorIngredients.toArray(cvArrayIngredients);

                                int rowsIngredientInserted = context.getContentResolver()
                                        .bulkInsert(BakingAppContract.IngredientEntry.CONTENT_URI, cvArrayIngredients);

                                Log.v("Res ingredients "+recipeId, "inserted new " + rowsIngredientInserted + " rows of news data");


                            }

                            if(cVVectorStep.size() > 0){
                                ContentValues[] cvArraySteps = new ContentValues[cVVectorStep.size()];
                                cVVectorStep.toArray(cvArraySteps);

                                int rowsStepInserted = context.getContentResolver()
                                        .bulkInsert(BakingAppContract.StepEntry.CONTENT_URI, cvArraySteps);

                                Log.v("Res steps "+recipeId, "inserted new " + rowsStepInserted + " rows of news data");

                            }

                        }

                        if (cVVectorRecipe.size() > 0) {
                            ContentValues[] cvArrayRecipe = new ContentValues[cVVectorRecipe.size()];
                            cVVectorRecipe.toArray(cvArrayRecipe);
                            deleteAllRecipeRecords(context);
                            Log.v("Res recipe", "All databases was deleted");
                            int rowsRecipeInserted = context.getContentResolver()
                                    .bulkInsert(BakingAppContract.RecipeEntry.CONTENT_URI, cvArrayRecipe);

                            Log.v("Res recipe", "inserted new " + rowsRecipeInserted + " rows of news data");
                            Toast.makeText(context, "Recipe data updated", Toast.LENGTH_SHORT).show();
                        }
                        pDialog.dismiss();
                    }else {
                        Toast.makeText(context, "Error: Recipe data is null", Toast.LENGTH_SHORT).show();
                        pDialog.dismiss();
                    }

                }

                @Override
                public void onFailure(Call<List<Recipe>> call, Throwable t) {
                    Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                    pDialog.dismiss();
                }
            });

        }else {
            Log.e(context.getPackageName(), "internet not connected");
        }
    }

    public static void setUpdateDate(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences(Constant.DATE_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String currentDate = sdf.format(new Date());
        editor.putString("date", Utility.simpleDateFormat(currentDate));
        editor.commit();
    }

    public static void setUpdateSavedRecipe(Context context, int recipeId, String recipeName, int recipeServings, String recipeIngredients){
        SharedPreferences sharedPref = context.getSharedPreferences(Constant.RECIPE_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("recipe_id", recipeId);
        editor.putString("recipe_name", recipeName);
        editor.putInt("recipe_servings", recipeServings);
        editor.putString("recipe_ingredients", recipeIngredients);

        editor.commit();
    }

}
