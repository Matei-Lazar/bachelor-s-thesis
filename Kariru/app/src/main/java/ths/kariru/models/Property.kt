package ths.kariru.models

data class Property(
    var address: Address = Address("", "", "", 0, ""),
    var latitude: String = "",
    var longitude: String = "",

    var floor: String = "",
    var room: String = "",
    var bath: String = "",
    var balcony: String = "",

    var surface: Int = 0,
    var description: String = "",
    var price: Int = 0,

    var userId: String = "",
    var propertyId: String = "",
    var imageList: MutableList<String> = mutableListOf()
)