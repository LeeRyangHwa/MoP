package cosmoslab.example.com.ClubMultiTracking.Etc;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Lee on 2018-05-22.
 * 모바일, 인터넷 연결의 유효성 확인
 */

public class Constants {

    //Email Validation pattern
    public static final String regEx = "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}\\b";

    //Mobile validation pattern
    //public static  final String mobregEx="^(\\+91[\\-\\s]?)?[0]?(91)?[789]\\d{9}$";
    public static  final String mobregEx="(01[016789])(\\d{3,4})(\\d{4})";


    public static boolean isOnline(Context applicationContext) {

        ConnectivityManager cm = (ConnectivityManager) applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnectedOrConnecting())
            return true;
        else
            return false;

    }

}