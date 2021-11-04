package com.ahmed.redditmodellayer.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class RemoteFullResponse(
    val kind: String,
    @SerializedName("data")
    val remotePostsList: RemotePostsList
)

data class RemotePostsList(
    val after: String?,
    val dist: Int,
    @SerializedName("children")
    val posts: List<PostKind>,
    val before: String?
)

data class PostsList(
    val after: String?,
    val dist: Int,
    val posts: List<Post> = listOf(),
    val before: String?,
    var isCached: Boolean = false
)

data class PostKind(
    val kind: String,
    @SerializedName("data")
    val post: Post
)

@Entity(tableName = "Posts")
data class Post(
    @PrimaryKey
    val id: String,
    val title: String,
    val thumbnail: String,
    @SerializedName("url_overridden_by_dest")
    val thumbnailFullUrl: String,
    val name: String,
    @SerializedName("created_utc")
    val createdUTC: Double,
    val domain: String,
    val author: String,
    @SerializedName("num_comments")
    val numComments: Int,
    @SerializedName("is_video")
    val isVideo: Boolean,
    @SerializedName("ups")
    val upVotes: Int,
    @SerializedName("total_awards_received")
    val totalAwardsReceived: Int,
    @SerializedName("all_awardings")
    val awardIcons: List<AwardIcon>,
    val score: Int,
    @SerializedName("post_hint")
    val postHint: String?,
    val media: Media?
)

data class Media(
    @SerializedName("reddit_video")
    val redditVideo: RedditVideo?,
    @SerializedName("oembed")
    val gif: OEmbed?,
    val type: String?
)

data class RedditVideo (
    @SerializedName("fallback_url")
    val fallbackUrl: String,
    val height: Int,
    val width: Int,
    val duration: Double,
    @SerializedName("is_gif")
    val isGif: Boolean = false,
    @SerializedName("transcodingStatus")
    val transcodingStatus: String
)

data class OEmbed(
    @SerializedName("provider_url")
    val providerUrl: String,
    val description: String,
    val title: String,
    @SerializedName("author_name")
    val authorName: String,
    val height: Int,
    val width: Int,
    val html: String,
    @SerializedName("thumbnail_width")
    val thumbnailWidth: Int,
    @SerializedName("thumbnail_height")
    val thumbnailHeight: Int,
    val version: String,
    @SerializedName("provider_name")
    val providerName: String,
    @SerializedName("thumbnail_url")
    val thumbnailUrl: String,
    val type: String
)

data class AwardIcon (
    val id: String,
    val name: String,
    @SerializedName("static_icon_url")
    val iconUrl: String
)