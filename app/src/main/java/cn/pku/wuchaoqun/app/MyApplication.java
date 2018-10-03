package cn.pku.wuchaoqun.app;

import android.app.Application;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import cn.pku.wuchaoqun.bean.City;
import cn.pku.wuchaoqun.db.CityDB;

public class MyApplication extends Application {

    private static final String TAG = "MyAPP";

    private static MyApplication mApp;

    private CityDB myCityDB;

    private List<City> myCitylist;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "MyApplication --> Oncreate");

        mApp = this;
        myCityDB = openCityDB();

        initCityList();
    }

    public List<City> getList(){
        return myCitylist;
    }

    private void initCityList() {
        myCitylist = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
               prepareCityList();
            }
        }).start();
    }

    private void prepareCityList() {
        myCitylist = myCityDB.getAllCity();
        int count = 0;
        for (City city : myCitylist) {
            count += 1;
            String cityName = city.getCity();
            String cityCode = city.getNumber();
            Log.d(TAG, cityCode + " : " + cityName +"  " + count);
        }
        Log.d(TAG, "count = " + count);
    }




    public static MyApplication getInstance() {
        return mApp;
    }

    private CityDB openCityDB() {
        String path = "/data" + Environment.getDataDirectory().getAbsolutePath()
                + File.separator + getPackageName() + File.separator + "databases1"
                + File.separator + CityDB.CITY_DB_NAME;
        File db = new File(path);
        Log.d(TAG, path);
        if (!db.exists()) {
            String pathfolder = "/data"
                    + Environment.getDataDirectory().getAbsolutePath()
                    + File.separator + getPackageName()
                    + File.separator + "databases1"
                    + File.separator;


            File dirFirstFolder = new File(pathfolder);
            if (!dirFirstFolder.exists()) {
                dirFirstFolder.mkdirs();
                Log.d(TAG, "mkdirs");
            }
            Log.i(TAG, "db is not exits");
            try {
                InputStream in = getAssets().open("city.db");
                FileOutputStream out = new FileOutputStream(db);
                int len = -1;
                byte[] buffer = new byte[1024];
                while ((len = in.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                    out.flush();
                }
                in.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new CityDB(this, path);

    }
}
