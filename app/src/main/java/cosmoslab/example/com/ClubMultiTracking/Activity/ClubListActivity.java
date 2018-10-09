package cosmoslab.example.com.ClubMultiTracking.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cosmoslab.example.com.ClubMultiTracking.Etc.AddNewClub;
import cosmoslab.example.com.ClubMultiTracking.Etc.GlobalVariable;
import cosmoslab.example.com.ClubMultiTracking.R;

import static java.lang.System.arraycopy;

/**
 * Created by Lee on 2018-05-22.
 * 동호회 리스트 Activity
 */

public class ClubListActivity extends AppCompatActivity {

    MMAdapter adapter;//리스트뷰 추가됐을 경우
    int Addition = 1;
    private ArrayList<AddNewClub> storage = new ArrayList<>();
    private ListView lv;
    private EditText editSearch;

    GlobalVariable globalVariable;
    private String url2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clublist);

        globalVariable = (GlobalVariable) getApplicationContext();
        String url = globalVariable.getIpUrl() + "/TestLogin/MeetinglistSendData.jsp";
        url2 = globalVariable.getIpUrl() + "/TestLogin/MyPage_addClub.jsp";

        new RecieveJson().execute(url);
        storage = new ArrayList<AddNewClub>();
        setListView();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ClubListActivity.this, AddClubListActivity.class);
                startActivityForResult(intent, Addition);
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) { // 리스트 뷰 클릭 이벤트
                String listviewTitle = storage.get(position).getName().toString();
                Toast.makeText(getApplicationContext(), listviewTitle, Toast.LENGTH_SHORT).show();

                //전역변수로 넣기
                globalVariable.setclubName(listviewTitle);

                boolean check = false;

                for (int i = 0; i < globalVariable.getMyClubLIst().length; i++) {
                    if (globalVariable.getMyClubLIst()[i].equals(listviewTitle)) {
                        check = true;
                        break;
                    }
                }

                if (check == true) {
                    //ClubMain으로 이동
                    Intent intent = new Intent(getApplicationContext(), ClubMainActivity.class);
                    startActivity(intent);
                } else {
                    //회원가입으로 이동
                    clubSignUpDialog();
                }
            }
        });

        editSearch = (EditText) findViewById(R.id.editSearch);
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String searchText = editSearch.getText().toString();
                adapter.searchList(searchText);
            }
        });

    }


    public void clubSignUpDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("동호회 가입");
        builder.setMessage("이 동호회에 가입하시겠습니까?");
        builder.setPositiveButton("예",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        boolean check = false;
                        //가입된 정보를 넣고
                        for (int i = 0; i < globalVariable.getMyClubLIst().length; i++) {
                            Log.d("나와라", globalVariable.getclubName());

                            if (globalVariable.getMyClubLIst()[i].equals("null")) {
                                Log.d("아스날dwdwdw2", globalVariable.getclubName());
                                globalVariable.getMyClubLIst()[i] = globalVariable.getclubName();
                                Toast.makeText(getApplicationContext(), "동호회에 가입되었습니다.", Toast.LENGTH_SHORT).show();
                                check = true;
                                //jsp로 전송
                                sendNewClubName(url2, i + 1, globalVariable.getUserID(), globalVariable.getclubName());
                                break;
                            }
                        }

                        if (check == false) {
                            Toast.makeText(getApplicationContext(), "4개의 동호회에 가입되어 있습니다.", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

        builder.setNegativeButton("아니오",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "가입을 취소하였습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
        builder.show();
    }

    //메인 실행함수
    private class RecieveJson extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String url = globalVariable.getIpUrl() + "/TestLogin/MeetinglistSendData.jsp";

            try {
                return (String) downloadUrl((String) url);
            } catch (IOException e) {
                return "실패";
            }
        }

        private String downloadUrl(String myurl) throws IOException {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(myurl);
                conn = (HttpURLConnection) url.openConnection();
                Log.v("커넥트 시작", myurl);
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("GET");
                BufferedInputStream buf = new BufferedInputStream(conn.getInputStream());
                BufferedReader bufreader = new BufferedReader(new InputStreamReader(buf, "UTF-8"));
                String line = null;
                String page = "";

                while ((line = bufreader.readLine()) != null) {
                    page += line;
                }
                return page;
            } finally {
                conn.disconnect();
            }
        }

        @Override
        protected void onPostExecute(String result) {

            try {
                //Log.v("오류1", "오류1"); // 오류 점검 차 로그
                JSONObject json = new JSONObject(result); // 함수 사용 시 (result) 부분에 URL이 들어가
                //Log.v("오류2", "오류2"); // 오류 점검 차 로그
                JSONArray jArr = json.getJSONArray("clublist"); // JSP에서 board란 이름의 jsonarray 가져오기

                for (int i = 0; i < jArr.length(); i++) { // board의 길이만큼 반복하기
                    json = jArr.getJSONObject(i); //jarray의 i번째 jsonObject를 가져옴

                    String TITLE = json.optString("title", "text on no value");
                    String DATE = json.optString("date", "text on no value");
                    String INTEREST = json.optString("interest", "text on no value");
                    String INTRODUCE = json.optString("introduce", "text on no value");
                    Log.v("타이틀", TITLE);
                    Log.v("내용", DATE);
                    AddNewClub tt = new AddNewClub(TITLE, DATE, INTEREST, INTRODUCE);

                    //TITLE을 ArrayList에 추가
                    storage.add(tt); // add(i,TITLE)로 인덱스 부여하고 for문을 int i = jArr.length(); i >= 0; i--
                    adapter.notifyDataSetChanged();//변경내용 반영
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void setListView() {
        lv = (ListView) findViewById(R.id.listview);
        adapter = new MMAdapter(storage, this);
        lv.setAdapter(adapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Addition) {
            if (resultCode == RESULT_OK) {
                AddNewClub mm = data.getParcelableExtra("Make_Meeting");
                storage.add(mm);
                adapter.notifyDataSetChanged();

                try {
                    String result;
                    CustomTask task = new CustomTask();
                    result = task.execute(mm.getName(), mm.getDate(), mm.getInterest(), mm.getIntroduce()).get();
                    Log.i("결과값", result);

                    Toast.makeText(getApplicationContext(), "동호회 생성", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "동호회 생성 실패.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "동호회 생성 안함", Toast.LENGTH_SHORT).show();
            }
        }
    }


    class CustomTask extends AsyncTask<String, Void, String> {
        String sendMsg, receiveMsg;

        @Override
        protected String doInBackground(String... strings) {
            try {
                String str;

                URL url = new URL(globalVariable.getIpUrl() + "/TestLogin/MeetinglistData.jsp");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
                sendMsg = "title=" + strings[0] + "&date=" + strings[1] + "&interest=" + strings[2] + "&introduce=" + strings[3];
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

    private void sendNewClubName(String Url, final int index, final String userID, final String newClubName) {

        RequestQueue requestQueue = new Volley().newRequestQueue(getApplicationContext());
        StringRequest postRequest = new StringRequest(Request.Method.POST, Url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                error.printStackTrace();

            }
        })

        {
            protected Map<String, String> getParams() {

                Map<String, String> param = new HashMap<String, String>();

                param.put("index", String.valueOf(index));
                param.put("userID", userID);
                param.put("newClubName", newClubName);

                return param;
            }

        };

        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        requestQueue.add(postRequest);
    }


}