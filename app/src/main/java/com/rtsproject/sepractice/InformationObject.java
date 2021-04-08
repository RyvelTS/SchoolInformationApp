package com.rtsproject.sepractice;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

public class InformationObject {
    private String InfoTitle;
    private String InfoContent;
    private int preview;
    public InformationObject(int preview, String InfoTitle, String InfoContent){
        this.InfoTitle = InfoTitle;
        this.InfoContent = InfoContent;
        this.preview=preview;
    }
    public String getInfoTitle() {
        return InfoTitle;
    }
    public String getInfoContent() {
        return InfoContent;
    }

    public int getPreview() {
        return preview;
    }
}
