package ua.com.sinenko.examples.web.dto;

import java.io.Serializable;

public class InternalDto implements Serializable {
    private String param;

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    @Override
    public String toString() {
        return "Dto{" +
                "param='" + param + '\'' +
                '}';
    }
}
