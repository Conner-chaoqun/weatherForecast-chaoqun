package cn.pku.wuchaoqun.myweatherforecast;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import cn.pku.wuchaoqun.bean.City;

public class CityAdapter extends ArrayAdapter<City> {

    private int resourceId;
    public CityAdapter(@NonNull Context context, int resource, @NonNull List<City> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        City city = getItem(position);
        View view;
        ViewHolder viewHolder;

        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.cityName = view.findViewById(R.id.city_name_tv);
            viewHolder.cityCode = view.findViewById(R.id.city_code_tv);
            view.setTag(viewHolder);
        }else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.cityName.setText(city.getCity());
        viewHolder.cityCode.setText(city.getNumber());
        return view;
    }

    class ViewHolder{
        TextView cityName;
        TextView cityCode;
    }
}
