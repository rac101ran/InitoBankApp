package com.example.myapplication.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.models.Event

class EventAdapter(private val eventList: List<Event>) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val timestamp: TextView = itemView.findViewById(R.id.timestamp)
        val amount: TextView = itemView.findViewById(R.id.amount)
        val from: TextView = itemView.findViewById(R.id.from)
        val to: TextView = itemView.findViewById(R.id.to)
        val type: TextView = itemView.findViewById(R.id.type)
        val description: TextView = itemView.findViewById(R.id.description)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.event_item, parent, false)
        return EventViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val currentEvent = eventList[position]
        holder.timestamp.text = currentEvent.timestamp
        holder.amount.text = "Amount - ${currentEvent.amount}"
        holder.from.text = "From - ${currentEvent.from}"
        holder.to.text = "To - ${currentEvent.to}"
        holder.type.text = "Type - ${currentEvent.type}"
        holder.description.text = "Desciption - ${currentEvent.description}"
    }

    override fun getItemCount() = eventList.size
}