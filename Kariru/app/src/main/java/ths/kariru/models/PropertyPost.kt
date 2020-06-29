package ths.kariru.models

import android.net.Uri

data class PropertyPost(
    var imageList: MutableList<Uri>,
    var price: Int,
    var neighborhood: String,
    var streetNumber: String,
    var room: String,
    var bath: String,
    var surface: Int,
    var propertyId: String
)