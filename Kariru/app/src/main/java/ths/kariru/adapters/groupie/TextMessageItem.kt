package ths.kariru.adapters.groupie

import android.content.Context
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import ths.kariru.R
import ths.kariru.models.TextMessage

class TextMessageItem(
    val message: TextMessage,
    val context: Context) : Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getLayout() = R.layout.text_message_item
}