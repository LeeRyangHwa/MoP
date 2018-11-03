package cosmoslab.example.com.ClubMultiTracking.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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

import cosmoslab.example.com.ClubMultiTracking.Etc.GlobalVariable;
import cosmoslab.example.com.ClubMultiTracking.R;

public class Fragment1 extends Fragment {

    private TextView textView_introduce;
    private TextView textView_interest;
    private TextView textView_date;

    private String ClubName;
    private GlobalVariable globalVariable;
    private String clubinfo_url;

    private String[] clubinfo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment1, container, false);

        globalVariable = (GlobalVariable) getActivity().getApplication();
        ClubName = globalVariable.getclubName();
        clubinfo_url = globalVariable.getIpUrl() + "/TestLogin/MeetinglistInfo.jsp";

        textView_introduce = (TextView) rootView.findViewById(R.id.textview_one);
        textView_interest = (TextView) rootView.findViewById(R.id.textview_two);
        textView_date = (TextView) rootView.findViewById(R.id.textview_three);
        clubinfo = new String[3];

        getClubInfo(clubinfo_url, ClubName);

        textView_introduce.setText(clubinfo[0]);
        textView_interest.setText(clubinfo[1]);
        textView_date.setText(clubinfo[2]);

        return rootView;
    }

    private void getClubInfo(String clubInfo_url, final String club_name) {

        RequestQueue requestQueue = new Volley().newRequestQueue(getActivity());

        StringRequest postRequest = new StringRequest(Request.Method.POST, clubInfo_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getString("status").equals("success"))//응답이 Success면
                    {
                        String introduce = jsonObject.getString("introduce");
                        String interest = jsonObject.getString("interest");
                        String date = jsonObject.getString("date");

                        clubinfo[0] = introduce;
                        clubinfo[1] = interest;
                        clubinfo[2] = date;

                        textView_introduce.setText(clubinfo[0]);
                        textView_interest.setText(clubinfo[1]);
                        textView_date.setText(clubinfo[2]);

                    } else {
                        Toast.makeText(getActivity(), "응답 실패", Toast.LENGTH_SHORT).show();
                    }
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
                param.put("clubname", club_name);

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
