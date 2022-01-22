package com.example.upcounter

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.SnapHelper
import androidx.wear.widget.WearableLinearLayoutManager
import androidx.wear.widget.WearableRecyclerView
import com.example.upcounter.databinding.ActivityMainBinding

class MainActivity : FragmentActivity() {

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

        val countList = mutableListOf<CounterModel>()
        countList.addAll(db.readData())

        val adapter = CustomAdapter(countList, this, db)
        recyclerView.adapter = adapter

        val snapHelper: SnapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(recyclerView)
    }
}