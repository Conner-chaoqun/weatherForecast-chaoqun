package cn.pku.wuchaoqun.myweatherforecast;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import cn.pku.wuchaoqun.bean.City;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{

    private List<City> list;

    private OnItemClickListener mOnItemClickListener;

    public MyAdapter updataView(List<City> filterDataList) {
        Collections.sort(filterDataList);
        return new MyAdapter(filterDataList);
    }


    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(MyAdapter.OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView cityProvince;
        TextView cityName;
        TextView cityCode;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cityProvince = itemView.findViewById(R.id.city_province);
            cityName = itemView.findViewById(R.id.city_name_tv);
            cityCode = itemView.findViewById(R.id.city_code_tv);
        }
    }

    public MyAdapter(List<City> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.city_item, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        viewHolder.cityProvince.setText(list.get(i).getProvince());
        viewHolder.cityName.setText(list.get(i).getCity());
        viewHolder.cityCode.setText(list.get(i).getNumber());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null){
                    mOnItemClickListener.onItemClick(v, i);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }


}
