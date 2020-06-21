package ths.kariru.models

data class Address(
    var street: String,
    var streetNumber: Int,
    var blockName: String = "",
    var apartmentNumber: Int = 0,
    var neighborhood: String = ""
)