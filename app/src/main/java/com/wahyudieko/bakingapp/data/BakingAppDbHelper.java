package com.wahyudieko.bakingapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.wahyudieko.bakingapp.data.BakingAppContract.*;

/**
 * Created by EKO on 04/09/2017.
 */

public class BakingAppDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "recipe.db";


    public BakingAppDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_RECIPE_TABLE = "CREATE TABLE " + RecipeEntry.TABLE_NAME + " (" +
                RecipeEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                RecipeEntry.COLUMN_ID + " TEXT NOT NULL, " +
                RecipeEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                RecipeEntry.COLUMN_INGREDIENTS + " INTEGER NOT NULL, " +
                RecipeEntry.COLUMN_STEPS + " INTEGER NOT NULL, " +
                RecipeEntry.COLUMN_SERVINGS + " INTEGER NOT NULL," +
                RecipeEntry.COLUMN_IMAGE + " TEXT NOT NULL);";

        final String SQL_CREATE_INGREDIENT_TABLE = "CREATE TABLE " + IngredientEntry.TABLE_NAME + " (" +
                IngredientEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                IngredientEntry.COLUMN_ID + " TEXT NOT NULL, " +
                IngredientEntry.COLUMN_QUANTITY + " FLOAT NOT NULL, " +
                IngredientEntry.COLUMN_MEASURE + " TEXT NOT NULL, " +
                IngredientEntry.COLUMN_INGREDIENT + " TEXT NOT NULL);";

        final String SQL_CREATE_STEP_TABLE = "CREATE TABLE " + StepEntry.TABLE_NAME + " (" +
                StepEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                StepEntry.COLUMN_ID + " TEXT NOT NULL, " +
                StepEntry.COLUMN_SORT_ID + " TEXT NOT NULL, " +
                StepEntry.COLUMN_SHORT_DESCRIPTION + " TEXT NOT NULL, " +
                StepEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                StepEntry.COLUMN_VIDEO_URL + " TEXT NOT NULL, " +
                StepEntry.COLUMN_THUMBNAIL_URL + " TEXT NOT NULL);";


        sqLiteDatabase.execSQL(SQL_CREATE_RECIPE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_INGREDIENT_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_STEP_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(" DROP TABLE IF EXIST " + RecipeEntry.TABLE_NAME);
        sqLiteDatabase.execSQL(" DROP TABLE IF EXIST " + IngredientEntry.TABLE_NAME);
        sqLiteDatabase.execSQL(" DROP TABLE IF EXIST " + StepEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
