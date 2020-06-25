package ths.kariru.adapters

import android.graphics.Bitmap
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.add_view_pager.view.*
import ths.kariru.R

class AddViewPagerAdapter (
    private val images: MutableList<Uri>
) : RecyclerView.Adapter<AddViewPagerAdapter.ViewPagerViewHolder>() {

    inner class ViewPagerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.add_view_pager, parent, false)
        return ViewPagerViewHolder(view)
    }

    override fun getItemCount(): Int {
        return images.size
    }

    override fun onBindViewHolder(holder: ViewPagerViewHolder, position: Int) {
        val currentImage = images[position]
        holder.itemView.add_image_view_pager.setImageURI(currentImage)
    }

    fun removeItem(viewHolder: RecyclerView.ViewHolder) {
        images.removeAt(viewHolder.bindingAdapterPosition)
        notifyItemRemoved(viewHolder.bindingAdapterPosition)
    }
}