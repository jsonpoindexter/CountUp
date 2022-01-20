package com.example.countup

import android.app.Activity
import android.os.Bundle
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.SnapHelper
import androidx.wear.widget.WearableLinearLayoutManager
import androidx.wear.widget.WearableRecyclerView
import com.example.countup.databinding.ActivityMainBinding
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class MainActivity : Activity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val recyclerView = findViewById<WearableRecyclerView>(R.id.recycler_launcher_view)
        recyclerView.apply {
            layoutManager = WearableLinearLayoutManager(this@MainActivity)
        }

        val db = DataBaseHandler(this)
        println("database Entries: " + db.readData())

        val countList = db.readData().map {
            ItemsViewModel(
                it.type,
                ChronoUnit.DAYS.between(it.startDate, LocalDate.now()).toString()
            )
        }

        val adapter = CustomAdapter(countList, this, db)
        recyclerView.adapter = adapter

        val snapHelper: SnapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(recyclerView)
    }
}