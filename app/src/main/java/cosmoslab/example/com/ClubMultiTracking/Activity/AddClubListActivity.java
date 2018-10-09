package cosmoslab.example.com.ClubMultiTracking.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cosmoslab.example.com.ClubMultiTracking.Etc.AddNewClub;
import cosmoslab.example.com.ClubMultiTracking.Etc.GlobalVariable;
import cosmoslab.example.com.ClubMultiTracking.R;

/**
 * Created by Lee on 2018-05-22.
 * 동호회 추가 Activity
 */

public class AddClubListActivity extends AppCompatActivity {

    private EditText edt_name, edt_introduce, edt_interest;
    Intent intent;

    //private static String MAKECLUB_URL = "http://54.180.67.243/TestLogin/PutClubInfo.jsp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addlist);
        setTitle("동호회 추가");
        edt_name = (EditText) findViewById(R.id.nameinput);
        edt_interest = (EditText) findViewById(R.id.interestinginput);
        edt_introduce = (EditText) findViewById(R.id.introduceinput);
    }

    public void onClick(View v) {

        if (v.getId() == R.id.MakeButton) {
            add();

        } else if (v.getId() == R.id.CancelButton) {
            setResult(RESULT_CANCELED, intent);
            finish();
        }
    }

    private void add() {
        Intent intent = getIntent();
        String name = edt_name.getText().toString();
        String interest = edt_interest.getText().toString();
        String introduce = edt_introduce.getText().toString();

        if (edt_name.getText().toString().getBytes().length <= 0)
            Toast.makeText(getApplicationContext(), "다시 입력해주세요", Toast.LENGTH_SHORT).show();
        else {
            AddNewClub mm = new AddNewClub(name, getTime(), interest, introduce);//DB에 넣을 정보
            intent.putExtra("Make_Meeting", mm);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    private String getTime() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String time = sdf.format(date);
        return time;
    }

}
