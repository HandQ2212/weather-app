package com.app.weatherapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.weatherapp.data.model.SearchCityResponseItem
import com.app.weatherapp.databinding.ItemSearchBinding

class SearchCityAdapter(
    private val onItemClick: (SearchCityResponseItem) -> Unit
) : RecyclerView.Adapter<SearchCityAdapter.SearchCityViewHolder>() {

    private val items = mutableListOf<SearchCityResponseItem>()

    fun submitList(cities: List<SearchCityResponseItem>) {
        items.clear()
        items.addAll(cities)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchCityViewHolder {
        val binding = ItemSearchBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SearchCityViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchCityViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class SearchCityViewHolder(
        private val binding: ItemSearchBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(city: SearchCityResponseItem) {
            binding.city = city
            binding.root.setOnClickListener {
                onItemClick(city)
            }
            binding.executePendingBindings()
        }
    }
}
