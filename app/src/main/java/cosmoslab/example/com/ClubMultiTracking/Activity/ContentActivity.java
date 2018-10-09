package cosmoslab.example.com.ClubMultiTracking.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import cosmoslab.example.com.ClubMultiTracking.R;

public class ContentActivity extends AppCompatActivity {
    TextView tv_content,tv_name,tv_title,tv_black,tv_black1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        tv_content = (TextView) findViewById(R.id.c_textview);
        tv_name = (TextView) findViewById(R.id.n_textview);
        tv_title = (TextView) findViewById(R.id.t_textview);
        // ------------------ 구분선 텍스트뷰 -----
        tv_black = (TextView) findViewById(R.id.black_textview);
        tv_black1 = (TextView) findViewById(R.id.black1_textview);
        tv_black.setBackgroundColor(Color.BLACK);
        tv_black1.setBackgroundColor(Color.BLACK);
        // ------------------ 구분선 텍스트뷰 -----
        Intent intent = getIntent();
        String content = intent.getExtras().getString("content1");
        String name = intent.getExtras().getString("name1");
        String title = intent.getExtras().getString("title1"); //메인액티비티에서 쏜 title1이라는 키 값의 intent값을 가져와서 title 변수에 저장
        if (name != null) tv_name.setText(name);
        if (title != null) tv_title.setText(title);
        if (content != null) tv_content.setText(content);
    }
    public void onc_ButtonClicked(View view){ // 뒤로가기 버튼
        Intent intent = new Intent(this,ClubMainActivity.class);/////////////////////////////////////////추후 수정
        startActivity(intent);
        finish();
    }
}

