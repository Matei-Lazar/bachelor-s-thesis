package ths.kariru.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.property_post_item.view.*
import ths.kariru.R
import ths.kariru.models.Property
import timber.log.Timber

class SearchRecyclerViewAdapter(
    private var propertyList: MutableList<Property>,
    private val listener: (Property) -> Unit
) : RecyclerView.Adapter<SearchRecyclerViewAdapter.PropertyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PropertyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.property_post_item, parent, false)
        return PropertyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PropertyViewHolder, position: Int) {

        val property = propertyList[position]
        holder.apply {
            itemView.setOnClickListener { listener(property) }
            val neighborhood = itemView.context.getString(R.string.cluj_napoca, property.address.neighborhood)
            val street = itemView.context.getString(R.string.street, property.address.street, property.address.streetNumber)
            val room = itemView.context.getString(R.string.room, property.room)
            val rooms = itemView.context.getString(R.string.rooms, property.room)
            val bath = itemView.context.getString(R.string.bath, property.bath)
            val baths = itemView.context.getString(R.string.baths, property.bath)


            val adapter = AddViewPagerAdapter(property.imageList)
            searchViewPager.adapter = adapter
            searchNeighborhood.text = neighborhood
            searchStreet.text = street
            searchPrice.text = property.price.toString()
            if (property.room == "1") {
                searchRooms.text = room
            } else {
                searchRooms.text = rooms
            }
            if (property.bath == "1") {
                searchBaths.text = bath
            } else {
                searchBaths.text = baths
            }
            searchSurface.text = property.surface.toString()
        }

    }

    override fun getItemCount(): Int{
        return propertyList.size
    }

    class PropertyViewHolder (
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {
        val searchViewPager : ViewPager2 = itemView.search1_view_pager
        val searchNeighborhood : TextView = itemView.search1_neighborhood
        val searchStreet : TextView = itemView.search1_street
        val searchPrice : TextView = itemView.search1_price
        val searchRooms : TextView = itemView.search1_rooms
        val searchBaths : TextView = itemView.search1_baths
        val searchSurface : TextView = itemView.search1_surface
    }
}