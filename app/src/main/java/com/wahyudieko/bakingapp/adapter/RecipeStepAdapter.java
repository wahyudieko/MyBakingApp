package com.wahyudieko.bakingapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wahyudieko.bakingapp.R;
import com.wahyudieko.bakingapp.entities.Step;

import java.util.List;

/**
 * Created by EKO on 04/09/2017.
 */

public class RecipeStepAdapter extends RecyclerView.Adapter<RecipeStepAdapter.RecipeStepAdapterViewHolder>{

    private List<Step> mRecipeStepList;
    private Context mContext;
    private final RecipeStepAdapterOnClickHandler mClickHandler;

    public RecipeStepAdapter(RecipeStepAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    public interface RecipeStepAdapterOnClickHandler {
        void onClick(Step step, int position);
    }

    public class RecipeStepAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final TextView mRecipeStepNameTextView;

        public RecipeStepAdapterViewHolder(View itemView) {
            super(itemView);
            mRecipeStepNameTextView = itemView.findViewById(R.id.recipe_step_name_tv);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            Step step = mRecipeStepList.get(adapterPosition);
            mClickHandler.onClick(step, adapterPosition);
        }
    }

    @Override
    public RecipeStepAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();

        int layoutForListItem = R.layout.recipe_step_list_item;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutForListItem, parent, shouldAttachToParentImmediately);
        return new RecipeStepAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecipeStepAdapterViewHolder holder, int position) {
        Step recipeStep = mRecipeStepList.get(position);

        String recipeStepName = recipeStep.getShortDescription();

        holder.mRecipeStepNameTextView.setText(recipeStepName);

    }

    @Override
    public int getItemCount() {
        if(null == mRecipeStepList) return 0;
        return mRecipeStepList.size();
    }

    public void setRecipeStepList(List<Step> recipeStepList){
        mRecipeStepList = recipeStepList;
        notifyDataSetChanged();
    }
}
