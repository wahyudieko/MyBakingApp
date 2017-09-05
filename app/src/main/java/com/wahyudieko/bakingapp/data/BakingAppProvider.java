package com.wahyudieko.bakingapp.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by EKO on 04/09/2017.
 */

public class BakingAppProvider extends ContentProvider{
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private BakingAppDbHelper mOpenHelper;

    private static final int RECIPE = 100;
    private static final int RECIPE_BY_ID = 101;

    private static final int INGREDIENT = 102;
    private static final int INGREDIENT_BY_ID = 103;

    private static final int STEP = 104;
    private static final int STEP_BY_ID = 105;

    private static final SQLiteQueryBuilder sRecipesQueryBuilder;
    private static final SQLiteQueryBuilder sIngredientQueryBuilder;
    private static final SQLiteQueryBuilder sStepsQueryBuilder;


    static {
        sRecipesQueryBuilder = new SQLiteQueryBuilder();
        sIngredientQueryBuilder = new SQLiteQueryBuilder();
        sStepsQueryBuilder = new SQLiteQueryBuilder();

        sRecipesQueryBuilder.setTables(
                BakingAppContract.RecipeEntry.TABLE_NAME
        );
        sIngredientQueryBuilder.setTables(
                BakingAppContract.IngredientEntry.TABLE_NAME
        );
        sStepsQueryBuilder.setTables(
                BakingAppContract.StepEntry.TABLE_NAME
        );
    }

    private static final String sRecipeSelection =
            BakingAppContract.RecipeEntry.TABLE_NAME+
                    "." + BakingAppContract.RecipeEntry.COLUMN_ID + " = ? ";

    private static final String sIngredientSelection =
            BakingAppContract.IngredientEntry.TABLE_NAME+
                    "." + BakingAppContract.IngredientEntry.COLUMN_ID + " = ? ";

    private static final String sStepSelection =
            BakingAppContract.StepEntry.TABLE_NAME+
                    "." + BakingAppContract.StepEntry.COLUMN_ID + " = ? ";


    private Cursor getRecipe(Uri uri, String[] projection, String sortOrder) {
        String univSetting = BakingAppContract.RecipeEntry.getRecipeFromUri(uri);
        String[] selectionArgs;
        String selection;

        selectionArgs = new String[]{univSetting};
        selection = sRecipeSelection;

        return sRecipesQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getIngredient(Uri uri, String[] projection, String sortOrder) {
        String univSetting = BakingAppContract.RecipeEntry.getRecipeFromUri(uri);
        String[] selectionArgs;
        String selection;

        selectionArgs = new String[]{univSetting};
        selection = sIngredientSelection;

        return sIngredientQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getStep(Uri uri, String[] projection, String sortOrder) {
        String univSetting = BakingAppContract.RecipeEntry.getRecipeFromUri(uri);
        String[] selectionArgs;
        String selection;

        selectionArgs = new String[]{univSetting};
        selection = sStepSelection;

        return sStepsQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = BakingAppContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, BakingAppContract.PATH_RECIPE, RECIPE);
        matcher.addURI(authority, BakingAppContract.PATH_RECIPE + "/*", RECIPE_BY_ID);
        matcher.addURI(authority, BakingAppContract.PATH_INGREDIENT, INGREDIENT);
        matcher.addURI(authority, BakingAppContract.PATH_INGREDIENT + "/*", INGREDIENT_BY_ID);
        matcher.addURI(authority, BakingAppContract.PATH_STEP, STEP);
        matcher.addURI(authority, BakingAppContract.PATH_STEP + "/*", STEP_BY_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new BakingAppDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "movie"
            case RECIPE: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        BakingAppContract.RecipeEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "recipe/*"
            case RECIPE_BY_ID: {
                retCursor = getRecipe(uri, projection, sortOrder);
                break;
            }
            case INGREDIENT: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        BakingAppContract.IngredientEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "ingredient/*"
            case INGREDIENT_BY_ID: {
                retCursor = getIngredient(uri, projection, sortOrder);
                break;
            }
            case STEP: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        BakingAppContract.StepEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "step/*"
            case STEP_BY_ID: {
                retCursor = getStep(uri, projection, sortOrder);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            // "Recipe"
            case RECIPE:
                return BakingAppContract.RecipeEntry.CONTENT_TYPE;
            // "Ingredient"
            case INGREDIENT:
                return BakingAppContract.IngredientEntry.CONTENT_TYPE;
            // "Step"
            case STEP:
                return BakingAppContract.StepEntry.CONTENT_TYPE;


            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;
        switch (match) {
            case RECIPE: {
                long _id = db.insert(BakingAppContract.RecipeEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = BakingAppContract.RecipeEntry.buildRecipeUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case INGREDIENT: {
                long _id = db.insert(BakingAppContract.IngredientEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = BakingAppContract.IngredientEntry.buildIngredientUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case STEP: {
                long _id = db.insert(BakingAppContract.StepEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = BakingAppContract.StepEntry.buildStepUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        switch (match) {
            case RECIPE:
                rowsDeleted = db.delete(
                        BakingAppContract.RecipeEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case INGREDIENT:
                rowsDeleted = db.delete(
                        BakingAppContract.IngredientEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case STEP:
                rowsDeleted = db.delete(
                        BakingAppContract.StepEntry.TABLE_NAME, selection, selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Because a null deletes all rows
        if (selection == null || rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case RECIPE:
                rowsUpdated = db.update(BakingAppContract.RecipeEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case INGREDIENT:
                rowsUpdated = db.update(BakingAppContract.IngredientEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case STEP:
                rowsUpdated = db.update(BakingAppContract.StepEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;


            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case RECIPE:
                db.beginTransaction();
                int returnCountRecipe = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(BakingAppContract.RecipeEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCountRecipe++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCountRecipe;
            case INGREDIENT:
                db.beginTransaction();
                int returnCountIngredient = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(BakingAppContract.IngredientEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCountIngredient++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCountIngredient;
            case STEP:
                db.beginTransaction();
                int returnCountStep = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(BakingAppContract.StepEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCountStep++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCountStep;

            default:
                return super.bulkInsert(uri, values);
        }
    }
}
