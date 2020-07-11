package ths.kariru.adapters


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.add_view_pager.view.*
import ths.kariru.R
import ths.kariru.utils.GlideApp

class EditRecyclerViewAdapter(
    val context: Context,
    val images: MutableList<String>
) : RecyclerView.Adapter<EditRecyclerViewAdapter.EditRecyclerViewViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EditRecyclerViewViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.add_view_pager,
            parent, false)
        return EditRecyclerViewViewHolder(view)
    }

    override fun getItemCount() = images.size

    override fun onBindViewHolder(holder: EditRecyclerViewViewHolder, position: Int) {
        val currentImage = images[position]
        GlideApp.with(context)
            .load(currentImage)
            .into(holder.imageView)
    }

    inner class EditRecyclerViewViewHolder (itemView: View): RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.add_image_view_pager
    }
}