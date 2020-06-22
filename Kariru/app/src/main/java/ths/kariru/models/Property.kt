package ths.kariru.models

data class Property(
    var address: Address, // checked
    var photos: String = "", // checked
    var description: String = "",
    var type : String = "",
    var floor: Int = 0,
    var price: Int = 0,
    var room: Int = 0,
    var bath: Int = 0,
    var balcony: Int = 0,
    var surface: Int = 0,
    var userId: String = "",
    var propertyId: String = ""
)