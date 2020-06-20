package ths.kariru.models

data class Property(
    var description: String,
    var photos: String = "",
    var address: Address,
    var type : String,
    var floor: Int,
    var price: Int,
    var room: Int,
    var bath: Int,
    var balcony: Int,
    var surface: Int
)