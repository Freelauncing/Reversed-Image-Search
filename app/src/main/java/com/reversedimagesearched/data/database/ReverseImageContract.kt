package com.reversedimagesearched.data.database

class ReverseImageContract {

    companion object {

        val DATABASE_NAME = "db_reverse_images.db"
        val DATABASE_VERSION = 1

        val DATABASE_TABLE_REVERSEIMAGES = "TBL_REVERSEIMAGES"

        val ROW_ID = "_id"
        val ROW_REALIMAGE = "realimage"
        val ROW_REALIMAGEURL = "realimageurl"
        val ROW_DOWNLOADEDIMAGE = "downloadedimage"
        val ROW_DOWNLOADEDIMAGEURL = "downloadedimageurl"
        val ROW_DOWNLOADEDIMAGENAME = "downloadedimagename"

        val QUERY_CREATE_REVERSEIMAGES = "CREATE TABLE IF NOT EXISTS " +
                "$DATABASE_TABLE_REVERSEIMAGES " +
                "($ROW_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$ROW_REALIMAGE BLOB , $ROW_REALIMAGEURL TEXT, " +
                "$ROW_DOWNLOADEDIMAGE BLOB , $ROW_DOWNLOADEDIMAGEURL TEXT , $ROW_DOWNLOADEDIMAGENAME TEXT)"
        val QUERY_UPDATE_REVERSEIMAGES = "DROP TABLE IF EXISTS $DATABASE_TABLE_REVERSEIMAGES"

    }

}