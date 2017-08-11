package app.racdeveloper.com.bookmybook;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by Rachit on 4/28/2017.
 */

public class QueryPreferences {
    public static final String BOOK_INFO = "bookInfo";
    public static final String USER_NAME = "userName";

    public static void setBookInfo(Context context, String text){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(BOOK_INFO, text)
                .apply();
    }

    public static String getBookInfo(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(BOOK_INFO, null);
    }

    public static void setUserName(Context context, String text){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(USER_NAME, text)
                .apply();
    }

    public static String getUserName(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(USER_NAME, null);
    }

}
