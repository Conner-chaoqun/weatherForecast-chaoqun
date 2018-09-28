package cn.pku.wuchaoqun.myweatherforecast;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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
import java.net.MalformedURLException;
import java.net.URL;

import cn.pku.wuchaoqun.util.NetUtil;

public class MainActivity extends AppCompatActivity {

    private ImageView myUpdate;

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
                Log.d("myWeatherForecast", "onClick: "+cityCode);

                if (NetUtil.getNetworkState(MainActivity.this) != NetUtil.NETWORK_NONE) {
                    Log.d("myWeatherForecast", "网络ok！");
                    requestWeatherByCode(cityCode);
                }else {
                    Log.d("myWeatherForecast", "网络挂了！");
                    Toast.makeText(MainActivity.this, "网络未连接", Toast.LENGTH_SHORT).show();
                }

            }
        });

        testNet();

    }

    private void testNet(){
        if (NetUtil.getNetworkState(this) != NetUtil.NETWORK_NONE) {
            Log.d("myWeatherForecast", "网络ok！");
            Toast.makeText(MainActivity.this, "网络ok!", Toast.LENGTH_SHORT).show();
        }else {
            Log.d("myWeatherForecast", "网络挂了！");
            Toast.makeText(MainActivity.this, "网络未连接", Toast.LENGTH_SHORT).show();
        }
    }

    private void requestWeatherByCode(String cityCode){
        final String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey=" + cityCode;
        Log.d("myWeatherForecast", address);
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
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
                    while ((str = reader.readLine()) != null){
                        response.append(str);
                        Log.d("myWeatherForecast", str);
                    }
                    String result = response.toString();
                    Log.d("myWeatherForecast", result);
                    parseXML(result);
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    if (connection != null){
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

    private void parseXML(String xml) {
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
            while (getType != xmlPullParser.END_DOCUMENT){
                switch (getType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;

                    case XmlPullParser.START_TAG:
                        if (xmlPullParser.getName().equals("city")) {
                            getType = xmlPullParser.next();
                            Log.d("myWeatherForecast", "city: "+xmlPullParser.getText());
                        }else if (xmlPullParser.getName().equals("updatetime")) {
                            getType = xmlPullParser.next();
                            Log.d("myWeatherForecast", "updatetime: "+xmlPullParser.getText());
                        }else if (xmlPullParser.getName().equals("shidu")) {
                            getType = xmlPullParser.next();
                            Log.d("myWeatherForecast", "shidu: "+xmlPullParser.getText());
                        }else if (xmlPullParser.getName().equals("wendu")) {
                            getType = xmlPullParser.next();
                            Log.d("myWeatherForecast", "wendu: "+xmlPullParser.getText());
                        }else if (xmlPullParser.getName().equals("pm25")) {
                            getType =xmlPullParser.next();
                            Log.d("myWeatherForecast", "pm25: "+xmlPullParser.getText());
                        }else if (xmlPullParser.getName().equals("quality")) {
                            getType = xmlPullParser.next();
                            Log.d("myWeatherForecast", "quality: "+xmlPullParser.getText());
                        }else if (xmlPullParser.getName().equals("fengxiang") && fengxiangCount == 0) {
                            getType = xmlPullParser.next();
                            Log.d("myWeatherForecast", "fengxiang: "+xmlPullParser.getText());
                            fengxiangCount += 1;
                        }else if (xmlPullParser.getName().equals("fengli") && fengliCount == 0) {
                            getType = xmlPullParser.next();
                            Log.d("myWeatherForecast", "fengli: "+xmlPullParser.getText());
                            fengliCount += 1;
                        }else if (xmlPullParser.getName().equals("date") && dateCount == 0) {
                            getType = xmlPullParser.next();
                            Log.d("myWeatherForecast", "date: "+xmlPullParser.getText());
                            dateCount += 1;
                        }else if (xmlPullParser.getName().equals("high") && highCount == 0) {
                            getType = xmlPullParser.next();
                            Log.d("myWeatherForecast", "high: "+xmlPullParser.getText());
                            highCount += 1;
                        }else if (xmlPullParser.getName().equals("low") && lowCount == 0) {
                            getType = xmlPullParser.next();
                            Log.d("myWeatherForecast", "low: "+xmlPullParser.getText());
                            lowCount += 1;
                        }else if (xmlPullParser.getName().equals("type") && typeCount == 0){
                            getType = xmlPullParser.next();
                            Log.d("myWeatherForecast", "type: "+xmlPullParser.getText());
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
    }
}
