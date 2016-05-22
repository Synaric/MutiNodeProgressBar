package com.synaric.mutinodeprogressbar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import android.widget.TextView;



import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<String> data = new ArrayList<>();
        data.add("已提交");
        data.add("已接货");
        data.add("已付款");
        data.add("已送达");
        MutiNodeProgressBar pb = (MutiNodeProgressBar) findViewById(R.id.pb_muti);
        if (pb != null) {
            pb.setAdapter(new SimpleMutiNodeAdapter<String>(data) {
                @Override
                public View getDescView(int position) {
                    TextView tv = new TextView(MainActivity.this);
                    tv.setText((CharSequence) getItem(position));
                    tv.setPadding(0, 50, 0, 50);
                    return tv;
                }
            });
        }
    }
}
