package com.poindexterjson.upcounter

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.view.LayoutInflater
import android.view.View
import android.view.View.inflate
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
import java.util.*
import java.util.concurrent.TimeUnit


class CustomAdapter(
    private var mList: MutableList<CounterModel>,
    val context: Context,
    val db: DataBaseHandler,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val timer = Timer()

    fun destroyTimers() {
        this.timer.cancel()
    }

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
            R.layout.counter_populated_design -> {
                (holder as ViewHolder).bind(mList[position])
                // Schedule counters to update after midnight next day
                val today = Calendar.getInstance()
                today.add(Calendar.DATE, 1)
                today.set(Calendar.HOUR_OF_DAY, 0)
                today.set(Calendar.MINUTE, 0)
                today.set(Calendar.SECOND, 0)
                timer.schedule(object : TimerTask() {
                    // Run scheduled task at upcoming midnight
                    override fun run() {
                        val daysView = holder.itemView.findViewById<TextView>(R.id.daysValue)
                        daysView.text =
                            ChronoUnit.DAYS.between(
                                mList[holder.bindingAdapterPosition].startDate,
                                LocalDate.now()
                            )
                                .toString()
                    }
                }, today.time, TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS))

            }
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
        fun bind(item: CounterModel) {
            daysView.text = ChronoUnit.DAYS.between(item.startDate, LocalDate.now()).toString()
            typeView.text = item.type
            frameLayout.setOnLongClickListener {
                val dialog = Dialog(context)
                val myLayout = inflate(context, R.layout.wearable_alert_dialog, null)

                val positiveButton = myLayout.findViewById<ImageButton>(R.id.btn_ok)
                positiveButton.setOnClickListener {
                    db.deleteData(item.id)
                    mList.removeAt(bindingAdapterPosition)
                    notifyItemRemoved(bindingAdapterPosition)
                    notifyItemRangeChanged(bindingAdapterPosition, mList.size)
                    dialog.cancel()
                }

                val negativeButton = myLayout.findViewById<ImageButton>(R.id.btn_cancel)
                negativeButton.setOnClickListener {
                    dialog.cancel()
                }

                dialog.setContentView(myLayout)
                dialog.show()


                true
            }
        }
    }

    inner class AddViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dateEditText = itemView.findViewById<EditText>(R.id.date)
        private val nameEditText = itemView.findViewById<EditText>(R.id.name)
        private val addButton = itemView.findViewById<ImageButton>(R.id.addButton)
        private val deleteButton = itemView.findViewById<ImageButton>(R.id.deleteButton)

        fun bind() {
            val currentDate = LocalDate.now()
            // Default to current date
            dateEditText.setText(currentDate.toString())

            dateEditText.setOnClickListener {
                val cal = Calendar.getInstance()
                val dateSetListener =
                    DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                        cal.set(Calendar.YEAR, year)
                        cal.set(Calendar.MONTH, monthOfYear)
                        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                        val myFormat = "yyyy-MM-dd" // format you need
                        val sdf = SimpleDateFormat(myFormat, Locale.US)
                        dateEditText.setText(sdf.format(cal.time))

                    }
                DatePickerDialog(
                    context, dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show()
            }

            addButton.setOnClickListener {
                try {
                    val date = LocalDate.parse(dateEditText.text)
                    // Add item to database
                    val id = db.insertData(
                        CounterModel(
                            0, // Note: this doesn't get used
                            nameEditText.text.toString(),
                            date
                        )
                    )
                    // Add item to recycler list
                    mList.add(CounterModel(id, nameEditText.text.toString(), date))
                    // Snap to new item
                    notifyItemInserted(mList.size)
                } catch (e: DateTimeParseException) {
                    val shake: Animation =
                        AnimationUtils.loadAnimation(context, R.anim.shake)
                    dateEditText.startAnimation(shake)
                    println(e)
                } finally {
                    // Reset values
                    dateEditText.setText(currentDate.toString())
                    nameEditText.setText("")
                }
            }
            // Reset fields
            deleteButton.setOnClickListener {
                dateEditText.setText(currentDate.toString())
                nameEditText.setText("")
            }
        }
    }
}