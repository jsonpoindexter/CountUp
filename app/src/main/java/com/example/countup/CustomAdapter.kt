package com.example.countup

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class CustomAdapter(private val mList: List<ItemsViewModel>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == R.layout.counter_populated_design) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.counter_populated_design, parent, false)
            ViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.add_counter_design, parent, false)
            AddViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            R.layout.counter_populated_design -> (holder as ViewHolder).bind(mList[position])
            R.layout.add_counter_design -> (holder as AddViewHolder).bind()
        }
    }

    override fun getItemCount(): Int {
        return mList.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == mList.size) R.layout.add_counter_design else R.layout.counter_populated_design
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val typeView = itemView.findViewById<TextView>(R.id.type)
        private val daysView = itemView.findViewById<TextView>(R.id.daysValue)

        fun bind(item: ItemsViewModel) {
            daysView.text = item.days
            typeView.text = item.type
        }
    }

    inner class AddViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        private val btn = itemView.findViewById<EditText>(R.id.a)

        fun bind() {
//            btn.setOnClickListener {
//                //Do your logic here for the button
//            }
        }
    }
}