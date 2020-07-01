package ths.kariru.adapters.groupie

import android.content.Context
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.person_item.view.*
import ths.kariru.R
import ths.kariru.models.User
import ths.kariru.utils.GlideApp
import ths.kariru.utils.StorageUtil

class PersonItem(
    val person: User,
    val userId: String,
    private val context: Context
) : Item() {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.person_name_text.text = person.name
        if (person.profilePicture != null) {
            GlideApp.with(context)
                .load(StorageUtil.pathToReference(person.profilePicture))
                .placeholder(R.drawable.ic_account)
                .into(viewHolder.itemView.person_image_view)
        }
    }

    override fun getLayout() = R.layout.person_item

}