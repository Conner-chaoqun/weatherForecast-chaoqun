package cn.pku.wuchaoqun.myweatherforecast;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import cn.pku.wuchaoqun.app.MyApplication;
import cn.pku.wuchaoqun.bean.City;

public class SelectCity extends AppCompatActivity {

    private ImageView myBack;

    private ListView myList;

    private CityAdapter adapter;

    private List<City> list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_city);

        //myBack = findViewById(R.id.title_back_img);
        /*
        myBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("cityCode", "101160101");
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        */

        initData();
        initView();

    }

    private void initData() {
        MyApplication myApplication = (MyApplication) getApplication();
        list = myApplication.getList();
        adapter = new CityAdapter(this, R.layout.city_item, list);

    }

    private void initView() {
        myBack = findViewById(R.id.title_back_img);
        myBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        myList = findViewById(R.id.city_list);
        myList.setAdapter(adapter);
        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                City city = list.get(position);
                Intent intent = new Intent();
                intent.putExtra("cityCode", city.getNumber());
                setResult(RESULT_OK, intent);
                finish();
            }
        });






    }
}
