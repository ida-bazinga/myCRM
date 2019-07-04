package com.haulmont.thesis.crm.gui.schedulecharts.model.configurations;

import java.io.Serializable;

public abstract class AbstractConfiguration<T extends AbstractConfiguration> implements Serializable {

    private String color;
    private String fontFamily;
    private Integer fontSize;

    //todo refactor to colors for lines
    public String getColor() {
        return color;
    }

    public T setColor(String color) {
        this.color = color;
        return (T) this;
    }

    public String getFontFamily() {
        return fontFamily;
    }

    public T setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
        return (T) this;
    }

    public Integer getFontSize() {
        return fontSize;
    }

    public T setFontSize(Integer fontSize) {
        this.fontSize = fontSize;
        return (T) this;
    }
}
