package cosmoslab.example.com.ClubMultiTracking.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import cosmoslab.example.com.ClubMultiTracking.Etc.GlobalVariable;
import cosmoslab.example.com.ClubMultiTracking.R;

import static java.lang.System.arraycopy;

public class MyPageActivity extends AppCompatActivity {

    private ArrayList<String> club_name = new ArrayList<String>();
    private ListView listview;

    private String userID;
    private String IDinfo;
    private String userinfo[];
    private TextView tv_id, tv_mobile, tv_email, tv_setup, tv_clublist;

    private GlobalVariable globalVariable;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("마이페이지");

        globalVariable = (GlobalVariable) getApplicationContext();

        Intent refIntent = this.getIntent();
        userID = refIntent.getStringExtra("ID");//ID값 가져오기
        globalVariable.setUserID(userID);

        tv_id = (TextView) findViewById(R.id.IDtext);
        tv_mobile = (TextView) findViewById(R.id.mobile);
        tv_email = (TextView) findViewById(R.id.userEmail);
        //tv_setup = (TextView) findViewById(R.id.usersetup);
        tv_clublist = (TextView) findViewById(R.id.clublist);

        try {
            CustomTask task = new CustomTask();

            IDinfo = task.execute(userID).get();
            userinfo = (IDinfo.split("/"));

            tv_id.setText(userinfo[0]);
            tv_mobile.setText(globalVariable.getUserID());
            tv_email.setText(userinfo[2]);

            arraycopy(userinfo, 3, globalVariable.getMyClubLIst(), 0, 4);

            for (int i = 3; i < 7; i++) {
                Log.i("userinfo", userinfo[i]);
                if (userinfo[i].equals("null")) {
                    club_name.add("추가하기");
                } else {
                    club_name.add(userinfo[i]);
                }
            }
        } catch (Exception e) {
            Log.i("마이페이지 불러오기", "실패입니다.");
        }

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, club_name);

        listview = (ListView) findViewById(R.id.clubname);
        listview.setAdapter(adapter); //위에 만들어진 Adapter를 ListView에 설정 : xml에서 'entries'속성

        //ListView의 아이템 하나가 클릭되는 것을 감지하는 Listener객체 설정 (Button의 OnClickListener와 같은 역할)
        listview.setOnItemClickListener(listener);

        /*tv_setup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               *//* // TextView 클릭될 시 할 코드작성
                Intent intent2 = new Intent(getApplicationContext(), UserSet_Activity.class);//userinfo 이동
                intent2.putExtra("userID", userID);
                intent2.putExtra("name", String.valueOf(userinfo[0]));
                intent2.putExtra("hobby", String.valueOf(userinfo[1]));
                intent2.putExtra("userEmail", String.valueOf(userinfo[2]));
                startActivity(intent2);*//*
                // Toast.makeText(MyPageActivity.this, "준비중입니다.",0).show();
            }
        });*/


        tv_clublist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ClubListActivity.class);
                startActivity(intent);


            }
        });

    }

    AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            String selected_club = (String) listview.getItemAtPosition(position);

            if (selected_club.equals("추가하기")) {
                Intent intent = new Intent(getApplicationContext(), ClubListActivity.class);
                startActivity(intent);
            } else {
                globalVariable.setclubName(selected_club);
                Toast.makeText(getApplicationContext(), selected_club, Toast.LENGTH_SHORT).show();
                Intent intent1 = new Intent(getApplicationContext(), ClubMainActivity.class);
                startActivity(intent1);
            }

        }
    };


    class CustomTask extends AsyncTask<String, Void, String> {
        String sendMsg, receiveMsg;

        @Override
        protected String doInBackground(String... strings) {
            try {
                String str;
                String ipURL = globalVariable.getIpUrl() + "/TestLogin/MyPage.jsp";
                URL url = new URL(ipURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
                sendMsg = "IDinfo=" + strings[0];
                osw.write(sendMsg);
                osw.flush();
                if (conn.getResponseCode() == conn.HTTP_OK) {
                    InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                    BufferedReader reader = new BufferedReader(tmp);
                    StringBuffer buffer = new StringBuffer();
                    while ((str = reader.readLine()) != null) {
                        buffer.append(str);
                    }
                    receiveMsg = buffer.toString();
                } else {
                    Log.i("통신 결과", conn.getResponseCode() + "에러");
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return receiveMsg;
        }

    }

}
