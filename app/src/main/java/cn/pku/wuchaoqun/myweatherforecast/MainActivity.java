package cn.pku.wuchaoqun.myweatherforecast;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
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
}
