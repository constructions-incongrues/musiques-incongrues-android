package com.headbangers.mi.model;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RSSMessage implements Comparable<RSSMessage> {

    static SimpleDateFormat FORMATTER = new SimpleDateFormat(
            "EEE, dd MMM yyyy HH:mm:ss");
    private String title;
    private URL link;
    private String description;
    private String content;
    private Date date;

    public void setLink(String link) {
        try {
            this.link = new URL(link);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getContent() {
        return content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public URL getLink() {
        return link;
    }

    public void setLink(URL link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDate() {
        return FORMATTER.format(this.date);
    }

    public void setDate(String date) {
        // while (!date.endsWith("00")) {
        // date += "0";
        // }
        try {
            this.date = FORMATTER.parse(date.trim());
        } catch (ParseException e) {
            this.date = null;
        }
    }

    @Override
    public int compareTo(RSSMessage another) {
        if (another == null)
            return 1;
        return another.date.compareTo(date);
    }

}
