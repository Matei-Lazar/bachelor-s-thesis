package ths.kariru.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import ths.kariru.R
import ths.kariru.adapters.SearchRecyclerViewAdapter
import ths.kariru.databinding.FragmentSearchBinding
import ths.kariru.models.Property
import ths.kariru.utils.TopSpacingItemDecoration
import ths.kariru.viewmodels.SearchFragmentViewModel
import timber.log.Timber

class SearchFragment : Fragment() {

    private lateinit var viewModel: SearchFragmentViewModel
    private lateinit var binding: FragmentSearchBinding

    private var propertyList = ArrayList<Property>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        viewModel = ViewModelProvider(this).get(SearchFragmentViewModel::class.java)
        binding.viewModel = viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()

        viewModel.fetchProperties()

        viewModel.properties.observe(viewLifecycleOwner, Observer {
            propertyList.removeAll(propertyList)
            propertyList.addAll(it)
            binding.searchRecyclerview.adapter!!.notifyDataSetChanged()
        })
    }

    private fun initRecyclerView() {
        binding.searchRecyclerview.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            val topSpacingDecoration = TopSpacingItemDecoration(10)
            addItemDecoration(topSpacingDecoration)
            adapter = SearchRecyclerViewAdapter(propertyList) {property ->
                Timber.i("Search: ${property.propertyId}")
            }
        }
    }

//    private fun setUpRecyclerViewFromFirestore() {
//
//        binding.searchRecyclerview.apply {
//            layoutManager = LinearLayoutManager(activity)
//            val topSpacingDecoration = TopSpacingItemDecoration(30)
//            addItemDecoration(topSpacingDecoration)
//            itemAnimator = DefaultItemAnimator()
//        }
//
//        loadPropertyList()
//        firestoreListener = propertyCollectionRef
//            .addSnapshotListener(EventListener { documentSnapshots, e ->
//                if (e != null) {
//                    Timber.i("Search: Listen failed : $e")
//                    return@EventListener
//                }
//
//                propertyList = mutableListOf()
//
//                if (documentSnapshots != null) {
//                    for (doc in documentSnapshots) {
//                        val property = doc.toObject(Property::class.java)
//                        property.propertyId = doc.id
//                        propertyList.add(property)
//                    }
//                }
//
//                adapter.notifyDataSetChanged()
//                binding.searchRecyclerview.adapter = adapter
//            })
//    }
//
//    private fun loadPropertyList() {
//        val query = Firebase.firestore.collection("properties")
//        val options = FirestoreRecyclerOptions.Builder<Property>()
//            .setQuery(query, object : SnapshotParser<Property> {
//                override fun parseSnapshot(snapshot: DocumentSnapshot): Property? {
//                    return snapshot.toObject(Property::class.java).also {
//                        it?.propertyId = snapshot.id
//                    }
//                }
//            })
//            .setLifecycleOwner(this)
//            .build()
//
//        adapter = object: FirestoreRecyclerAdapter<Property, PropertyViewHolder>(options) {
//
//            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PropertyViewHolder {
//                val view = LayoutInflater.from(parent.context)
//                    .inflate(R.layout.property_post_item, parent, false)
//                return PropertyViewHolder(view)
//            }
//
//            override fun onBindViewHolder(holder: PropertyViewHolder?, position: Int, model: Property?) {
//                //val property = propertyList[position]
//                val adapter = AddViewPagerAdapter(model!!.imageList)
//                holder?.apply {
//                    searchViewPager.adapter = adapter
//                    searchNeighborhood.text = R.string.cluj_napoca.toString() + model.address.neighborhood
//                    searchStreet.text = model.address.street +" " + model.address.streetNumber
//                    searchPrice.text = model.price.toString()
//                    if (model.room == "1") {
//                        searchRooms.text = model.room + R.string.room
//                    } else {
//                        searchRooms.text = model.room + R.string.rooms
//                    }
//                    if (model.bath == "1") {
//                        searchBaths.text = model.bath + R.string.bath
//                    } else {
//                        searchBaths.text = model.bath + R.string.baths
//                    }
//                    searchSurface.text = model.surface.toString()
//                }
//
//            }
//
//            override fun onError(e: FirebaseFirestoreException?) {
//                Timber.i("Search: setup $e")
//            }
//        }
//        adapter.notifyDataSetChanged()
//        binding.searchRecyclerview.adapter = adapter
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//
//        //firestoreListener.remove()
//    }
}
