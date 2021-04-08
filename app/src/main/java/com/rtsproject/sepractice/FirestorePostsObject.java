package com.rtsproject.sepractice;

public class FirestorePostsObject {
    private String Contents;
    private String Date;
    private String Email;
    private String Picture;
    private String Title;
    private int Type;
    private String Writer;
    FirestorePostsObject(){
        //empty constructor needed
    }
    FirestorePostsObject(String Contents, String Date, String Email, String Picture, String Title, int Type, String Writer){
        this.Contents = Contents;
        this.Date = Date;
        this.Email = Email;
        this.Picture = Picture;
        this.Title = Title;
        this.Type = Type;
        this.Writer = Writer;
    }
    public String getContents() {
        return Contents;
    }
    public String getDate() {
        return Date;
    }
    public String getEmail() {
        return Email;
    }
    public String getPicture() {
        return Picture;
    }
    public String getTitle() {
        return Title;
    }
    public int getType() {
        return Type;
    }
    public String getWriter() {
        return Writer;
    }
}
