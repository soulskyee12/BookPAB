package com.example.booksapp3

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.booksapp3.R
import com.example.booksapp3.ViewModel.ChatMessage

class ChatAdapter(private var messages: List<ChatMessage>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].isSender) VIEW_TYPE_SENDER else VIEW_TYPE_RECEIVER
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_TYPE_SENDER) {
            val view = inflater.inflate(R.layout.activity_send_chatbot, parent, false)
            SenderViewHolder(view)
        } else {
            val view = inflater.inflate(R.layout.activity_receive_chatbot, parent, false)
            ReceiverViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        Log.d("ChatAdapter", "Binding message at position $position: ${message.message}")
        if (holder is SenderViewHolder) {
            holder.tvMessage.text = message.message
        } else if (holder is ReceiverViewHolder) {
            holder.tvMessage.text = message.message
        }
    }



    override fun getItemCount(): Int = messages.size

    class SenderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvMessage: TextView = itemView.findViewById(R.id.tvMessageSender)
    }

    class ReceiverViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvMessage: TextView = itemView.findViewById(R.id.tvMessageReceiver)
    }

    fun updateMessages(newMessages: List<ChatMessage>) {
        Log.d("ChatAdapter", "updateMessages called with: ${newMessages.size} messages")
        messages = newMessages
        notifyDataSetChanged()
    }


    companion object {
        private const val VIEW_TYPE_SENDER = 1
        private const val VIEW_TYPE_RECEIVER = 2
    }


}