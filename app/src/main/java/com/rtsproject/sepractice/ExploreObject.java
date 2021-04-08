package com.rtsproject.sepractice;

public class ExploreObject {
    private String ExploreTitle;
    private String ExploreContent;
    private int preview;
    public ExploreObject(int preview, String ExploreTitle, String ExploreContent){
        this.preview=preview;
        this.ExploreTitle = ExploreTitle;
        this.ExploreContent = ExploreContent;
    }
    public String getExploreTitle() {
        return ExploreTitle;
    }
    public String getExploreContent() {
        return ExploreContent;
    }

    public int getPreview() {
        return preview;
    }
}
