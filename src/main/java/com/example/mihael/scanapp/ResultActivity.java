package com.example.mihael.scanapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Intent intent = getIntent();
        boolean result = intent.getBooleanExtra("result", false);
        ImageView image = (ImageView)findViewById(R.id.result_img);
        TextView description = (TextView)findViewById(R.id.result_description);
        RelativeLayout background = (RelativeLayout)findViewById(R.id.background);

        if (result){
            image.setImageResource(R.drawable.valid);
            description.setText(R.string.result_valid);
            background.setBackgroundColor(0xFF3EC43A);
        }else {
            image.setImageResource(R.drawable.not_valid);
            description.setText(R.string.result_not_valid);
            background.setBackgroundColor(0xFFD84141);
        }
    }

    public void restart(View view){
        Intent newIntent = new Intent(ResultActivity.this, BarcodeScanActivity.class);
        ResultActivity.this.startActivity(newIntent);
        ResultActivity.this.finish();
    }
}
