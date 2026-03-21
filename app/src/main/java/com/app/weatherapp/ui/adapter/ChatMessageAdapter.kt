package com.app.weatherapp.ui.adapter

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.weatherapp.R
import com.app.weatherapp.databinding.ItemChatMessageBinding
import com.app.weatherapp.ui.model.ChatMessageUi

class ChatMessageAdapter : ListAdapter<ChatMessageUi, ChatMessageAdapter.ChatMessageViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatMessageViewHolder {
        val binding = ItemChatMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatMessageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatMessageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ChatMessageViewHolder(
        private val binding: ItemChatMessageBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ChatMessageUi) {
            binding.tvMessage.text = item.text
            val params = binding.tvMessage.layoutParams as ViewGroup.MarginLayoutParams
            if (item.isUser) {
                params.width = ViewGroup.LayoutParams.WRAP_CONTENT
                (params as? ViewGroup.MarginLayoutParams)?.marginStart = binding.root.resources.getDimensionPixelSize(R.dimen.chat_user_message_start_margin)
                (params as? ViewGroup.MarginLayoutParams)?.marginEnd = 0
                binding.tvMessage.layoutParams = params
                binding.tvMessage.gravity = Gravity.END
                binding.tvMessage.setBackgroundResource(R.drawable.bg_chat_user)
                binding.tvMessage.setTextColor(binding.root.context.getColor(android.R.color.white))
                binding.root.gravity = Gravity.END
            } else {
                params.width = ViewGroup.LayoutParams.WRAP_CONTENT
                (params as? ViewGroup.MarginLayoutParams)?.marginStart = 0
                (params as? ViewGroup.MarginLayoutParams)?.marginEnd = binding.root.resources.getDimensionPixelSize(R.dimen.chat_bot_message_end_margin)
                binding.tvMessage.layoutParams = params
                binding.tvMessage.gravity = Gravity.START
                binding.tvMessage.setBackgroundResource(R.drawable.bg_chat_bot)
                binding.tvMessage.setTextColor(binding.root.context.getColor(R.color.black))
                binding.root.gravity = Gravity.START
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<ChatMessageUi>() {
        override fun areItemsTheSame(oldItem: ChatMessageUi, newItem: ChatMessageUi): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ChatMessageUi, newItem: ChatMessageUi): Boolean {
            return oldItem == newItem
        }
    }
}
