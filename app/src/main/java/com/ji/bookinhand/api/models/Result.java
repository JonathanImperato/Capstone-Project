package com.ji.bookinhand.api.models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Result {

    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("publication_dt")
    @Expose
    private String publicationDt;
    @SerializedName("byline")
    @Expose
    private String byline;
    @SerializedName("book_title")
    @Expose
    private String bookTitle;
    @SerializedName("book_author")
    @Expose
    private String bookAuthor;
    @SerializedName("summary")
    @Expose
    private String summary;
    @SerializedName("uuid")
    @Expose
    private String uuid;
    @SerializedName("uri")
    @Expose
    private String uri;
    @SerializedName("isbn13")
    @Expose
    private List<String> isbn13 = null;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPublicationDt() {
        return publicationDt;
    }

    public void setPublicationDt(String publicationDt) {
        this.publicationDt = publicationDt;
    }

    public String getByline() {
        return byline;
    }

    public void setByline(String byline) {
        this.byline = byline;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public List<String> getIsbn13() {
        return isbn13;
    }

    public void setIsbn13(List<String> isbn13) {
        this.isbn13 = isbn13;
    }

}