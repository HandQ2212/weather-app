package com.app.weatherapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.weatherapp.databinding.ItemHourlyForecastBinding

class HourlyForecastAdapter : RecyclerView.Adapter<HourlyForecastAdapter.HourlyViewHolder>() {

    data class HourlyUiModel(
        val time: String,
        val temperature: String,
        val condition: String,
        val rainChance: String,
        val wind: String
    )

    private val items = mutableListOf<HourlyUiModel>()

    fun submitList(hourlyData: List<HourlyUiModel>) {
        items.clear()
        items.addAll(hourlyData)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyViewHolder {
        val binding = ItemHourlyForecastBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HourlyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HourlyViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class HourlyViewHolder(
        private val binding: ItemHourlyForecastBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: HourlyUiModel) {
            binding.tvHourTime.text = item.time
            binding.tvHourTemp.text = item.temperature
            binding.tvHourCondition.text = item.condition
            binding.tvHourRain.text = item.rainChance
            binding.tvHourWind.text = item.wind
        }
    }
}
