package cn.pku.wuchaoqun.myweatherforecast;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.HashMap;
import java.util.Map;

import cn.pku.wuchaoqun.bean.TodayWeatherInfo;
import cn.pku.wuchaoqun.util.NetUtil;

public class MainActivity extends AppCompatActivity {

    private static final int UPDATE_TODAY_WEATHER = 1;

    private ImageView myUpdate, myCitySelect;

    private TextView cityTv, timeTv, humidityTv, weekTodayTv, pmDataTv, pmQualityTv, temTv, temTodayTv, climateTv, windTv, titleCityTv;

    private ImageView weatherImg, pmImg;

    private Map<String,Integer> imgSrcForWeather = new HashMap<>();

    private Map<Integer,Integer> imgSrcForPm = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myUpdate = findViewById(R.id.title_update_img);
        myUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences mySharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
                String cityCode = mySharedPreferences.getString("main_city_code", "101010100");
                Log.d("myWeatherForecast", "onClick: " + cityCode);

                if (NetUtil.getNetworkState(MainActivity.this) != NetUtil.NETWORK_NONE) {
                    Log.d("myWeatherForecast", "网络ok！");
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
                //startActivity(intent);
                startActivityForResult(intent, 1);
            }
        });

        testNet();

        initView();

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        if (requestCode == 1 && resultCode == RESULT_OK){
            String newCityCode = intent.getStringExtra("cityCode");
            Log.d("myWeatherForecast", "选择的城市代码是："+newCityCode);
            if (NetUtil.getNetworkState(MainActivity.this) != NetUtil.NETWORK_NONE) {
                Log.d("myWeatherForecast", "网络ok！");
                requestWeatherByCode(newCityCode);
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

        imgSrcForWeather.put("晴", R.drawable.biz_plugin_weather_qing);
        imgSrcForWeather.put("多云", R.drawable.biz_plugin_weather_duoyun);
        imgSrcForPm.put(0, R.drawable.biz_plugin_weather_0_50);
        imgSrcForPm.put(1, R.drawable.biz_plugin_weather_51_100);
        imgSrcForPm.put(2, R.drawable.biz_plugin_weather_101_150);
        imgSrcForPm.put(3, R.drawable.biz_plugin_weather_151_200);
        imgSrcForPm.put(4, R.drawable.biz_plugin_weather_201_300);
        imgSrcForPm.put(5, R.drawable.biz_plugin_weather_201_300);
    }

    private void updateWeather(TodayWeatherInfo info) {
        titleCityTv.setText(info.getCity() + "天气");
        cityTv.setText(info.getCity());
        timeTv.setText("今天" + info.getUpdateTime() + "发布");
        humidityTv.setText("湿度:" + info.getShidu());
        weekTodayTv.setText("今天" + info.getDate());
        pmDataTv.setText(info.getPm25());
        pmQualityTv.setText(info.getQuality());
        temTv.setText("温度:" + info.getWendu() + "℃");
        temTodayTv.setText(info.getLow() + "~" + info.getHigh());
        climateTv.setText(info.getType());
        windTv.setText(info.getFengxiang() + " 风力:" + info.getFengli());

        weatherImg.setImageResource(imgSrcForWeather.get(info.getType()));
        pmImg.setImageResource(imgSrcForPm.get(Integer.parseInt(info.getPm25())/50));


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
                    todayWeatherInfo = parseXML(result);
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
