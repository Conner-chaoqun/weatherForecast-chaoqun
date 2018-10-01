package cn.pku.wuchaoqun.myweatherforecast;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class SelectCity extends AppCompatActivity {

    private ImageView myBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_city);

        myBack = findViewById(R.id.title_back_img);
        myBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("cityCode", "101160101");
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
