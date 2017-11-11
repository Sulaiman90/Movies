package com.innovae.movies.util;


import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

public final class Utility {

    public static String buildCompletePosterPath(String posterPath){
        return Constants.IMAGE_LOADING_BASE_URL_342+posterPath;
    }

    public static String buildCompleteBackdropPath(String posterPath){
        return Constants.IMAGE_LOADING_BASE_URL_500+posterPath;
    }

    public static void showDebugToast( Context context,String msg){
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }

    public static String buildTrailerThumbNailPath(String trailerKey){
        return Constants.YOUTUBE_THUMBNAIL_BASE_URL + trailerKey + Constants.YOUTUBE_THUMBNAIL_IMAGE_QUALITY;
    }

    public static Uri buildYoutubeTrailerUrl(String trailerKey){
        return Uri.parse(Constants.YOUTUBE_WATCH_BASE_URL + trailerKey);
    }

}
