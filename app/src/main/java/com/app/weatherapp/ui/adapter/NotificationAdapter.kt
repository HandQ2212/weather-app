package com.app.weatherapp.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.weatherapp.data.model.NotificationEntity
import com.app.weatherapp.databinding.ItemNotificationBinding

class NotificationAdapter(
    private val notis: List<NotificationEntity>,
    private val isNewSection: Boolean
) : RecyclerView.Adapter<NotificationAdapter.NotiViewHolder>() {

    class NotiViewHolder(val binding: ItemNotificationBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = NotiViewHolder(
        ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: NotiViewHolder, position: Int) {
        holder.binding.noti = notis[position]

        changeColorBackground(holder)

        holder.binding.executePendingBindings()
    }

    override fun getItemCount() = notis.size

    private fun changeColorBackground(holder: NotiViewHolder) {
        val backgroundColor = if (isNewSection) Color.parseColor("#E6F5FA") else Color.TRANSPARENT
        holder.binding.rootLayout.setBackgroundColor(backgroundColor)
    }
}