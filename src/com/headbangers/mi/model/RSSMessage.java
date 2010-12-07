package com.headbangers.mi.model;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RSSMessage implements Comparable<RSSMessage>, Serializable {

    private static final long serialVersionUID = 1984L;

    static SimpleDateFormat FORMATER = new SimpleDateFormat(
            "EEE, dd MMM yyyy HH:mm:ss");
    static SimpleDateFormat SIMPLE_FORMATER = new SimpleDateFormat(
            "yyyy-MM-dd");

    private String title;
    private URL link;
    private String description;
    private String content;
    private Date date;

    private String author;
    private URL enclosureLink;

    public void setLink(String link) {
        try {
            this.link = new URL(link);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setEnclosureLink(String link) {
        try {
            this.enclosureLink = new URL(link);
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
        this.title = title.replaceAll("#039;", "'");
    }

    public URL getLink() {
        return link;
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

    public String getDateAsString() {
        return FORMATER.format(this.date);
    }

    public String getSimpleFormatedDate() {
        if (this.date != null) {
            return SIMPLE_FORMATER.format(this.date);
        }
        return "???";
    }

    public Date getDate() {
        return this.date;
    }

    public void setDate(String date) {
        // while (!date.endsWith("00")) {
        // date += "0";
        // }
        try {
            this.date = FORMATER.parse(date.trim());
        } catch (ParseException e) {
            this.date = null;
        }
    }

    public URL getEnclosureLink() {
        return enclosureLink;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthor() {
        return author!=null?author:"L'incongru inconnu";
    }

    @Override
    public int compareTo(RSSMessage another) {
        if (another == null)
            return 1;
        return another.date.compareTo(date);
    }

}
