package com.rtsproject.sepractice;

public class RegistrationObject {
    private String RegistrationTitle;
    private String RegistrationContent;
    private int preview;
    public RegistrationObject(int preview, String RegistrationTitle, String RegistrationContent){
        this.preview=preview;
        this.RegistrationTitle = RegistrationTitle;
        this.RegistrationContent = RegistrationContent;
    }
    public String getRegistrationTitle() {
        return RegistrationTitle;
    }
    public String getRegistrationContent() {
        return RegistrationContent;
    }

    public int getPreview() {
        return preview;
    }
}
