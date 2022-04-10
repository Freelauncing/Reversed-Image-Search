package com.reversedimagesearched.data.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.reversedimagesearched.data.database.ReverseImageContract.Companion.DATABASE_NAME
import com.reversedimagesearched.data.database.ReverseImageContract.Companion.DATABASE_TABLE_REVERSEIMAGES
import com.reversedimagesearched.data.database.ReverseImageContract.Companion.DATABASE_VERSION
import com.reversedimagesearched.data.database.ReverseImageContract.Companion.QUERY_CREATE_REVERSEIMAGES
import com.reversedimagesearched.data.database.ReverseImageContract.Companion.QUERY_UPDATE_REVERSEIMAGES
import com.reversedimagesearched.data.database.ReverseImageContract.Companion.ROW_DOWNLOADEDIMAGE
import com.reversedimagesearched.data.database.ReverseImageContract.Companion.ROW_DOWNLOADEDIMAGENAME
import com.reversedimagesearched.data.database.ReverseImageContract.Companion.ROW_DOWNLOADEDIMAGEURL
import com.reversedimagesearched.data.database.ReverseImageContract.Companion.ROW_ID
import com.reversedimagesearched.data.database.ReverseImageContract.Companion.ROW_REALIMAGE
import com.reversedimagesearched.data.database.ReverseImageContract.Companion.ROW_REALIMAGEURL

class ReverseDbHelper(ctx: Context) : SQLiteOpenHelper(ctx,
    DATABASE_NAME, null,
    DATABASE_VERSION
) {

    companion object {
        private lateinit var INSTANCE: ReverseDbHelper
        private lateinit var database: SQLiteDatabase
        private var databaseOpen: Boolean = false

        fun closeDatabase() {
            if (database.isOpen && databaseOpen) {
                database.close()
                databaseOpen = false

                Log.i("Database" , "Database close")
            }
        }

        fun initDatabaseInstance(ctx: Context): ReverseDbHelper {
            INSTANCE = ReverseDbHelper(ctx)
            return INSTANCE
        }

        fun insertReverseImage(obj:DatabaseModel): Long {

            if (!databaseOpen) {
                database = INSTANCE.writableDatabase
                databaseOpen = true

                Log.i("Database" , "Database Open")
            }

            val values = ContentValues()
            values.put(ROW_REALIMAGE, obj.realimage)
            values.put(ROW_REALIMAGEURL, obj.realimageurl)
            values.put(ROW_DOWNLOADEDIMAGE, obj.downloadedImage)
            values.put(ROW_DOWNLOADEDIMAGEURL, obj.downloadedImageUrl)
            values.put(ROW_DOWNLOADEDIMAGENAME, obj.downloadedImageName)
            return database.insert(DATABASE_TABLE_REVERSEIMAGES, null, values)
        }

        fun getAllImages(): MutableList<DatabaseModel> {
            if (!databaseOpen) {
                database = INSTANCE.writableDatabase
                databaseOpen = true

                Log.i("Database" , "Database Open")
            }

            val data: MutableList<DatabaseModel> = ArrayList()
            val cursor = database.rawQuery("SELECT * FROM ${DATABASE_TABLE_REVERSEIMAGES}", null)
            cursor.use { cur ->
                if (cursor.moveToFirst()) {
                    do {
                        val image = DatabaseModel(
                            cur.getInt(0),
                            cur.getBlob(1),
                            cur.getString(2),
                            cur.getBlob(3),
                            cur.getString(4),
                            cur.getString(5)
                        )
                        data.add(image)

                    } while (cursor.moveToNext())
                }
            }
            return data
        }

        fun deleteReverseImageData(id: Int): Int {
            if (!databaseOpen) {
                database = INSTANCE.writableDatabase
                databaseOpen = true

                Log.i("Database" , "Database Open")
            }
            return database.delete(DATABASE_TABLE_REVERSEIMAGES, "${ROW_ID} = $id", null)
        }

    }

    override fun onCreate(p0: SQLiteDatabase?) {
        p0?.execSQL(QUERY_CREATE_REVERSEIMAGES)
        Log.i("DATABASE", "DATABASE CREATED")
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        p0?.execSQL(QUERY_UPDATE_REVERSEIMAGES)
        Log.i("DATABASE", "DATABASE UPDATED")
    }

}