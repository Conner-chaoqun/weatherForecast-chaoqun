package cn.pku.wuchaoqun.bean;

import java.util.List;

public class TodayWeatherInfo {
    private String city;
    private String updateTime;
    private String wendu;
    private String shidu;
    private String pm25;
    private String quality;
    private String fengxiang;
    private String fengli;
    private String date;
    private String high;
    private String low;
    private String type;
    private List<ForecastWeather> dayOfWeekForecastWeather;

    public void setDayOfWeekForcastWeather(List<ForecastWeather> dayOfWeekForecastWeather) {
        this.dayOfWeekForecastWeather = dayOfWeekForecastWeather;
    }

    public List<ForecastWeather> getDayOfWeekForecastWeather() {

        return dayOfWeekForecastWeather;
    }

    public String getCity() {
        return city;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public String getWendu() {
        return wendu;
    }

    public String getShidu() {
        return shidu;
    }

    public String getPm25() {
        return pm25;
    }

    public String getQuality() {
        return quality;
    }

    public String getFengxiang() {
        return fengxiang;
    }

    public String getFengli() {
        return fengli;
    }

    public String getDate() {
        return date;
    }

    public String getHigh() {
        return high;
    }

    public String getLow() {
        return low;
    }

    public String getType() {
        return type;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public void setWendu(String wendu) {
        this.wendu = wendu;
    }

    public void setShidu(String shidu) {
        this.shidu = shidu;
    }

    public void setPm25(String pm25) {
        this.pm25 = pm25;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public void setFengxiang(String fengxiang) {
        this.fengxiang = fengxiang;
    }

    public void setFengli(String fengli) {
        this.fengli = fengli;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setHigh(String high) {
        this.high = high;
    }

    public void setLow(String low) {
        this.low = low;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "TodayWeatherInfo{" +
                "city='" + city + '\'' +
                ", updateTime='" + updateTime + '\'' +
                ", wendu='" + wendu + '\'' +
                ", shidu='" + shidu + '\'' +
                ", pm25='" + pm25 + '\'' +
                ", quality='" + quality + '\'' +
                ", fengxiang='" + fengxiang + '\'' +
                ", fengli='" + fengli + '\'' +
                ", date='" + date + '\'' +
                ", high='" + high + '\'' +
                ", low='" + low + '\'' +
                ", type='" + type + '\'' +
                ", dayOfWeekForecastWeather=" + dayOfWeekForecastWeather +
                '}';
    }
}
