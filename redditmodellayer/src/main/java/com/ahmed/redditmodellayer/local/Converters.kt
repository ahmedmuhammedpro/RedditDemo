package com.ahmed.redditmodellayer.local

import androidx.room.TypeConverter
import com.ahmed.redditmodellayer.models.AwardIcon
import com.ahmed.redditmodellayer.models.Media
import com.google.gson.Gson

class Converters {

    @TypeConverter
    fun fromAwardIcons(list: List<AwardIcon>?) = Gson().toJson(list)

    @TypeConverter
    fun toAwardIcons(value: String) = Gson().fromJson(value, Array<AwardIcon>::class.java).toList()

    @TypeConverter
    fun fromMedia(media: Media?) = Gson().toJson(media)

    @TypeConverter
    fun toMedia(value: String) = Gson().fromJson(value, Media::class.java)
}