package com.example.countup

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoUnit


class CustomAdapter(
    private var mList: MutableList<ItemsViewModel>,
    val context: Context,
    val db: DataBaseHandler
) :
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
        private val frameLayout = itemView.findViewById<FrameLayout>(R.id.frame)

        fun bind(item: ItemsViewModel) {
            daysView.text = item.days
            typeView.text = item.type
            frameLayout.setOnLongClickListener {
                db.deleteData(item.id)
                mList.removeAt(bindingAdapterPosition)
                notifyItemRemoved(bindingAdapterPosition)
                notifyItemRangeChanged(bindingAdapterPosition, mList.size)
                true
            }
        }
    }

    inner class AddViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dateView = itemView.findViewById<EditText>(R.id.date)
        private val nameEditText = itemView.findViewById<EditText>(R.id.name)
        private val addButton = itemView.findViewById<ImageButton>(R.id.addButton)
        private val deleteButton = itemView.findViewById<ImageButton>(R.id.deleteButton)

        fun bind() {
            // Default to current date
            dateView.setText(LocalDate.now().toString())

            addButton.setOnClickListener {
                try {
                    val days =
                        ChronoUnit.DAYS.between(LocalDate.parse(dateView.text), LocalDate.now())

                    // Add item to database
                    val id = db.insertData(
                        CounterModel(
                            0, // Note: this doesn't get used
                            nameEditText.text.toString(),
                            LocalDate.parse(dateView.text)
                        )
                    )
                    // Add item to recycler list
                    mList.add(ItemsViewModel(id, nameEditText.text.toString(), days.toString()))
                    // Snap to new item
                    notifyItemInserted(mList.size)
                } catch (e: DateTimeParseException) {
                    val shake: Animation =
                        AnimationUtils.loadAnimation(context, R.anim.shake)
                    dateView.startAnimation(shake)
                    println(e)
                } finally {
                    // Reset values
                    dateView.setText(LocalDate.now().toString())
                    nameEditText.setText("")
                }
            }
            // Reset fields
            deleteButton.setOnClickListener {
                dateView.setText(LocalDate.now().toString())
                nameEditText.setText("")
            }
        }
    }
}