package com.sarrawi.img.model

import com.google.gson.annotations.SerializedName

data class ImgsModel(
    @SerializedName("id")
    var id:Int,
    @SerializedName("ID_Type_id")
    var ID_Type_id : Int,
    @SerializedName("new_img")
    var new_img :Int,
    @SerializedName("image_url")
    var image_url :String

)

