package sample;

import java.util.logging.Logger;

public class DataBean {
    private String info;

    public String getInfo() {
        return this.info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void close() {
        Logger.getAnonymousLogger().info("***** close : " + this);
    }
}