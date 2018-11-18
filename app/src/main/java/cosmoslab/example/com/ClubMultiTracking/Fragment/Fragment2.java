package cosmoslab.example.com.ClubMultiTracking.Fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cosmoslab.example.com.ClubMultiTracking.Activity.ContentActivity;
import cosmoslab.example.com.ClubMultiTracking.Etc.GlobalVariable;
import cosmoslab.example.com.ClubMultiTracking.Map.GPSTracker;
import cosmoslab.example.com.ClubMultiTracking.R;

public class Fragment2 extends Fragment implements OnMapReadyCallback {

    Double la = 0.0;
    Double lo = 0.0;

    GPSTracker gps = null;
    public Handler mHandler;
    public static int RENEW_GPS = 1;
    public static int SEND_PRINT = 2;

    private View rootView;
    private MapView mapView;

    GlobalVariable globalVariable;
    private String locationUrl;
    private String addUserUrl;

    private Button btn_showLocation;
    private Button btn_addUser;
    private TextView tv_userList;
    private TextView tv_latitude;
    private TextView tv_longitude;

    private ArrayList arr_userMobile;
    private ArrayList arr_latitude;
    private ArrayList arr_longitude;

    GoogleMap mMap;

    ////    public Fragment2() {
////    }
//
//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//
//    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment2, container, false);

        resetFragment2();

        onStart();

        if (gps == null) {
            gps = new GPSTracker(getActivity(), mHandler);
        } else {
            gps.Update();
        }

        if (gps.canGetLocation()) {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            la = latitude;
            lo = longitude;
            String latitude_s = Double.toString(latitude);
            String longitude_s = Double.toString(longitude);

            transferLocation(locationUrl, latitude_s, longitude_s, globalVariable.getUserID());
        }

        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == RENEW_GPS) {
                    makeNewGpsService();
                }

            }
        };

        btn_addUser.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View view) {
                AddUserDialog();
            }
        });

        btn_showLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // create class object
                if (gps == null) {
                    gps = new GPSTracker(getActivity(), mHandler);
                } else {
                    gps.Update();
                }

                if (gps.canGetLocation()) {
                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();
                    String latitude_s = Double.toString(latitude);
                    String longitude_s = Double.toString(longitude);
                    la = latitude;
                    lo = longitude;

                    refresh();
                } else {
                    //위치 받을 수 없는경우
                    gps.showSettingsAlert();
                }
            }
        });

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        return rootView;
    }

    public void makeNewGpsService() {
        if (gps == null) {
            gps = new GPSTracker(getActivity(), mHandler);
        } else {
            gps.Update();
        }

    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    //GoogleMap googleMap
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        MapsInitializer.initialize(this.getActivity());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(la, lo), 14);
        googleMap.animateCamera(cameraUpdate);

        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.mylocation);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, 50, 50, false);

        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(la, lo))
                .title("내위치")
                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
        );
    }

    private void refresh() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }

    //Fragment2 초기화 메소드
    private void resetFragment2() {
        globalVariable = (GlobalVariable) getActivity().getApplication();
        locationUrl = globalVariable.getIpUrl() + "/TestLogin/Location.jsp";
        addUserUrl = globalVariable.getIpUrl() + "/TestLogin/AddUser.jsp";

        arr_userMobile = new ArrayList();
        arr_latitude = new ArrayList();
        arr_longitude = new ArrayList();

        tv_userList = (TextView) rootView.findViewById(R.id.textview_user);
        tv_latitude = (TextView) rootView.findViewById(R.id.textview_latitude);
        tv_longitude = (TextView) rootView.findViewById(R.id.textview_longitude);

        btn_showLocation = (Button) rootView.findViewById(R.id.btnShowLocation);
        btn_addUser = (Button) rootView.findViewById(R.id.btnAddUser);

        mapView = (MapView) rootView.findViewById(R.id.mapview);
    }

    //자신의 위치정보 보내는 메소드
    private void transferLocation(String locationUrl, final String latitude, final String longitude, final String userId) {

        RequestQueue requestQueue = new Volley().newRequestQueue(getActivity());

        StringRequest postRequest = new StringRequest(Request.Method.POST, locationUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject jsonObjectInfo = jsonObject.getJSONObject("Content");

                    if (jsonObjectInfo.getInt("success") == 1) {
                        Intent intent = new Intent(getActivity(), ContentActivity.class);
                        intent.putExtra("content1", jsonObjectInfo.getString("content"));
                        intent.putExtra("name1", jsonObjectInfo.getString("name"));
                        intent.putExtra("title1", jsonObjectInfo.getString("title"));

                        startActivity(intent);//메인으로 전달
                    } else

                        Toast.makeText(getActivity(), jsonObjectInfo.getString("message"), Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
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
                param.put("la", latitude);
                param.put("lo", longitude);
                param.put("id", userId);

                return param;
            }
        };

        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);

        requestQueue.add(postRequest);
    }

    //상대 정보를 추가하는 다이아로그
    private void AddUserDialog() {
        AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
        ad.setTitle("핸드폰 번호로 유저 추가");

        final EditText et = new EditText(getActivity());
        et.setInputType(InputType.TYPE_CLASS_NUMBER);
        ad.setView(et);

        ad.setPositiveButton("추가", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String mobile = et.getText().toString();

                if (globalVariable.getUserID().equals(mobile)) {
                    Toast.makeText(getActivity(), "자신의 번호는 추가할 수 없습니다", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "유저 추가", Toast.LENGTH_SHORT).show();
                    addUser(addUserUrl, mobile);
                }
                dialog.dismiss();
            }
        });

        ad.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        ad.show();
    }

    //서버로 유저를 추가하고 해당 유저의 정보를 받는 메소드
    private void addUser(String addUserUrl, final String mobile) {

        RequestQueue requestQueue = new Volley().newRequestQueue(getActivity());
        StringRequest postRequest = new StringRequest(Request.Method.POST, addUserUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject jsonObjectInfo = jsonObject.getJSONObject("user");

                    if (jsonObjectInfo.getString("status").equals("success")) {
                        if (!arr_userMobile.contains(mobile)) {
                            arr_userMobile.add(mobile);//핸드폰 번호 추가
                        }

                        String name = jsonObjectInfo.getString("name");
                        tv_userList.append("  " + name);

                        String latitude = jsonObjectInfo.getString("latitude");
                        arr_latitude.add(latitude);
                        //tv_latitude.setText(latitude);

                        String longitude = jsonObjectInfo.getString("longitude");
                        arr_longitude.add(longitude);
                        //tv_longitude.setText(longitude);

                        //마커 찍기
                        addLocationMarker();

                    } else {
                        Toast.makeText(getActivity(), "해당 유저가 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
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
                param.put("adduser", mobile);

                return param;
            }
        };

        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);

        requestQueue.add(postRequest);
    }

    private void addLocationMarker() {

        try {
            for (int idx = 0; idx < arr_userMobile.size(); idx++) {
                // 1. 마커 옵션 설정 (만드는 과정)
                Double la = Double.parseDouble(arr_latitude.get(idx).toString());
                Double lo = Double.parseDouble(arr_longitude.get(idx).toString());

                Toast.makeText(getActivity(), la.toString(), Toast.LENGTH_SHORT).show();
                Toast.makeText(getActivity(), lo.toString(), Toast.LENGTH_SHORT).show();

                MarkerOptions makerOptions = new MarkerOptions();
                makerOptions
                        .position(new LatLng(la, lo))
                        .title("마커" + idx); // 타이틀.

                // 2. 마커 생성 (마커를 나타냄)
                mMap.addMarker(makerOptions);
            }
        } catch(Exception e) {
            Toast.makeText(getActivity(), "마커 오류", Toast.LENGTH_SHORT).show();
        }
    }


}