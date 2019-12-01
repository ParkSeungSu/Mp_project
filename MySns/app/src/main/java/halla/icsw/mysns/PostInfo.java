package halla.icsw.mysns;

import java.util.ArrayList;
import java.util.Date;

public class PostInfo {
    private String title;
    private ArrayList<String>  Contents;
    private String publisher;
    private Date createdAt;

    public PostInfo(String title, ArrayList<String>  contents, String publisher, Date createdAt){
      this.title=title;
      this.Contents=contents;
      this.publisher=publisher;
      this.createdAt=createdAt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<String>  getContents() {
        return Contents;
    }

    public void setContents(ArrayList<String>  contents) {
        Contents = contents;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}