package sms.demo.com.smsdemo;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by umang.bhusri on 6/22/2017.
 */

public class SharedPrefHelper {

    SharedPreferences sharedPreferences;

    private SharedPrefHelper() {
    }

    public SharedPrefHelper(Context context) {
        sharedPreferences = context.getSharedPreferences("shared", Context.MODE_PRIVATE);
    }

    public void saveStringKey(String key, String value) {
        if (sharedPreferences != null) {
            sharedPreferences.edit().putString(key, value).apply();
        }

    }

    public String getStringKey(String key) {
        if (sharedPreferences != null) {
            return sharedPreferences.getString(key, "");
        }
        return "";

    }
    public String getStringKey(String key,String defKey) {
        if (sharedPreferences != null) {
            return sharedPreferences.getString(key,defKey);
        }else {
            return "";
        }

    }
}
