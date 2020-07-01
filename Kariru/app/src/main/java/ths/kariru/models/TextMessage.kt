package ths.kariru.models

import java.util.*

data class TextMessage(
    val text: String = "",
    override val time: Date = Date(0),
    override val senderId: String = "",
    override val type: String = MessageType.TEXT
) : Message {
}