package cosmoslab.example.com.ClubMultiTracking.Etc;

import android.app.Application;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Lee on 2018-05-22.
 * 전역 변수
 */

public class GlobalVariable extends Application {

    private String clubName;//동호회 이름
    private String ipUrl = "http://192.168.0.7:8080";//ip주소
    private String userID;
    private String[] myClubLIst = new String[4];

    public String getclubName() {
        return clubName;
    }

    public void setclubName(String _clubName) {
        this.clubName = _clubName;
    }

    public String getIpUrl() {
        return ipUrl;
    }

    public void setIpUrl(String _ipUrl) {
        this.ipUrl = _ipUrl;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String _userID) {
        this.userID = _userID;
    }

    public String[] getMyClubLIst() {
        return myClubLIst;
    }

    public void setMyClubLIst(String[] myClubLIst) {
        this.myClubLIst = myClubLIst;
    }
}

