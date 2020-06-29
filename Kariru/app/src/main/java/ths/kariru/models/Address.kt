package ths.kariru.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Address(
    var street: String = "",
    var streetNumber: String ="",
    var blockName: String = "",
    var apartmentNumber: Int = 0,
    var neighborhood: String = ""
) : Parcelable