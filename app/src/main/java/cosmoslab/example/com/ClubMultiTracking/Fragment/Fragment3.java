package cosmoslab.example.com.ClubMultiTracking.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

import cosmoslab.example.com.ClubMultiTracking.Activity.AddScheduleActivity;
import cosmoslab.example.com.ClubMultiTracking.Decorator.OneDayDecorator;
import cosmoslab.example.com.ClubMultiTracking.Decorator.EventDecorator;
import cosmoslab.example.com.ClubMultiTracking.Decorator.SaturdayDecorator;
import cosmoslab.example.com.ClubMultiTracking.Decorator.SundayDecorator;
import cosmoslab.example.com.ClubMultiTracking.Etc.GlobalVariable;
import cosmoslab.example.com.ClubMultiTracking.R;

public class Fragment3 extends Fragment {

    ListView listview;
    ArrayList list ;
    String result, resultday;
    String resultout[];
    String redayout[];

    private final Calendar calendar = Calendar.getInstance();
    private final OneDayDecorator oneDayDecorator = new OneDayDecorator();
    int YY = calendar.YEAR;
    int YDay = calendar.MONTH;
    CalendarDay DDay = CalendarDay.today();

    String Yea,Mon;
    String reday;

    int yy;
    int mm;
    int dd;

    private String ClubName;
    private GlobalVariable globalVariable;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment3, container, false);

        MaterialCalendarView materialCalendarView = (MaterialCalendarView) rootView.findViewById(R.id.calendarView);

        globalVariable = (GlobalVariable) getActivity().getApplication();
        ClubName = globalVariable.getclubName();

        materialCalendarView.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setMinimumDate(CalendarDay.from(2017, 0, 1))
                .setMaximumDate(CalendarDay.from(2030, 11, 31))
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

        try {
            CustomTask1 task = new CustomTask1();
            Yea = String.valueOf(YY);
            Mon = String.valueOf(YDay+1);

            reday = task.execute(Yea, Mon, ClubName).get();
            redayout=(reday.split("@"));

            for(int i=0;i<redayout.length;i++) {
                materialCalendarView.addDecorators(
                        new SundayDecorator(),
                        new SaturdayDecorator(),
                        new EventDecorator(redayout[i])
                );
            }

        } catch (Exception e) {
            Log.i("일정 불러오기", "실패입니다.");
        }
        materialCalendarView.addDecorators(
                oneDayDecorator
        );
        list = new ArrayList();
        listview = (ListView)rootView.findViewById(R.id.listview);

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_list_item_1, list);

        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                Toast.makeText(getActivity(), String.valueOf(date.getDate()), Toast.LENGTH_SHORT).show();
                yy = date.getYear();
                mm = date.getMonth() + 1;
                dd = date.getDay();

                Log.i("Year test", yy + "");
                Log.i("Month test", mm + "");
                Log.i("Day test", dd + "");


                try {
                    Log.i("확인", "여기까지 됩니다");
                    list.clear();
                    String sety = String.valueOf(yy);
                    String setm = String.valueOf(mm);
                    String setd = String.valueOf(dd);

                    int i=0;

                    Log.i("달", setm);
                    CustomTask task = new CustomTask();
                    Log.i("중간", ".....");
                    result = task.execute(sety, setm, setd, ClubName).get();


                    Log.i("result값", result);

                    resultout=(result.split("@"));
                    listview.setAdapter(adapter);

                    listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {



                        @Override
                        public void onItemClick(AdapterView<?> adapterView,
                                                View view, int position, long id) {

                            //클릭한 아이템의 문자열을 가져옴
                            String selected_item = (String)adapterView.getItemAtPosition(position);

                        }
                    });

                    for(int n=0; n< resultout.length;n++){
                        list.add(resultout[n]);

                        Log.i("리턴 값", resultout[n]);
                    }


                } catch (Exception e) {
                    Log.i("오류", "실패입니다.");
                }
            }

        });


        Button add = (Button) rootView.findViewById(R.id.addbutton);
        add.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View view) {

                if(yy <2000){
                    yy = DDay.getYear();
                    mm = DDay.getMonth()+1;
                    dd = DDay.getDay();
                }

                Intent intent = new Intent(getContext(), AddScheduleActivity.class);
                intent.putExtra("yy", yy);
                intent.putExtra("mm", mm);
                intent.putExtra("dd", dd);
                startActivity(intent);
            }
        });

        return rootView;
    }

    //날짜별로 일정 가져오는 메소드
    class CustomTask extends AsyncTask<String, Void, String> {
        String sendMsg, receiveMsg;

        @Override
        protected String doInBackground(String... strings) {
            try {
                String str;
                URL url = new URL(globalVariable.getIpUrl()+"/TestLogin/ScheduleSendData.jsp");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
                sendMsg = "sety=" + strings[0] + "&setm=" + strings[1] + "&setd=" + strings[2] + "&setclubname=" + strings[3];
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


    //
    class CustomTask1 extends AsyncTask<String, Void, String> {
        String sendMsg, receiveMsg;

        @Override
        protected String doInBackground(String... strings) {
            try {
                String str;
                URL url = new URL(globalVariable.getIpUrl()+"/TestLogin/ScheduleCalData.jsp");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
                sendMsg = "Yea=" + strings[0] + "&Mon=" + strings[1] + "&ClubName=" + strings[2];
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

    public void onResume()
    {
        super.onResume();
        ClubName = globalVariable.getclubName();
    }

}