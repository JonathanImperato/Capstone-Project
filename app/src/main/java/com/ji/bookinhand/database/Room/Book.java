package com.ji.bookinhand.database.Room;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.content.ContentValues;
import android.provider.BaseColumns;


@Entity(tableName = Book.TABLE_NAME)
public class Book {

    public static final String TABLE_NAME = "books";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_ID = BaseColumns._ID;
    public static final String COLUMN_AUTHORS = "authors";
    public static final String COLUMN_PUBLISHER = "publisher";
    public static final String COLUMN_PUBLISH_DATE = "publishedDate";
    //  public static final String COLUMN_INDUSTRY_IDENTIFIERS = "industryIdentifiers";
    // public static final String COLUMN_READING_MODES = "readingModes";
    public static final String COLUMN_PAGE_COUNT = "pageCount";
    public static final String COLUMN_PRINT_TYPE = "printType";
    public static final String COLUMN_CATEGORIES = "categories";
    public static final String COLUMN_AVERAGE_RATING = "averageRating";
    public static final String COLUMN_RATING_COUNT = "ratingsCount";
    public static final String COLUMN_MATURITY_RATING = "maturityRating";
    public static final String COLUMN_IMAGE_LINKS = "imageLinks";
    public static final String COLUMN_LANGUAGE = "language";
    public static final String COLUMN_PREVIEW_LINK = "previewLink";
    public static final String COLUMN_INFO_LINK = "infoLink";
    public static final String COLUMN_CANONICAL_VOLUME_LINK = "canonicalVolumeLink";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_SUBTITLE = "subtitle";

    @PrimaryKey (autoGenerate = true)
    @ColumnInfo(index = true, name = COLUMN_ID)
    int _ID;

    @ColumnInfo(name = COLUMN_TITLE)
    String title;


    @ColumnInfo(name = COLUMN_AUTHORS)
    String authors;


    @ColumnInfo(name = COLUMN_PUBLISHER)
    String publisher;


    @ColumnInfo(name = COLUMN_PUBLISH_DATE)
    String publishedDate;


/*    @ColumnInfo(name = COLUMN_INDUSTRY_IDENTIFIERS)
    String industryIdentifiers;
    @ColumnInfo(name = COLUMN_READING_MODES)
    String readingModes;
*/


    @ColumnInfo(name = COLUMN_PAGE_COUNT)
    int pageCount;


    @ColumnInfo(name = COLUMN_PRINT_TYPE)
    String printType;


    @ColumnInfo(name = COLUMN_CATEGORIES)
    String categories;


    @ColumnInfo(name = COLUMN_AVERAGE_RATING)
    Double averageRating;


    @ColumnInfo(name = COLUMN_RATING_COUNT)
    int ratingsCount;


    @ColumnInfo(name = COLUMN_MATURITY_RATING)
    int maturityRating;


    @ColumnInfo(name = COLUMN_IMAGE_LINKS)
    String imageLinks;


    @ColumnInfo(name = COLUMN_LANGUAGE)
    String language;


    @ColumnInfo(name = COLUMN_PREVIEW_LINK)
    String previewLink;


    @ColumnInfo(name = COLUMN_INFO_LINK)
    String infoLink;


    @ColumnInfo(name = COLUMN_CANONICAL_VOLUME_LINK)
    String canonicalVolumeLink;


    @ColumnInfo(name = COLUMN_DESCRIPTION)
    String description;


    @ColumnInfo(name = COLUMN_SUBTITLE)
    String subtitle;

    public static Book fromContentValues(ContentValues values) {
        final Book book = new Book();
        if (values.containsKey(COLUMN_ID)) {
            book._ID = values.getAsInteger(COLUMN_ID);
        }
        if (values.containsKey(COLUMN_TITLE)) {
            book.title = values.getAsString(COLUMN_TITLE);
        }
        return book;
    }

    public Book() {
    }

    public Book(int _ID, String title, String authors, String publisher, String publishedDate,
                int pageCount, String printType, String categories, Double averageRating,
                int ratingsCount, int maturityRating, String imageLinks, String language,
                String previewLink, String infoLink, String canonicalVolumeLink,
                String description, String subtitle) {
        this._ID = _ID;
        this.title = title;
        this.authors = authors;
        this.publisher = publisher;
        this.publishedDate = publishedDate;
        this.pageCount = pageCount;
        this.printType = printType;
        this.categories = categories;
        this.averageRating = averageRating;
        this.ratingsCount = ratingsCount;
        this.maturityRating = maturityRating;
        this.imageLinks = imageLinks;
        this.language = language;
        this.previewLink = previewLink;
        this.infoLink = infoLink;
        this.canonicalVolumeLink = canonicalVolumeLink;
        this.description = description;
        this.subtitle = subtitle;
    }

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

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
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

    /*    public String getIndustryIdentifiers() {
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
    */
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

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
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