package com.wahyudieko.bakingapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.wahyudieko.bakingapp.R;
import com.wahyudieko.bakingapp.entities.Recipe;

import java.util.List;

/**
 * Created by EKO on 04/09/2017.
 */

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeAdapterViewHolder>{
    private List<Recipe> mRecipeList;
    private Context mContext;
    private final RecipeAdapterOnClickHandler mClickHandler;

    public RecipeAdapter(RecipeAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    public interface RecipeAdapterOnClickHandler {
        void onClick(Recipe recipe);
    }

    public class RecipeAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final ImageView mRecipeImageView;
        public final TextView mRecipeNameTextView;

        public RecipeAdapterViewHolder(View itemView) {
            super(itemView);
            mRecipeImageView = itemView.findViewById(R.id.recipe_image_iv);
            mRecipeNameTextView = itemView.findViewById(R.id.recipe_name_tv);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            Recipe recipe = mRecipeList.get(adapterPosition);
            mClickHandler.onClick(recipe);
        }
    }

    @Override
    public RecipeAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        int layoutForListItem = R.layout.recipe_list_item;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutForListItem, parent, shouldAttachToParentImmediately);
        return new RecipeAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecipeAdapterViewHolder holder, int position) {
        Recipe recipe = mRecipeList.get(position);

        String recipeName = recipe.getName();
        String recipeImage = recipe.getImage();

        holder.mRecipeNameTextView.setText(recipeName);
        if(!recipeImage.equals("")){
            Glide.with(mContext).load(recipeImage)
                    .dontAnimate()
                    .into(holder.mRecipeImageView);
        }

    }

    @Override
    public int getItemCount() {
        if(null == mRecipeList) return 0;
        return mRecipeList.size();
    }

    public void setRecipeList(List<Recipe> recipeList){
        mRecipeList = recipeList;
        notifyDataSetChanged();
    }
}
