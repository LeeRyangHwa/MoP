package cosmoslab.example.com.ClubMultiTracking.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

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

/**
 * Created by Lee on 2018-05-22.
 * 일정 추가 Activity
 */

public class AddScheduleActivity extends Activity {

    private int fyy,yy1;
    private int fmm,mm1;
    private int fdd,dd1;

    private EditText EditTextTitle;
    private EditText EditTextContent;

    private GlobalVariable globalVariable;
    private String clubName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        Intent refIntent = this.getIntent();
        EditTextTitle = (EditText)findViewById(R.id.schetitle);
        EditTextContent = (EditText)findViewById(R.id.schcontent);

        globalVariable = (GlobalVariable) getApplication();
        clubName = globalVariable.getclubName();

        int yy = refIntent.getIntExtra("yy",-1);
        int mm = refIntent.getIntExtra("mm",-1);
        int dd = refIntent.getIntExtra("dd",-1);

        yy1 = yy;
        mm1 = mm;
        dd1 = dd;

        NumberPicker pickery = (NumberPicker)findViewById(R.id.yypicker);
        pickery.setMinValue(1970);
        pickery.setMaxValue(2030);
        pickery.setValue(yy);


        NumberPicker pickerm = (NumberPicker)findViewById(R.id.mmpicker);
        pickerm.setMinValue(1);
        pickerm.setMaxValue(12);
        pickerm.setValue(mm);



        NumberPicker pickerd = (NumberPicker)findViewById(R.id.ddpicker);
        pickerd.setMinValue(1);
        pickerd.setMaxValue(31);
        pickerd.setValue(dd);

        pickery.setOnScrollListener(onScrollListener);
        pickerm.setOnScrollListener(onScrollListener);
        pickerd.setOnScrollListener(onScrollListener);


        //버튼 클릭
        Button data = (Button) findViewById(R.id.sendData);
        data.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View view) {

                try {
                    if(fyy==0){
                        fyy=yy1;
                    }
                    if(fmm==0){
                        fmm=mm1;
                    }
                    if(fdd==0){
                        fdd = dd1;
                    }

                    String Dateyy = String.valueOf(fyy);
                    String Datemm = String.valueOf(fmm);
                    String Datedd = String.valueOf(fdd);
                    String Schtitle = EditTextTitle.getText().toString();
                    String Schcontent = EditTextContent.getText().toString();
                    String result;

                    CustomTask task = new CustomTask();
                    result = task.execute(Dateyy, Datemm, Datedd, Schtitle, Schcontent, clubName).get();
                    Log.i("리턴 값", result);

                    if(Schtitle.length() > 0) {
                        /*Intent intent = new Intent(getApplicationContext(), ClubMainActivity.class);//Fragment4로 이동하도록 구현
                        startActivity(intent);////메인화면으로 전환*/
                        /*Fragment4 fragment4 = new Fragment4();
                        Bundle bundle = new Bundle();
                        fragment4.setArguments(bundle);*/
                        finish();

                    }else
                        Toast.makeText(AddScheduleActivity.this, "다시 입력해주세요",0).show();
                } catch (Exception e) {
                    Log.i("오류", "실패입니다.");
                }
            }
        });
    }
    //날짜설정--------------------------------------------------------------------------
    NumberPicker.OnScrollListener onScrollListener = new NumberPicker.OnScrollListener() {
        @Override
        public void onScrollStateChange(NumberPicker view, int scrollState) {
            NumberPicker picker = view;

            if (scrollState == SCROLL_STATE_IDLE) {
                if(view.getId() == R.id.yypicker)
                    fyy = picker.getValue();
                else if(view.getId() == R.id.mmpicker)
                    fmm = picker.getValue();
                else if(view.getId() == R.id.ddpicker)
                    fdd = picker.getValue();
            }
        }
    };
    //전송-----------------------------------------------------------------------------
    class CustomTask extends AsyncTask<String, Void, String> {
        String sendMsg, receiveMsg;

        @Override
        protected String doInBackground(String... strings) {
            try {
                String str;
                URL url = new URL(globalVariable.getIpUrl() + "/TestLogin/ScheduleData.jsp");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
                conn.setRequestMethod("POST");
                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
                sendMsg = "Dateyy=" + strings[0] + "&Datemm=" + strings[1] + "&Datedd=" + strings[2] + "&Schtitle=" + strings[3] + "&Schcontent=" + strings[4] + "&Clubname=" + strings[5];
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
    /*public void Back(View v){
        *//*Intent intent = new Intent(this, ClubMainActivity.class);
        startActivity(intent);*//*
        Fragment4 fragment4 = new Fragment4();
        Bundle bundle = new Bundle();
        fragment4.setArguments(bundle);
    }*/

    public void Back(View v){
        //Intent intent = new Intent(this, ClubMainActivity.class);
        //startActivity(intent);
        finish();
    }

}
