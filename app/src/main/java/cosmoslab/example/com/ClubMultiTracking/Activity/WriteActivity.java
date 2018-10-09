package cosmoslab.example.com.ClubMultiTracking.Activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import cosmoslab.example.com.ClubMultiTracking.Etc.GlobalVariable;
import cosmoslab.example.com.ClubMultiTracking.Fragment.Fragment4;
import cosmoslab.example.com.ClubMultiTracking.R;

public class WriteActivity extends AppCompatActivity {

    EditText et_id, et_pw, et_title, et_content;
    String sid, spw, stitle, scontent,cn;
    private String ClubName;
    private GlobalVariable globalVariable;

    public void onButton2Clicked(View view) { //완료 버튼 클릭

        sid = et_id.getText().toString();
        spw = et_pw.getText().toString();
        stitle = et_title.getText().toString();
        scontent = et_content.getText().toString();
        cn = ClubName;
       /* Intent Sintent = new Intent(this,Fragment5.class);
        Sintent.putExtra("타이틀",stitle);
        startActivity(Sintent);*/

        Fragment4 fragment4 = new Fragment4();
        Bundle bundle = new Bundle();
        fragment4.setArguments(bundle);


        CustomTask task = new CustomTask();
        task.execute();

        finish();


        // Toast.makeText(getApplicationContext(), "클릭됨", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        globalVariable = (GlobalVariable) this.getApplication();
        ClubName = globalVariable.getclubName();
        Log.i(ClubName,"clubname");

        et_id = (EditText) findViewById(R.id.et_id);
        et_pw = (EditText) findViewById(R.id.et_pw);
        et_title = (EditText) findViewById(R.id.et_title);
        et_content = (EditText) findViewById(R.id.et_content);

    }

    class CustomTask extends AsyncTask<String, Void, String> {
        String id, pw, title,content,clubname, sendMsg,sendMsg2, receiveMsg;

        @Override
        protected String doInBackground(String... strings) {
            try {
                String str;
                id = sid; //et_id EditText에 입력된 값을 getString 해온 sid 값을 id라는 지역변수에 넣음
                pw = spw;
                title = stitle;
                content = scontent;
                clubname = cn;
                //집 ip : 192.168.219.101 폰 핫스팟 : 172.20.10.3
                //URL url = new URL("http://172.20.10.3/Board/data.jsp");
                URL url = new URL(globalVariable.getIpUrl()+"/TestLogin/BoardData.jsp");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
                sendMsg = "id="+id+"&pw="+pw; // WriteActivity에 있는 EditText에 있는 텍스트들의 아이디값을
                osw.write(sendMsg);
                sendMsg2 = "&title="+title+"&content="+content+"&clubname="+clubname;
                osw.write(sendMsg2);
                osw.flush();
                Log.v("최종id",id);
                Log.v("최종pw",pw);
                Log.v("최종title",title);
                Log.v("최종content",content);
                Log.v("최종clubname",clubname);

                if (conn.getResponseCode() == conn.HTTP_OK) {
                    InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                    BufferedReader reader = new BufferedReader(tmp);
                    StringBuffer buffer = new StringBuffer();
                    while ((str = reader.readLine()) != null) {
                        buffer.append(str);
                    }
                    receiveMsg = buffer.toString();
                } else {
                    Log.i("통신결과", conn.getResponseCode() + "에러");
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