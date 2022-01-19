package com.example.countup

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.widget.TextView
import com.example.countup.databinding.ActivityMainBinding
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class MainActivity : Activity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val typeView = findViewById<TextView>(R.id.type)
        @SuppressLint("SetTextI18n") // TODO: remove once dynamic
        typeView.text = "Hospital"

        // Start date will be user input
        val startDate = LocalDate.of(2021, 11, 28)
        val currDate = LocalDate.now()
        println(startDate)
        println(currDate)
        val diff = ChronoUnit.DAYS.between(startDate, currDate)
        println("DateDiff: $diff")
        val daysView = findViewById<TextView>(R.id.daysValue)
        daysView.text = diff.toString()

    }
}