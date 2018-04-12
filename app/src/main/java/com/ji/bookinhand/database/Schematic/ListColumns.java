package com.ji.bookinhand.database.Schematic;


import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

import static net.simonvt.schematic.annotation.DataType.Type.INTEGER;
import static net.simonvt.schematic.annotation.DataType.Type.TEXT;

public interface ListColumns {

    @DataType(INTEGER)
    @PrimaryKey
    @AutoIncrement
    String _ID = "_id";

    @DataType(TEXT)
    @NotNull
    String TITLE = "title";

    @DataType(TEXT)
    @NotNull
    String authors = "authors";

    @DataType(TEXT)
    String publisher = "publisher";

    @DataType(TEXT)
    String publishedDate = "publishedDate";

    @DataType(TEXT)
    String industryIdentifiers = "industryIdentifiers";

    @DataType(TEXT)
    String readingModes = "readingModes";

    @DataType(INTEGER)
    String pageCount = "pageCount";

    @DataType(TEXT)
    String printType = "printType";

    @DataType(TEXT)
    String categories = "categories";

    @DataType(TEXT)
    String averageRating = "averageRating";

    @DataType(INTEGER)
    String ratingsCount = "ratingsCount";

    @DataType(TEXT)
    String maturityRating = "maturityRating";

    @DataType(TEXT)
    String imageLinks = "imageLinks";

    @DataType(TEXT)
    String language = "language";

    @DataType(TEXT)
    String previewLink = "previewLink";

    @DataType(TEXT)
    String infoLink = "infoLink";

    @DataType(TEXT)
    String canonicalVolumeLink = "canonicalVolumeLink";

    @DataType(TEXT)
    @NotNull
    String description = "description";

    @DataType(TEXT)
    String subtitle = "subtitle";
}