package com.app.weatherapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.weatherapp.databinding.ItemDailyForecastBinding

class DailyForecastAdapter : RecyclerView.Adapter<DailyForecastAdapter.DailyViewHolder>() {

    data class DailyUiModel(
        val date: String,
        val condition: String,
        val tempRange: String,
        val extraInfo: String
    )

    private val items = mutableListOf<DailyUiModel>()

    fun submitList(dailyData: List<DailyUiModel>) {
        items.clear()
        items.addAll(dailyData)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyViewHolder {
        val binding = ItemDailyForecastBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DailyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DailyViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class DailyViewHolder(
        private val binding: ItemDailyForecastBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DailyUiModel) {
            binding.tvDayDate.text = item.date
            binding.tvDayCondition.text = item.condition
            binding.tvDayTemp.text = item.tempRange
            binding.tvDayExtra.text = item.extraInfo
        }
    }
}
