package com.innovae.movies.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.innovae.movies.R;
import com.innovae.movies.adapters.TrailersAdapter.TrailerViewHolder;
import com.innovae.movies.model.Video;
import com.innovae.movies.util.Utility;
import com.squareup.picasso.Picasso;

import java.util.List;

public class TrailersAdapter extends RecyclerView.Adapter<TrailerViewHolder>{

    private List<Video> mTrailers;
    private int rowLayout;
    private Context mContext;

    public TrailersAdapter(Context context, int rowLayout,List<Video> trailers){
        this.rowLayout = rowLayout;
        this.mContext = context;
        this.mTrailers = trailers;
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout,parent,false);
        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int position) {
        String videoTitle = mTrailers.get(position).getName();
        if(videoTitle!=null){
            holder.trailerTitle.setText(videoTitle);
        }
        else{
            holder.trailerTitle.setText("");
        }
        String thumbNailPath = Utility.buildTrailerThumbNailPath(mTrailers.get(position).getKey());
        Picasso.with(mContext).load(thumbNailPath).placeholder(R.drawable.placeholder_loading).into(holder.trailerVideo);
    }

    @Override
    public int getItemCount() {
        if (null == mTrailers){
            return 0;
        }
        else{
            return mTrailers.size();
        }
    }

    public class TrailerViewHolder extends RecyclerView.ViewHolder{

        ImageView trailerVideo;
        TextView trailerTitle;


        public TrailerViewHolder(View itemView) {
            super(itemView);
            trailerVideo = itemView.findViewById(R.id.iv_video);
            trailerTitle = itemView.findViewById(R.id.tv_video_name);

            trailerVideo.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent youtubeIntent = new Intent(Intent.ACTION_VIEW,
                            Utility.buildYoutubeTrailerUrl(mTrailers.get(getAdapterPosition()).getKey()));
                    mContext.startActivity(youtubeIntent);
                }
            });
        }
    }
}
