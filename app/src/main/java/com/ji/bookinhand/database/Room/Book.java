package com.ji.bookinhand.database.Room;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;


@Entity(tableName = Book.TABLE_NAME)
public class Book {

    public static final String TABLE_NAME = "books";

    @PrimaryKey
    int _ID;

    @ColumnInfo(name = "title")
    String title;


    @ColumnInfo(name = "authors")
    String[] authors;


    @ColumnInfo(name = "publisher")
    String publisher;


    @ColumnInfo(name = "publishedDate")
    String publishedDate;


    @ColumnInfo(name = "industryIdentifiers")
    String industryIdentifiers;


    @ColumnInfo(name = "readingModes")
    String readingModes;


    @ColumnInfo(name = "pageCount")
    int pageCount;


    @ColumnInfo(name = "printType")
    String printType;


    @ColumnInfo(name = "categories")
    String[] categories;


    @ColumnInfo(name = "averageRating")
    Double averageRating;


    @ColumnInfo(name = "ratingsCount")
    int ratingsCount;


    @ColumnInfo(name = "maturityRating")
    int maturityRating;


    @ColumnInfo(name = "imageLinks")
    String imageLinks;


    @ColumnInfo(name = "language")
    String language;


    @ColumnInfo(name = "previewLink")
    String previewLink;


    @ColumnInfo(name = "infoLink")
    String infoLink;


    @ColumnInfo(name = "canonicalVolumeLink")
    String canonicalVolumeLink;


    @ColumnInfo(name = "description")
    String description;


    @ColumnInfo(name = "subtitle")
    String subtitle;

    public int get_ID() {
        return _ID;
    }

    public void set_ID(int _ID) {
        this._ID = _ID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String[] getAuthors() {
        return authors;
    }

    public void setAuthors(String[] authors) {
        this.authors = authors;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    public String getIndustryIdentifiers() {
        return industryIdentifiers;
    }

    public void setIndustryIdentifiers(String industryIdentifiers) {
        this.industryIdentifiers = industryIdentifiers;
    }

    public String getReadingModes() {
        return readingModes;
    }

    public void setReadingModes(String readingModes) {
        this.readingModes = readingModes;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public String getPrintType() {
        return printType;
    }

    public void setPrintType(String printType) {
        this.printType = printType;
    }

    public String[] getCategories() {
        return categories;
    }

    public void setCategories(String[] categories) {
        this.categories = categories;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public int getRatingsCount() {
        return ratingsCount;
    }

    public void setRatingsCount(int ratingsCount) {
        this.ratingsCount = ratingsCount;
    }

    public int getMaturityRating() {
        return maturityRating;
    }

    public void setMaturityRating(int maturityRating) {
        this.maturityRating = maturityRating;
    }

    public String getImageLinks() {
        return imageLinks;
    }

    public void setImageLinks(String imageLinks) {
        this.imageLinks = imageLinks;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getPreviewLink() {
        return previewLink;
    }

    public void setPreviewLink(String previewLink) {
        this.previewLink = previewLink;
    }

    public String getInfoLink() {
        return infoLink;
    }

    public void setInfoLink(String infoLink) {
        this.infoLink = infoLink;
    }

    public String getCanonicalVolumeLink() {
        return canonicalVolumeLink;
    }

    public void setCanonicalVolumeLink(String canonicalVolumeLink) {
        this.canonicalVolumeLink = canonicalVolumeLink;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }
}
