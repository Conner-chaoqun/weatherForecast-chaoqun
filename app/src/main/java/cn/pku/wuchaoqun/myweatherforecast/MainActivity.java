package cn.pku.wuchaoqun.myweatherforecast;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import cn.pku.wuchaoqun.util.NetUtil;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (NetUtil.getNetworkState(this) != NetUtil.NETWORK_NONE) {
            Log.d("myWeatherForecast", "网络ok！");
            Toast.makeText(MainActivity.this, "网络ok!", Toast.LENGTH_SHORT).show();
        }else {
            Log.d("myWeatherForecast", "网络挂了！");
            Toast.makeText(MainActivity.this, "网络未连接", Toast.LENGTH_SHORT).show();
        }

    }
}
