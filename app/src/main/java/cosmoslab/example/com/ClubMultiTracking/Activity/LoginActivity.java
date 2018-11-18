package cosmoslab.example.com.ClubMultiTracking.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cosmoslab.example.com.ClubMultiTracking.Etc.Constants;
import cosmoslab.example.com.ClubMultiTracking.Etc.GlobalVariable;
import cosmoslab.example.com.ClubMultiTracking.R;

public class LoginActivity extends AppCompatActivity {

    private EditText edt_login, edt_password;
    private Button btn_movesingup;
    private Button btn_login;
    private CheckBox showPWD;
    private String login_url;
    private String ID;

    GlobalVariable globalVariable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        globalVariable = (GlobalVariable) getApplicationContext();
        login_url = globalVariable.getIpUrl() + "/TestLogin/User.jsp";

        edt_login = (EditText) findViewById(R.id.et_lnmobioe);
        edt_password = (EditText) findViewById(R.id.et_lnpassword);
        btn_movesingup = (Button) findViewById(R.id.btn_signup);
        showPWD = (CheckBox) findViewById(R.id.show_pwd);
        btn_login = (Button) findViewById(R.id.btn_login);


        //회원 가입
        btn_movesingup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

        //비밀번호 노출
        showPWD.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    edt_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                edt_password.setTransformationMethod(PasswordTransformationMethod.getInstance());

            }
        });

        //로그인
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Login_UserMob = edt_login.getText().toString();//모바일 텍스트
                String Login_UserPass = edt_password.getText().toString();//패스워드 텍스트
                ID = Login_UserMob;

                //두개의 텍스트 박스가 비었거나, 패스워드가 5글자 이상이면
                if (Login_UserMob.equalsIgnoreCase("") || Login_UserPass.equalsIgnoreCase("") || Login_UserPass.length() >= 5) {
                    if (Login_UserMob.equalsIgnoreCase("")) {
                        edt_login.setError("핸드폰 번호를 입력하세요");
                    } else if (Login_UserPass.equalsIgnoreCase("")) {
                        edt_password.setError("비밀번호를 입력하세요");
                    } else {
                        if (Constants.isOnline(getApplicationContext())) {
                            //로그인
//                            Toast.makeText(getApplicationContext(), "Login", Toast.LENGTH_SHORT).show();
                            login(login_url, Login_UserMob, Login_UserPass);
                        } else {
                            Toast.makeText(getApplicationContext(), "인터넷 연결이 원활하지 않습니다", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "항목이 잘못되었습니다", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    //로그인 구현
    private void login(String _login_url, final String login_userMob, final String login_userPass) {

        RequestQueue requestQueue = new Volley().newRequestQueue(getApplicationContext());//Volley1. Context를 삽입

        //Volley2. 파라미터(request 타입,가져올 url,가져온 데이터가 성공했을 때 onResponse에 작업
        StringRequest postRequest = new StringRequest(Request.Method.POST, _login_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    //응답 받은거
                    JSONObject jsonObject = new JSONObject(response);//response: 서버에서 받은 전체 내용
                    JSONObject jsonObjectInfo = jsonObject.getJSONObject("User");

                    /*JSONArray jArr = jsonObject.getJSONArray("User");

                    try {

                        String[] jsonName = {"success", "message", "name", "email", "moblie"};
                        String[][] parseredData = new String[jArr.length()][jsonName.length];
                        for (int i = 0; i < jArr.length(); i++) {
                            jsonObject = jArr.getJSONObject(i);
                            for (int j = 0; j < jsonName.length; j++) {
                                parseredData[i][j] = jsonObject.getString(jsonName[j]);
                            }

                        }
                    }catch (JSONException e){
                        e.printStackTrace();
                    }*/


                    if (jsonObjectInfo.getInt("success") == 1)//응답이 Success면
                    {

                        Toast.makeText(getApplicationContext(), jsonObjectInfo.getString("message"), Toast.LENGTH_SHORT).show();

//                        JSONObject jsonObjectInfo=jsonObject.getJSONObject("User");//getJSONObject: User라는 key에 해당하는 것을 호출
                        //intent에 넣는다.
                        Intent intent = new Intent(LoginActivity.this, MyPageActivity.class);
                        intent.putExtra("ID", ID);

                        globalVariable.setLoginCheck(true);
                        /*intent.putExtra("name",jsonObjectInfo.getString("name"));
                        intent.putExtra("email",jsonObjectInfo.getString("email"));
                        intent.putExtra("mobile",jsonObjectInfo.getString("mobile"));*/
                        startActivity(intent);//메인으로 전달
                    } else//Success == 0 일때

                        /////////////////////////
                        Toast.makeText(getApplicationContext(), jsonObjectInfo.getString("message"), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "JsonError", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "서버와의 연결이 원활하지 않습니다", Toast.LENGTH_SHORT).show();
                error.printStackTrace();

            }
        })

        {
            protected Map<String, String> getParams() {
                Map<String, String> param = new HashMap<String, String>();
                param.put("mobile", login_userMob);
                param.put("password", login_userPass);

                return param;
            }

        };

        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);

        //데이터를 파싱
        requestQueue.add(postRequest);

    }

}