package sl.com.lib.sharedpreferencesutil;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by shenlong on 08/11/2015.
 */
public class SharedPreferencesUtil {

    public static JSONObject GetJSONObject(Activity activity, String key)
    {
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        String str = sharedPref.getString(key, "");

        JSONObject res = null;
        try {
            res = new JSONObject(str);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return res;
    }
    public static void SetJSONObject (Activity activity, String key, JSONObject jsonObj)
    {
        Gson gson = new Gson();
        String jsonString = gson.toJson(jsonObj);
        SetJSONObject(activity, key,jsonObj.toString() );
    }
    public static void SetString (Activity activity, String key, String str)
    {

        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, str);
        editor.commit();
    }
    public static String GetString (Activity activity, String key)
    {

        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        String str = sharedPref.getString(key, "");
        return str;
    }
    private static void SetJSONObject (Activity activity, String key, String jsonString)
    {

        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, jsonString);
        editor.commit();
    }
}
