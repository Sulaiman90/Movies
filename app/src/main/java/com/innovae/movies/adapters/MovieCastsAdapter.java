package com.innovae.movies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.innovae.movies.R;
import com.innovae.movies.model.MovieCast;
import com.innovae.movies.util.Utility;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieCastsAdapter extends RecyclerView.Adapter<MovieCastsAdapter.CastViewHolder> {

    private List<MovieCast> mCasts;
    private Context mContext;

    public MovieCastsAdapter(Context mContext,List<MovieCast> mCasts){
        this.mContext = mContext;
        this.mCasts = mCasts;
    }

    @Override
    public CastViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_cast,parent,false);
        return new CastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CastViewHolder holder, int position) {
        String castImagePath = Utility.buildCastImagePath(mCasts.get(position).getProfilePath());
        Picasso.with(mContext).load(castImagePath).into(holder.mCastImageView);

        String name = mCasts.get(position).getName();
        String character = mCasts.get(position).getCharacter();
        if (name != null){
            holder.mNameTextView.setText(name);
        }
        if (character != null){
            holder.mCharTextView.setText(character);
        }
    }


    @Override
    public int getItemCount() {
       return mCasts.size();
    }

    public class CastViewHolder extends RecyclerView.ViewHolder{

        ImageView mCastImageView;
        TextView mNameTextView;
        TextView mCharTextView;

        public CastViewHolder(View itemView) {
            super(itemView);
            mCastImageView = itemView.findViewById(R.id.iv_castImage);
            mNameTextView = itemView.findViewById(R.id.text_view_cast_name);
            mCharTextView = itemView.findViewById(R.id.text_view_cast_as);
        }
    }
}
