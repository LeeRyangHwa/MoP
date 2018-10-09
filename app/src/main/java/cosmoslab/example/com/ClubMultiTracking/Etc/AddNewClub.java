package cosmoslab.example.com.ClubMultiTracking.Etc;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Lee on 2018-05-22.
 * 동호회 추가
 */

public class AddNewClub implements Parcelable {

    private String name;//동호회 이름
    private String date;//동호회 만든 날짜
    private String interest;//동호회 분야
    private String introduce;//동호회 소개


    public AddNewClub(AddNewClub mm) {
        this.name = mm.getName();
        this.date = mm.getDate();
        this.interest = mm.getInterest();
        this.introduce = mm.getIntroduce();
    }

    public AddNewClub(String _name, String _date, String _interest, String _introduce) {
        this.name = _name;
        this.date = _date;
        this.interest = _interest;
        this.introduce = _introduce;
    }

    public AddNewClub(Parcel in) {
        name = in.readString();
        date = in.readString();
        interest = in.readString();
        introduce = in.readString();
    }

    public static final Creator<AddNewClub> CREATOR = new Creator<AddNewClub>() {
        @Override
        public AddNewClub createFromParcel(Parcel in) {
            return new AddNewClub(in);
        }

        @Override
        public AddNewClub[] newArray(int size) {
            return new AddNewClub[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(date);
        parcel.writeString(interest);
        parcel.writeString(introduce);
    }

    public String getName() {
        return name;
    }

    public void setName(String _name) {
        this.name = _name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String _date) {
        this.date = _date;
    }

    public String getInterest() {
        return interest;
    }

    public void setInterest(String _interest) {
        this.interest = _interest;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String _introduce) {
        this.introduce = _introduce;
    }


}
