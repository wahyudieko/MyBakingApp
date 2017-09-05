package com.wahyudieko.bakingapp.connection;

import com.wahyudieko.bakingapp.entities.Recipe;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by EKO on 04/09/2017.
 */

public interface BakingAppService {

    @GET("/topher/2017/May/59121517_baking/baking.json")
    Call<List<Recipe>> getRecipes();

}
