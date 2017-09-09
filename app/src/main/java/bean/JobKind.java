package bean;

import java.io.Serializable;

/**
 * 创建日期：2017/8/30 on 10:38
 * 作者:孙明明
 * 描述:发布工作-招聘工种
 */

public class JobKind implements Serializable{

    private String name;
    private int count;
    private int money;
    private String startTime;
    private String stopTime;
    private boolean del;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getStopTime() {
        return stopTime;
    }

    public void setStopTime(String stopTime) {
        this.stopTime = stopTime;
    }

    public boolean isDel() {
        return del;
    }

    public void setDel(boolean del) {
        this.del = del;
    }

    @Override
    public String toString() {
        return "JobKind{" +
                "name='" + name + '\'' +
                ", count=" + count +
                ", money=" + money +
                ", startTime='" + startTime + '\'' +
                ", stopTime='" + stopTime + '\'' +
                ", del=" + del +
                '}';
    }
}
