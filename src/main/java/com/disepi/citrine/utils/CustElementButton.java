package com.disepi.citrine.utils;

import cn.nukkit.form.element.Element;
import cn.nukkit.form.element.ElementButtonImageData;

public class CustElementButton extends Element {

    private String text = "";
    private ElementButtonImageData image;

    public CustElementButton(String text) {
        this.text = text;
    }

    public CustElementButton(String text, ElementButtonImageData image) {
        this.text = text;
        if (!image.getData().isEmpty() && !image.getType().isEmpty()) this.image = image;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public ElementButtonImageData getImage() {
        return image;
    }

    public void addImage(ElementButtonImageData image) {
        if (!image.getData().isEmpty() && !image.getType().isEmpty()) this.image = image;
    }

}