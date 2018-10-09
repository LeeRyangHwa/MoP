package cosmoslab.example.com.ClubMultiTracking.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cosmoslab.example.com.ClubMultiTracking.Etc.Constants;
import cosmoslab.example.com.ClubMultiTracking.Etc.GlobalVariable;
import cosmoslab.example.com.ClubMultiTracking.R;

/**
 * Created by Lee on 2018-05-22.
 * 회원가입 Activity
 */

public class SignupActivity extends AppCompatActivity {

    private EditText edt_name, edt_email, edt_mobile, edt_password;
    private Button btn_signup;
    private Button btn_movelogin;
    private String signup_url;

    CheckBox tAndC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getSupportActionBar().hide();

        GlobalVariable globalVariable = (GlobalVariable) getApplicationContext();
        signup_url = globalVariable.getIpUrl() + "/TestLogin/SignUp.jsp";

        edt_name =(EditText)findViewById(R.id.et_name);
        edt_email =(EditText)findViewById(R.id.et_email);
        edt_mobile=(EditText)findViewById(R.id.et_mobile);
        edt_password=(EditText)findViewById(R.id.et_password);
        btn_movelogin =(Button)findViewById(R.id.btn_login);

        //로그인 화면으로 돌아가기
        btn_movelogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SignupActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });

        tAndC=(CheckBox)findViewById(R.id.terms_conditions);

        //가입 버튼
        btn_signup =(Button)findViewById(R.id.btn_signup);
        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = edt_name.getText().toString();
                String userEmail = edt_email.getText().toString();
                String userMobile = edt_mobile.getText().toString();
                String userPassword= edt_password.getText().toString();

                // Pattern match for email id
                Pattern p = Pattern.compile(Constants.regEx);
                Matcher m = p.matcher(userEmail);


                //Pattern match for Mobile No
                Pattern mobi = Pattern.compile(Constants.mobregEx);
                Matcher mob = mobi.matcher(userMobile);


                if(userName.equalsIgnoreCase("")||userName.length() >=3 ||  userEmail.equalsIgnoreCase("")||userEmail.length() >=8 ||
                        userMobile.equalsIgnoreCase("")||userMobile.length()==10||userPassword.equalsIgnoreCase("")||userPassword.length() >= 5)
                {

                    if(userName.equalsIgnoreCase("")){
                        edt_name.setError("이름을 입력해주세요. ");
                    }

                    else if(userEmail.equalsIgnoreCase("")||!m.find()){
                        edt_email.setError("이메일을 입력해주세요. ");
                    }


                    else if(userMobile.equalsIgnoreCase("")||!mob.find()){
                        edt_mobile.setError("핸드폰 번호를 입력해주세요. ");
                    }

                    else if (userPassword.equalsIgnoreCase("")){
                        edt_password.setError("비밀번호를 입력해주세요. ");
                    }

                    else if(!tAndC.isChecked()){
                        Toast.makeText(getApplicationContext(),"약관에 동의해주세요.",Toast.LENGTH_LONG).show();

                    }

                    //입력이 모두 만족
                    else {
                        if(Constants.isOnline(getApplicationContext())){
                            register(signup_url, userName, userEmail,userMobile, userPassword);
                        } else {
                            Toast.makeText(getApplicationContext(), "인터넷을 연결해주세요.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(),"항목이 잘못되었습니다.",Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    //가입 메소드
    private void register(String _signup_url, final String getuserName, final String getuserEmail, final String getuserMobile, final String getuserPassword) {

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        StringRequest stringRequest  =new StringRequest(Request.Method.POST, _signup_url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response){
                try {

                    JSONObject jsonObject=new JSONObject(response);
                    JSONObject jsonObjectInfo=jsonObject.getJSONObject("UserSignUp");

                    if(jsonObjectInfo.getInt("success")==0)
                    {

                        Toast.makeText(getApplicationContext(),jsonObjectInfo.getString("message"),Toast.LENGTH_LONG).show();

                    }
                    else if(jsonObjectInfo.getInt("success")==1){
                        Toast.makeText(getApplicationContext(),jsonObjectInfo.getString("message"),Toast.LENGTH_LONG).show();
                        Intent intent=new Intent(SignupActivity.this, LoginActivity.class);
                        startActivity(intent);

                    }

                    else
                        Toast.makeText(getApplicationContext(),jsonObjectInfo.getString("message"),Toast.LENGTH_LONG).show();
                }
                catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("JSON Parser", "Error parsing data " + e.toString());
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();


            }
        })

         //Android->Jsp로 전송
        {
            @Override
            protected Map<String, String> getParams() {

            Map<String, String> param = new HashMap<String, String>();

            param.put("name",getuserName);
            param.put("email", getuserEmail);
            param.put("mobile", getuserMobile);
            param.put("password", getuserPassword);
            return param;
            }
        };

        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);

        //파싱된 데이터 전송
        requestQueue.add(stringRequest);

    }
}