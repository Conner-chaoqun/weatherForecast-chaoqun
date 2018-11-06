package cn.pku.wuchaoqun.myweatherforecast;

import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import cn.pku.wuchaoqun.myweatherforecast.MyAdapter;
import cn.pku.wuchaoqun.app.MyApplication;
import cn.pku.wuchaoqun.bean.City;

public class SelectCity extends AppCompatActivity {

    private ImageView myBack;

    private TextView titleCityName;

    private List<City> list;

    private List<City> filterDataList;

    //private CityAdapter adapter;


    private RecyclerView mRecyclerView;

    private MyAdapter mAdapter;

    private RecyclerView.LayoutManager mLayoutManager;

    private SearchView mSearchView;


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
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        list = myApplication.getList();
        mAdapter = new MyAdapter(list);
        //adapter = new CityAdapter(this, R.layout.city_item, list);

    }



    private void initView() {
        titleCityName = findViewById(R.id.title_name_tv);
        Intent intent = getIntent();
        final String name = intent.getStringExtra("cityName");
        Log.d("intent", "initView: " + name );
        titleCityName.setText("当前城市：" + name);

        myBack = findViewById(R.id.title_back_img);
        myBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mAdapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                City city = list.get(position);
                Intent intent = new Intent();
                intent.putExtra("cityCode", city.getNumber());
                intent.putExtra("cityName", city.getCity());
                intent.putExtra("province",city.getProvince());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        mRecyclerView = findViewById(R.id.city_list);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        /*
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
        */

        mSearchView = findViewById(R.id.city_sv);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterData(newText);
                mRecyclerView.setAdapter(mAdapter);
                return true;
            }
        });





    }

    private void filterData(String newText) {
        filterDataList = new ArrayList<>();
        if (TextUtils.isEmpty(newText)) {
            mAdapter = new MyAdapter(list);
        }else {
            filterDataList.clear();
//            Log.d("首字母", " " + newText.toUpperCase());
            for (City c : list) {
                if (c.getCity().indexOf(newText.toString()) != -1) {
                    filterDataList.add(c);
                } else if (c.getAllPY().indexOf(newText.toUpperCase().toString()) == 0) {
                    filterDataList.add(c);
                } else if (c.getAllFirstPY().indexOf(newText.toUpperCase().toString()) == 0) {
                    filterDataList.add(c);
                }
            }
            mAdapter = mAdapter.updataView(filterDataList);
        }
        mAdapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                City city = filterDataList.get(position);
                Intent intent = new Intent();
                intent.putExtra("cityCode", city.getNumber());
                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }
}

