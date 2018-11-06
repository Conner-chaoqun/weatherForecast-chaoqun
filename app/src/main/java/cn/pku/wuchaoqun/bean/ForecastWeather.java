package cn.pku.wuchaoqun.bean;

public class ForecastWeather {
    private String date;
    private String high;
    private String low;
    private String type;
    private String fengxiang;

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

    public void setFengxiang(String fengxiang) {
        this.fengxiang = fengxiang;
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

    public String getFengxiang() {
        return fengxiang;
    }

    @Override
    public String toString() {
        return "ForcastWeather{" +
                "date='" + date + '\'' +
                ", high='" + high + '\'' +
                ", low='" + low + '\'' +
                ", type='" + type + '\'' +
                ", fengxiang='" + fengxiang + '\'' +
                '}';
    }
}
