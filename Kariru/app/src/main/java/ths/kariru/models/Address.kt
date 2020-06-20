package ths.kariru.models

data class Address(
    var city: String = "Cluj-Napoca",
    var street: String,
    var streetNumber: Int,
    var blockName: String = "",
    var apartmentNumber: Int = 0
)