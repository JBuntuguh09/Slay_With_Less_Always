package com.lonewolf.slay_with_less_always.Resources;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Settings {

    private final String PREF_NAME = "saved_preference";
    private final String pchecker = "check";
    private SharedPreferences sharedPreferences;
    private final String pIsAnnonymous = "annonymous";
    private final String LoggedIn = "always logged in";
    private final String UserName = "username";
    private final String Email = "email";
    private final String Role = "role perm";
    private final String paths ="pic paths";

    public Settings(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
    }

    public Editor getEditor() {
        return sharedPreferences.edit();
    }

    public SharedPreferences getPref() {
        return sharedPreferences;
    }

    public boolean getLoggedIn(){
        return sharedPreferences.getBoolean(LoggedIn, false);
    }


    public void setLoggedIn(boolean loggedIn){
        Editor editor = getEditor();
        editor.putBoolean(LoggedIn, loggedIn);
        editor.commit();
    }

    public String getUserName(){
        return sharedPreferences.getString(UserName, "");

    }

    public void setUserName(String userName){
        Editor editor = getEditor();
        editor.putString(UserName, userName);
        editor.commit();
    }


    public String getEmail(){
        return sharedPreferences.getString(Email, "");

    }

    public void setEmail(String email){
        Editor editor = getEditor();
        editor.putString(Email, email);
        editor.commit();
    }

    public String getRole(){
        return sharedPreferences.getString(Role, "");

    }

    public void setRole(String role){
        Editor editor = getEditor();
        editor.putString(Role, role);
        editor.commit();
    }

    public String getPaths(){
        return sharedPreferences.getString(paths, "");
    }

    public void setPaths(String path){
        Editor editor = getEditor();
        editor.putString(paths, path);
        editor.commit();
    }
}
