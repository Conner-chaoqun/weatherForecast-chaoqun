package cn.pku.wuchaoqun.myweatherforecast;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.pku.wuchaoqun.bean.City;
import cn.pku.wuchaoqun.bean.ForecastWeather;
import cn.pku.wuchaoqun.bean.TodayWeatherInfo;
import cn.pku.wuchaoqun.util.NetUtil;

public class MainActivity extends AppCompatActivity {

    private String currentCityName;

    private static final int UPDATE_TODAY_WEATHER = 1;

    private ImageView myUpdate, myCitySelect;

    private TextView cityTv, timeTv, humidityTv, weekTodayTv, pmDataTv, pmQualityTv, temTv, temTodayTv, climateTv, windTv, titleCityTv;

    private ImageView weatherImg, pmImg;

    private Map<String,Integer> imgSrcForWeather = new HashMap<>();

    private Map<Integer,Integer> imgSrcForPm = new HashMap<>();

    private String cityCode;

    private SharedPreferences.Editor editor;

    private RecyclerView mRecyclerView;

    private RecyclerView.Adapter mAdapter;

    private RecyclerView.LayoutManager mLayoutManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences mySharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
        cityCode = mySharedPreferences.getString("now_city_code", "101010100");
        Log.d("now_city_code", "onCreate: "+cityCode);
        editor = mySharedPreferences.edit();
        requestWeatherByCode(cityCode);

        myUpdate = findViewById(R.id.title_update_img);


        myUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("myWeatherForecast", "onClick: " + cityCode);

                if (NetUtil.getNetworkState(MainActivity.this) != NetUtil.NETWORK_NONE) {
                    Log.d("myWeatherForecast", "网络ok！");
                    myUpdate.setClickable(false);
                    updateAnimation(MainActivity.this, myUpdate);
                    requestWeatherByCode(cityCode);
                } else {
                    Log.d("myWeatherForecast", "网络挂了！");
                    Toast.makeText(MainActivity.this, "网络未连接", Toast.LENGTH_SHORT).show();
                }

            }
        });

        myCitySelect = findViewById(R.id.title_city_img);
        myCitySelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SelectCity.class);
                intent.putExtra("cityName", currentCityName);
                //startActivity(intent);
                startActivityForResult(intent, 1);
            }
        });

        testNet();

        initView();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        editor.putString("now_city_code", cityCode);
        editor.commit();
        Log.d("now_city_code", "onDestroy: "+cityCode);
    }



    private void updateAnimation(Context context, ImageView update){
        Animation circle_anim = AnimationUtils.loadAnimation(context, R.anim.anim_round_rotate);
        LinearInterpolator interpolator = new LinearInterpolator();  //设置匀速旋转，在xml文件中设置会出现卡顿
        circle_anim.setInterpolator(interpolator);
        if (circle_anim != null) {
            update.startAnimation(circle_anim);  //开始动画
        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        if (requestCode == 1 && resultCode == RESULT_OK){
            cityCode = intent.getStringExtra("cityCode");
            String cityName = intent.getStringExtra("cityName");
            String province = intent.getStringExtra("province");
            Log.d("myWeatherForecast", "选择的城市代码是："+cityCode);
            if (NetUtil.getNetworkState(MainActivity.this) != NetUtil.NETWORK_NONE) {
                Log.d("myWeatherForecast", "网络ok！");

                requestWeatherByCode(cityCode);


            } else {
                Log.d("myWeatherForecast", "网络挂了！");
                Toast.makeText(MainActivity.this, "网络未连接", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_TODAY_WEATHER:
                    updateWeather((TodayWeatherInfo) msg.obj);
                    break;

                default:
                    break;
            }
        }
    };

    private void initImgWithWeather(){
        imgSrcForWeather.put("晴", R.drawable.biz_plugin_weather_qing);
        imgSrcForWeather.put("多云", R.drawable.biz_plugin_weather_duoyun);
        imgSrcForWeather.put("暴雪", R.drawable.biz_plugin_weather_baoxue);
        imgSrcForWeather.put("暴雨", R.drawable.biz_plugin_weather_baoyu);
        imgSrcForWeather.put("大暴雨", R.drawable.biz_plugin_weather_dabaoyu);
        imgSrcForWeather.put("大雪", R.drawable.biz_plugin_weather_daxue);
        imgSrcForWeather.put("大雨", R.drawable.biz_plugin_weather_dayu);
        imgSrcForWeather.put("雷阵雨", R.drawable.biz_plugin_weather_leizhenyu);
        imgSrcForWeather.put("雷阵雨与冰雹", R.drawable.biz_plugin_weather_leizhenyubingbao);
        imgSrcForWeather.put("沙尘暴", R.drawable.biz_plugin_weather_shachenbao);
        imgSrcForWeather.put("特大暴雨", R.drawable.biz_plugin_weather_tedabaoyu);
        imgSrcForWeather.put("雾", R.drawable.biz_plugin_weather_wu);
        imgSrcForWeather.put("小雪", R.drawable.biz_plugin_weather_xiaoxue);
        imgSrcForWeather.put("小雨", R.drawable.biz_plugin_weather_xiaoyu);
        imgSrcForWeather.put("阴", R.drawable.biz_plugin_weather_yin);
        imgSrcForWeather.put("雨夹雪", R.drawable.biz_plugin_weather_yujiaxue);
        imgSrcForWeather.put("阵雪", R.drawable.biz_plugin_weather_zhenxue);
        imgSrcForWeather.put("阵雨", R.drawable.biz_plugin_weather_zhenyu);
        imgSrcForWeather.put("中雪", R.drawable.biz_plugin_weather_zhongxue);
        imgSrcForWeather.put("中雨", R.drawable.biz_plugin_weather_zhongyu);
    }

    private void initImgWithPm(){
        imgSrcForPm.put(0, R.drawable.biz_plugin_weather_0_50);
        imgSrcForPm.put(1, R.drawable.biz_plugin_weather_51_100);
        imgSrcForPm.put(2, R.drawable.biz_plugin_weather_101_150);
        imgSrcForPm.put(3, R.drawable.biz_plugin_weather_151_200);
        imgSrcForPm.put(4, R.drawable.biz_plugin_weather_201_300);
        imgSrcForPm.put(5, R.drawable.biz_plugin_weather_201_300);
    }

    private void initView() {
        cityTv = findViewById(R.id.city_tv);
        titleCityTv = findViewById(R.id.title_city_tv);
        timeTv = findViewById(R.id.time_tv);
        humidityTv = findViewById(R.id.humidity_tv);
        weekTodayTv = findViewById(R.id.week_today_tv);
        pmDataTv = findViewById(R.id.pm_data_tv);
        pmQualityTv = findViewById(R.id.pm_quality_tv);
        temTv = findViewById(R.id.tem_tv);
        temTodayTv = findViewById(R.id.tem_today_tv);
        climateTv = findViewById(R.id.climate_tv);
        windTv = findViewById(R.id.wind_tv);

        weatherImg = findViewById(R.id.weather_img);
        pmImg = findViewById(R.id.pm_img);

        initImgWithWeather();
        initImgWithPm();

        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView = findViewById(R.id.day_week_weather);
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    private void updateWeather(TodayWeatherInfo info) {
        currentCityName = info.getCity();
        titleCityTv.setText(info.getCity() + "天气");
        cityTv.setText(currentCityName);
        timeTv.setText("今天" + info.getUpdateTime() + "发布");
        humidityTv.setText("湿度:" + info.getShidu());
        weekTodayTv.setText("今天" + info.getDate());

        temTv.setText("温度:" + info.getWendu() + "℃");
        temTodayTv.setText(info.getLow() + "~" + info.getHigh());
        climateTv.setText(info.getType());
        windTv.setText(info.getFengxiang() + " 风力:" + info.getFengli());

        weatherImg.setImageResource(imgSrcForWeather.get(info.getType()));
        if (info.getPm25() != null){
            pmImg.setImageResource(imgSrcForPm.get(Integer.parseInt(info.getPm25())/50));
            pmDataTv.setText(info.getPm25());
            pmQualityTv.setText(info.getQuality());
        }else {
            pmImg.setImageResource(R.drawable.biz_plugin_weather_0_50);
            pmDataTv.setText("未知");
            pmQualityTv.setText("未知");
        }

        mAdapter = new DayOfWeekForecastAdapter(info.getDayOfWeekForecastWeather(), imgSrcForWeather);
        mRecyclerView.setAdapter(mAdapter);

        myUpdate.clearAnimation();
        myUpdate.setClickable(true);
        Toast.makeText(this, "更新成功！", Toast.LENGTH_SHORT).show();
    }

    private void testNet() {
        if (NetUtil.getNetworkState(this) != NetUtil.NETWORK_NONE) {
            Log.d("myWeatherForecast", "网络ok！");
            Toast.makeText(MainActivity.this, "网络ok!", Toast.LENGTH_SHORT).show();
        } else {
            Log.d("myWeatherForecast", "网络挂了！");
            Toast.makeText(MainActivity.this, "网络未连接", Toast.LENGTH_SHORT).show();
        }
    }


    private void requestWeatherByCode(String cityCode) {
        final String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey=" + cityCode;
        Log.d("myWeatherForecast", address);
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                TodayWeatherInfo todayWeatherInfo = null;
                try {
                    URL url = new URL(address);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setReadTimeout(8000);
                    connection.setConnectTimeout(8000);
                    InputStream in = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String str;
                    while ((str = reader.readLine()) != null) {
                        response.append(str);
                        Log.d("myWeatherForecast", str);
                    }
                    String result = response.toString();
                    Log.d("myWeatherForecast", result);
                    todayWeatherInfo = dom4jPaeseXML(result);
                    if (todayWeatherInfo != null) {
                        Log.d("myWeatherForecast", todayWeatherInfo.toString());
                        Message msg = new Message();
                        msg.what = UPDATE_TODAY_WEATHER;
                        msg.obj = todayWeatherInfo;
                        myHandler.sendMessage(msg);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

    private TodayWeatherInfo dom4jPaeseXML(String xml) throws DocumentException {
        Boolean today = false;
        List<ForecastWeather> dayOfWeekForecastWeather = new ArrayList<>();
        TodayWeatherInfo info = new TodayWeatherInfo();
        Document document = DocumentHelper.parseText(xml);

        Element root = document.getRootElement();
        Element node = root.element("city");
        info.setCity(node.getText());
        info.setUpdateTime(root.element("updatetime").getText());
        info.setWendu(root.elementText("wendu"));
        info.setFengli(root.elementText("fengli"));
        info.setShidu(root.elementText("shidu"));
        info.setFengxiang(root.elementText("fengxiang"));
        node = root.element("environment");
        if (node!=null){
            info.setPm25(node.elementText("pm25"));
            info.setQuality(node.elementText("quality"));
        }
        node = root.element("forecast");
//        node = root.element("forecast").element("weather");
//        info.setDate(node.elementText("date"));
//        info.setHigh(node.elementText("high"));
//        info.setLow(node.elementText("low"));
//        info.setType(node.element("day").elementText("type"));
        for (Iterator<Element> it = node.elementIterator("weather");it.hasNext();){
            Element weather = it.next();
            if (!today){
                today = true;
                info.setDate(weather.elementText("date"));
                info.setHigh(weather.elementText("high"));
                info.setLow(weather.elementText("low"));
                info.setType(weather.element("day").elementText("type"));
            }else {
                ForecastWeather eachDayForecastWeather  = new ForecastWeather();
                eachDayForecastWeather.setDate(weather.elementText("date"));
                eachDayForecastWeather.setHigh(weather.elementText("high"));
                eachDayForecastWeather.setLow(weather.elementText("low"));
                eachDayForecastWeather.setType(weather.element("day").elementText("type"));
                eachDayForecastWeather.setFengxiang(weather.element("day").elementText("fengxiang"));
                dayOfWeekForecastWeather.add(eachDayForecastWeather);
            }
        }
        info.setDayOfWeekForcastWeather(dayOfWeekForecastWeather);
        return info;
    }

    private TodayWeatherInfo parseXML(String xml) {
        TodayWeatherInfo info = null;
        int fengxiangCount = 0;
        int fengliCount = 0;
        int dateCount = 0;
        int highCount = 0;
        int lowCount = 0;
        int typeCount = 0;
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = factory.newPullParser();
            xmlPullParser.setInput(new StringReader(xml));
            int getType = xmlPullParser.getEventType();
            Log.d("myWeatherForecast", "try to parseXML");
            while (getType != xmlPullParser.END_DOCUMENT) {
                switch (getType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;

                    case XmlPullParser.START_TAG:
                        if (xmlPullParser.getName().equals("resp")) {
                            info = new TodayWeatherInfo();
                        }
                        if (xmlPullParser.getName().equals("city")) {
                            getType = xmlPullParser.next();
                            info.setCity(xmlPullParser.getText());
                            Log.d("myWeatherForecast", "city: " + xmlPullParser.getText());
                        } else if (xmlPullParser.getName().equals("updatetime")) {
                            getType = xmlPullParser.next();
                            info.setUpdateTime(xmlPullParser.getText());
                            Log.d("myWeatherForecast", "updatetime: " + xmlPullParser.getText());
                        } else if (xmlPullParser.getName().equals("shidu")) {
                            getType = xmlPullParser.next();
                            info.setShidu(xmlPullParser.getText());
                            Log.d("myWeatherForecast", "shidu: " + xmlPullParser.getText());
                        } else if (xmlPullParser.getName().equals("wendu")) {
                            getType = xmlPullParser.next();
                            info.setWendu(xmlPullParser.getText());
                            Log.d("myWeatherForecast", "wendu: " + xmlPullParser.getText());
                        } else if (xmlPullParser.getName().equals("pm25")) {
                            getType = xmlPullParser.next();
                            info.setPm25(xmlPullParser.getText());
                            Log.d("myWeatherForecast", "pm25: " + xmlPullParser.getText());
                        } else if (xmlPullParser.getName().equals("quality")) {
                            getType = xmlPullParser.next();
                            info.setQuality(xmlPullParser.getText());
                            Log.d("myWeatherForecast", "quality: " + xmlPullParser.getText());
                        } else if (xmlPullParser.getName().equals("fengxiang") && fengxiangCount == 0) {
                            getType = xmlPullParser.next();
                            info.setFengxiang(xmlPullParser.getText());
                            Log.d("myWeatherForecast", "fengxiang: " + xmlPullParser.getText());
                            fengxiangCount += 1;
                        } else if (xmlPullParser.getName().equals("fengli") && fengliCount == 0) {
                            getType = xmlPullParser.next();
                            info.setFengli(xmlPullParser.getText());
                            Log.d("myWeatherForecast", "fengli: " + xmlPullParser.getText());
                            fengliCount += 1;
                        } else if (xmlPullParser.getName().equals("date") && dateCount == 0) {
                            getType = xmlPullParser.next();
                            info.setDate(xmlPullParser.getText());
                            Log.d("myWeatherForecast", "date: " + xmlPullParser.getText());
                            dateCount += 1;
                        } else if (xmlPullParser.getName().equals("high") && highCount == 0) {
                            getType = xmlPullParser.next();
                            info.setHigh(xmlPullParser.getText());
                            Log.d("myWeatherForecast", "high: " + xmlPullParser.getText());
                            highCount += 1;
                        } else if (xmlPullParser.getName().equals("low") && lowCount == 0) {
                            getType = xmlPullParser.next();
                            info.setLow(xmlPullParser.getText());
                            Log.d("myWeatherForecast", "low: " + xmlPullParser.getText());
                            lowCount += 1;
                        } else if (xmlPullParser.getName().equals("type") && typeCount == 0) {
                            getType = xmlPullParser.next();
                            info.setType(xmlPullParser.getText());
                            Log.d("myWeatherForecast", "type: " + xmlPullParser.getText());
                            typeCount += 1;
                        }

                    case XmlPullParser.END_TAG:
                        break;
                }
                getType = xmlPullParser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return info;
    }
}
