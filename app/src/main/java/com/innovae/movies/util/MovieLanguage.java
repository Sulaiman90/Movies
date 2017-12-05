package com.innovae.movies.util;

public enum MovieLanguage {
    
    ENGLISH("en"),
    FRENCH("fr"),
    RUSSIAN("ru"),
    HINDI("hi"),
    TAMIL("ta"),
    MALAYALAM("mal"),
    TELUGU("te");

    private String value;

    MovieLanguage(String language){
        value = language;
    }

    public static MovieLanguage getLanguageFromString(String string){
        for(MovieLanguage movieLanguage: MovieLanguage.values()){
            if(string.equals(movieLanguage.toString())){
                return movieLanguage;
            }
        }
        throw new IllegalArgumentException("No language with text " + string + " found.");
    }


    @Override
    public String toString() {
        return value;
    }
}
