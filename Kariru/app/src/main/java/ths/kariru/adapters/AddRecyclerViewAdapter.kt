package ths.kariru.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.add_view_pager.view.*
import ths.kariru.MainActivity
import ths.kariru.R

class AddRecyclerViewAdapter(
    val images: MutableList<Uri>
) : RecyclerView.Adapter<AddRecyclerViewAdapter.AddRecyclerViewViewHolder>() {

    private val limit = 3

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddRecyclerViewViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.add_view_pager,
            parent, false)
        return AddRecyclerViewViewHolder(view)
    }

    override fun getItemCount() = images.size

    override fun onBindViewHolder(holder: AddRecyclerViewViewHolder, position: Int) {
        val currentImage = images[position]
        holder.imageView.setImageURI(currentImage)
    }

    inner class AddRecyclerViewViewHolder (itemView: View): RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.add_image_view_pager
    }
}