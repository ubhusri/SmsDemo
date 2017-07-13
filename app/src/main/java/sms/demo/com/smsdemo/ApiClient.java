package sms.demo.com.smsdemo;

import android.content.Context;
import android.widget.Toast;

import java.util.Random;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    public static String BASE_URL = "";
    private static Retrofit retrofit = null;

    public static boolean isBaseUrlEmpty(Context context) {
        BASE_URL = new SharedPrefHelper(context).getStringKey("base_url", "");
        if (BASE_URL.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    public static Retrofit getClient(Context context) {
        BASE_URL = new SharedPrefHelper(context).getStringKey("base_url", "");

        if (retrofit == null) {
            try {
                retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            }catch (IllegalArgumentException exc){
                Toast.makeText(context, "Kindly enter correct url with Http/Https", Toast.LENGTH_SHORT).show();
            }
        }

        return retrofit;
    }

    public static String genRandomNumber() {

        Random r = new Random(System.currentTimeMillis());
        return ((1 + r.nextInt(2)) * 1000000000 + r.nextInt(1000000000)) + "";

    }
}
