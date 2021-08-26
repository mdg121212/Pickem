package com.mattg.pickem.models


import com.google.gson.annotations.SerializedName

data class RssFeedItem(
    @SerializedName("description")
    var description: String?,
    @SerializedName("home_page_url")
    var homePageUrl: String?,
    @SerializedName("items")
    var items: List<Item?>?,
    @SerializedName("title")
    var title: String?,
    @SerializedName("version")
    var version: String?
) {
    data class Item(
        @SerializedName("content_html")
        var contentHtml: String?,
        @SerializedName("date_published")
        var datePublished: String?,
        @SerializedName("guid")
        var guid: String?,
        @SerializedName("summary")
        var summary: String?,
        @SerializedName("title")
        var title: String?,
        @SerializedName("url")
        var url: String?,
        @SerializedName("image")
        val image: String?,
        @SerializedName("thumbnail")
        val thumbnail: String?
    )
}