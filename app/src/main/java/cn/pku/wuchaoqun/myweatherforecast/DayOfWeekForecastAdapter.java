package cn.pku.wuchaoqun.myweatherforecast;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import cn.pku.wuchaoqun.bean.ForecastWeather;

public class DayOfWeekForecastAdapter extends RecyclerView.Adapter<DayOfWeekForecastAdapter.ViewHolder> {

    private List<ForecastWeather> list;
    private Map<String,Integer> imgSrcForWeather;

    public DayOfWeekForecastAdapter(List<ForecastWeather> list, Map<String,Integer> imgSrcForWeather) {
        this.list = list;
        this.imgSrcForWeather=imgSrcForWeather;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.day_week_weather_item, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        String type = list.get(i).getType();
        viewHolder.eachDayWeek.setText(list.get(i).getDate());
        viewHolder.eachDayTemperature.setText(list.get(i).getLow()+" ~ "+list.get(i).getHigh());
        viewHolder.eachDayClimate.setText(type);
        viewHolder.eachDayWind.setText(list.get(i).getFengxiang());
        viewHolder.eachDayType.setImageResource(imgSrcForWeather.get(type));
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView eachDayWeek;
        ImageView eachDayType;
        TextView eachDayTemperature;
        TextView eachDayClimate;
        TextView eachDayWind;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            eachDayWeek = itemView.findViewById(R.id.each_day_week);
            eachDayType = itemView.findViewById(R.id.each_day_type);
            eachDayTemperature = itemView.findViewById(R.id.each_day_temperature);
            eachDayClimate = itemView.findViewById(R.id.each_day_climate);
            eachDayWind = itemView.findViewById(R.id.each_day_wind);
        }
    }
}
