package com.reversedimagesearched.data.model

import androidx.room.ColumnInfo

data class CommonResponse(

    @ColumnInfo(name = "store_link")
    var store_link : String ,

    @ColumnInfo(name = "name")
    var name : String ,

    @ColumnInfo(name = "domain")
    var domain : String ,

    @ColumnInfo(name = "identifier")
    var identifier : String ,

    @ColumnInfo(name = "tracking_id")
    var tracking_id : String ,

    @ColumnInfo(name = "thumbnail_link")
    var thumbnail_link : String ,

    @ColumnInfo(name = "description")
    var description : String ,

    @ColumnInfo(name = "image_link")
    var image_link : String ,

    @ColumnInfo(name = "current_date")
    var current_date : String ,

)
