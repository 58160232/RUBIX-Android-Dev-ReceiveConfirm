package com.example.wiroon.test1;


import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Rattasart on 07/09/2561.
 */

public final class Appconfig implements Serializable {
    public static String Serial;
    public static String CompanyCode;
    public static String CompanyName;
    public static String DBServerName;
    public static String DBName;
    public static String UserName;
    public static String Password;
    public static String ServiceURL;
    public static String URL_Data;
    public static String DCID;

    public static Context context;
    public static boolean connect = false;
    public static String CurrentUser;


    //Setting data for authen
    public static void setup(String ar){
        JSONObject tmp = null;
        try {
            tmp = new JSONObject(new JSONArray(ar).get(0).toString());
            Serial = tmp.getString("Serial");
            CompanyCode =tmp.getString("CompanyCode");
            CompanyName =tmp.getString("CompanyName");
            DBServerName =tmp.getString("DBServerName");
            DBName =tmp.getString("DBName");
            UserName =tmp.getString("UserName");
            Password =tmp.getString("Password");
            ServiceURL =tmp.getString("ServiceURL");
            URL_Data = "http://192.168.128.249/RUBIXBASEPOCWebAPI/";

            connect = true;
        } catch (JSONException e) {

        }
    }
    public static boolean checkstate(){
        if(Serial==null || CompanyCode==null || CompanyName==null || DBServerName==null || DBName==null
                || UserName==null || Password==null || ServiceURL==null || !connect )
            return false;
        else return true;
    }
    //set current user was login
    public static void setCurrentUser(String user){
        CurrentUser = user;
    }
    //get current user
    public static String getUser(){
        return CurrentUser;
    }
    //get URL
    public static String getURL(){
        return URL_Data;
    }

    public static void clear(){
        Serial ="";
    }
    //context
    public static void setContext(Context context){
        Appconfig.context = context;
    }

//    public static Context getContext(Context context){
//        return context;
//    }
    //serial
    public static String getSerial() {
        return Serial;
    }

    public static void setSerial(String DCID) {
        Appconfig.DCID = DCID;
    }
    //dcid
    public static String getDCID() {
        return DCID;
    }

    public static void setDCID(String Serial) {
        Appconfig.DCID = DCID;
    }


    public static void clearstate() {
        Serial =null;
        CompanyCode=null;
        CompanyName=null;
        DBServerName=null;
        DBName=null;
        UserName=null;
        Password=null;
        ServiceURL=null;
        CurrentUser=null;
        URL_Data=null;
    }

}