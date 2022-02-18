package com.poindexterjson.upcounter

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.SnapHelper
import androidx.wear.widget.WearableLinearLayoutManager
import androidx.wear.widget.WearableRecyclerView
import com.poindexterjson.upcounter.databinding.ActivityMainBinding

class MainActivity : FragmentActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: CustomAdapter

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

        adapter = CustomAdapter(countList, this, db)
        recyclerView.adapter = adapter

        val snapHelper: SnapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(recyclerView)
    }

    override fun onDestroy() {
        super.onDestroy()
        this.adapter.destroyTimers()
    }

    override fun onResume() {
        super.onResume()
        this.adapter.refreshItems()
    }
}