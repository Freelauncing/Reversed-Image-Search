package com.reversedimagesearched.data.database

data class DatabaseModel (
    var _id:Int=0,
    var realimage:ByteArray,
    var realimageurl:String,
    var downloadedImage:ByteArray,
    var downloadedImageUrl:String,
    var downloadedImageName:String)