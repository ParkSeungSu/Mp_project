package halla.icsw.mysns;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PostInfo implements Serializable {
    private String title;
    private ArrayList<String>  contents;
    private ArrayList<String>  formats;
    private String publisher;
    private Date createdAt;
    private String id;

    public PostInfo(String title, ArrayList<String>  contents,ArrayList<String>  formats, String publisher, Date createdAt,String id){
      this.title=title;
      this.contents=contents;
      this.publisher=publisher;
      this.createdAt=createdAt;
      this.id=id;
      this.formats=formats;
    }
    public PostInfo(String title, ArrayList<String>  contents,ArrayList<String>  formats, String publisher, Date createdAt){
        this.title=title;
        this.contents=contents;
        this.publisher=publisher;
        this.createdAt=createdAt;
        this.formats=formats;

    }
    public Map<String,Object> getPostInfo(){
        Map<String, Object> docData=new HashMap<>();
        docData.put("title",title);
        docData.put("contents",contents);
        docData.put("formats",formats);
        docData.put("publisher",publisher);
        docData.put("createdAt",createdAt);
        return docData;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<String>  getContents() {
        return contents;
    }

    public void setContents(ArrayList<String>  contents) {
        this.contents = contents;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<String> getFormats() {
        return formats;
    }

    public void setFormats(ArrayList<String> formats) {
        this.formats = formats;
    }
}
