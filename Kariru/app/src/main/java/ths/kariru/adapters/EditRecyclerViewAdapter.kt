package ths.kariru.adapters


import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.add_view_pager.view.*
import kotlinx.coroutines.withContext
import ths.kariru.MainActivity
import ths.kariru.R

class EditRecyclerViewAdapter(
    val context: Context,
    val images: MutableList<String>
) : RecyclerView.Adapter<EditRecyclerViewAdapter.EditRecyclerViewViewHolder>() {

    private val limit = 3

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EditRecyclerViewViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.add_view_pager,
            parent, false)
        return EditRecyclerViewViewHolder(view)
    }

    override fun getItemCount() = images.size

    override fun onBindViewHolder(holder: EditRecyclerViewViewHolder, position: Int) {
        val currentImage = images[position]
        Glide.with(context)
            .load(currentImage)
            .into(holder.imageView)
    }

    inner class EditRecyclerViewViewHolder (itemView: View): RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.add_image_view_pager
    }
}