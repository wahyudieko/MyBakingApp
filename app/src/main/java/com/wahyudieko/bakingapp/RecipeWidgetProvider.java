package com.wahyudieko.bakingapp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

import com.wahyudieko.bakingapp.ui.MainActivity;
import com.wahyudieko.bakingapp.utils.Constant;

import static android.content.Context.MODE_PRIVATE;

/**
 * Implementation of App Widget functionality.
 */
public class RecipeWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int recipeId, String recipeName, int recipeServings, String recipeIngredients, int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.recipe_widget_provider);
        views.setTextViewText(R.id.appwidget_recipe_name_text, recipeName);
        views.setTextViewText(R.id.appwidget_ingredients_text, recipeIngredients);

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        views.setOnClickPendingIntent(R.id.appwidget_frame_relative_layout, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        SharedPreferences prefs = context.getSharedPreferences(Constant.RECIPE_PREF, MODE_PRIVATE);
        int recipeId = prefs.getInt("recipe_id", 0);
        String recipeName = prefs.getString("recipe_name", "Recipe data not added yet");
        int recipeServings = prefs.getInt("recipe_servings", 0);
        String recipeIngredients = prefs.getString("recipe_ingredients", "Recipe ingredients in unavailable");

        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, recipeId, recipeName, recipeServings, recipeIngredients, appWidgetId);
        }
    }

    public static void updateRecipeWidget(Context context, AppWidgetManager appWidgetManager,
                                          int recipeId, String recipeName, int recipeServings, String recipeIngredients, int[] appWidgetIds){
        for (int appWidgetId : appWidgetIds){
            updateAppWidget(context, appWidgetManager, recipeId, recipeName, recipeServings, recipeIngredients, appWidgetId);
        }

    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }


}

