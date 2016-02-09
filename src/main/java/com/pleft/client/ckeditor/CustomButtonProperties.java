package com.pleft.client.ckeditor;

import java.util.LinkedHashMap;
import java.util.Map;

public class CustomButtonProperties {

    private String title;
    private String icon;
    private Map<Object, Object> otherProperties = new LinkedHashMap<>();


    public CustomButtonProperties(String icon, Map<Object, Object> otherProperties, String title) {
        this.icon = icon;
        this.otherProperties = otherProperties;
        this.title = title;
    }

    public CustomButtonProperties(String title, String icon) {
        this.title = title;
        this.icon = icon;
    }

    public CustomButtonProperties() {}

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Map<Object, Object> getOtherProperties() {
        return otherProperties;
    }

    public void setOtherProperties(Map<Object, Object> otherProperties) {
        this.otherProperties = otherProperties;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
