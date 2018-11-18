package cosmoslab.example.com.ClubMultiTracking.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import org.json.JSONException;
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

import cosmoslab.example.com.ClubMultiTracking.Activity.ContentActivity;
import cosmoslab.example.com.ClubMultiTracking.Activity.WriteActivity;
import cosmoslab.example.com.ClubMultiTracking.Etc.GlobalVariable;
import cosmoslab.example.com.ClubMultiTracking.R;

public class Fragment4 extends Fragment {

    private ListView listView;
    private ArrayList<String> boardArr;
    private ArrayAdapter<String> adapter;

    private String url;
    private String url2;

    private String listviewTitle; // 리스트뷰 클릭했을 때 그 리스트뷰의 타이틀값을 알기 위한 변수

    private String ClubName;
    private GlobalVariable globalVariable;

    RecieveJson rJson;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment4, container, false);

        globalVariable = (GlobalVariable) getActivity().getApplication();
        url = globalVariable.getIpUrl() + "/TestLogin/BoardReceive.jsp";
        url2 = globalVariable.getIpUrl() + "/TestLogin/BoardMiddle.jsp";
        ClubName = globalVariable.getclubName();
        rJson = new RecieveJson();

        boardArr = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, boardArr);

        listView = (ListView) rootView.findViewById(R.id.listview_1); //xml로 존재하는 데이터를 ListView에 적용
        listView.setAdapter(adapter);
        listView.setDivider(new ColorDrawable(Color.BLUE));
        listView.setDividerHeight(2);
        //listView.setBackgroundColor(Color.rgb(178,204,255)); //배경색 바꿀 때
        //new RecieveJson().execute(url);

        ///////////////////////////////
        /*FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();*/

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //String sendTitle;
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) { // 리스트 뷰 클릭 이벤트
                listviewTitle = listView.getItemAtPosition(position).toString(); // 리스트뷰에 표시되있는 제목을 가져오기 (db에 접근해서 DB에 있는 제목과 비교하려고)
                Toast.makeText(getActivity(), listviewTitle, Toast.LENGTH_SHORT).show();
                sendTitle(url2, listviewTitle, ClubName);
            }
        });

        /*Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { // 필요 시
                Snackbar.make(view, "반갑습니다", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            }
        });

        Button write = (Button) rootView.findViewById(R.id.button1);
        write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent Myintent = new Intent(getActivity(), WriteActivity.class);
                startActivity(Myintent);
                Log.v("글쓰기버튼", "클릭됨");
            }
        });

        adapter.clear();
        return rootView;
    }

    public void onResume() {
        super.onResume();
        adapter.clear();
        rJson = new RecieveJson();
        //Toast.makeText(getActivity(), ClubName, Toast.LENGTH_SHORT).show();
        rJson.execute(url);
    }


    private class RecieveJson extends AsyncTask<String, Void, String> { // 메인 실행함수

        String sendMsg, page;

        @Override
        protected String doInBackground(String... strings) {

            HttpURLConnection conn = null;
            try {

                URL Url = new URL(url);
                conn = (HttpURLConnection) Url.openConnection();
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                conn.setRequestMethod("POST");
                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
                sendMsg = "clubname=" + ClubName;
                Log.i(sendMsg, "센메");
                osw.write(sendMsg);
                osw.flush();


                conn.setRequestMethod("GET");
                BufferedInputStream buf = new BufferedInputStream(conn.getInputStream());
                BufferedReader bufreader = new BufferedReader(new InputStreamReader(buf, "utf-8"));
                String line = null;
                page = "";

                while ((line = bufreader.readLine()) != null) {
                    page += line;
                }


            } catch (IOException e) {
                return "실패";
            }
            return page;
        }

        @Override
        protected void onPostExecute(String result) {

            try {
                Log.v("오류1", "오류1"); // 오류 점검 차 로그
                JSONObject json = new JSONObject(result); // 함수 사용 시 (result) 부분에 URL이 들어가
                Log.v("오류2", "오류2"); // 오류 점검 차 로그
                JSONArray jArr = json.getJSONArray("board"); // JSP에서 클럽이름board란 이름의 jsonarray 가져오기

                for (int i = 0; i < jArr.length(); i++) { // board의 길이만큼 반복하기
                    json = jArr.getJSONObject(i); //jarray의 i번째 jsonObject를 가져옴
                    String TITLE = json.optString("title", "text on no value"); // board 에서 TITLE
                    String CONTENT = json.optString("content", "text on no value");
                    Log.v("타이틀", TITLE);
                    Log.v("내용", CONTENT);

                    //TITLE을 ArrayList에 추가
                    boardArr.add(TITLE); // add(i,TITLE)로 인덱스 부여하고 for문을 int i = jArr.length(); i >= 0; i--
                    adapter.notifyDataSetChanged();//변경내용 반영
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void sendTitle(String loginUrl2, final String title, final String clubname) { // TITLE을 서버로 보내서 서버에 있는 타이틀값이랑 비교하기 위한 함수

        RequestQueue requestQueue = new Volley().newRequestQueue(getActivity());//Volley1. Context를 삽입

        //Volley2. 파라미터(request 타입,가져올 url,가져온 데이터가 성공했을 때 onResponse에 작업
        StringRequest postRequest = new StringRequest(Request.Method.POST, loginUrl2, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    //응답 받은거
                    JSONObject jsonObject = new JSONObject(response);//response: 서버에서 받은 전체 내용
                    JSONObject jsonObjectInfo = jsonObject.getJSONObject("Content"); //jsp에서 Content라는 키값을 가진 JSON 데이터 가져옴


                    if (jsonObjectInfo.getInt("success") == 1)//응답이 Success면
                    {

                        //Toast.makeText(getApplicationContext(), jsonObjectInfo.getString("message"), Toast.LENGTH_SHORT).show();

//                        JSONObject jsonObjectInfo=jsonObject.getJSONObject("User");//getJSONObject: User라는 key에 해당하는 것을 호출
                        //intent에 넣는다.
                        Intent intent = new Intent(getActivity(), ContentActivity.class);
                        intent.putExtra("content1", jsonObjectInfo.getString("content")); //jsp에서 content라는 키값을 가진 진짜 데이터값을 가져와서 content1이라는 키값으로 intent를 이용해 쏜다.
                        intent.putExtra("name1", jsonObjectInfo.getString("name"));
                        intent.putExtra("title1", jsonObjectInfo.getString("title"));

                        startActivity(intent);//메인으로 전달
                    } else//Success == 0 일때

                        /////////////////////////
                        Toast.makeText(getActivity(), jsonObjectInfo.getString("message"), Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    Toast.makeText(getActivity(), "JsonError", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "서버와의 연결이 원활하지 않습니다", Toast.LENGTH_SHORT).show();
                error.printStackTrace();

            }
        })


        {
            protected Map<String, String> getParams() {
                Map<String, String> param = new HashMap<String, String>();
                param.put("getTitle", title);
                param.put("getClubName", clubname);

                return param;
            }

        };

        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);

        //데이터를 파싱
        requestQueue.add(postRequest);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}