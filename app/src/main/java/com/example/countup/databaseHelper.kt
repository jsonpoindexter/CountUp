package com.example.countup

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import java.time.LocalDate

const val DATABASENAME = "MY DATABASE"
const val TABLENAME = "Counters"
const val COL_TYPE = "type"
const val COL_START_DATE = "startDate"
const val COL_ID = "id"

class DataBaseHandler(private var context: Context) : SQLiteOpenHelper(
    context, DATABASENAME, null,
    1
) {
    override fun onCreate(db: SQLiteDatabase?) {
        val createTable =
            "CREATE TABLE $TABLENAME ($COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,$COL_TYPE VARCHAR(256),$COL_START_DATE TEXT)"
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //onCreate(db);
    }

    fun insertData(item: CounterModel) {
        val database = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COL_TYPE, item.type)
        contentValues.put(COL_START_DATE, item.startDate.toString())
        val result = database.insert(TABLENAME, null, contentValues)
        if (result == (0).toLong()) {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
        }
    }


    fun readData(): MutableList<CounterModel> {
        val list: MutableList<CounterModel> = ArrayList()
        val db = this.readableDatabase
        val query = "Select * from $TABLENAME"
        val result = db.rawQuery(query, null)
        if (result.moveToFirst()) {
            do {
                @SuppressLint("Range")
                val type = result.getString(result.getColumnIndex(COL_TYPE))

                @SuppressLint("Range")
                val startDate = result.getString(result.getColumnIndex(COL_START_DATE))
                val item = CounterModel(type, LocalDate.parse(startDate))
                list.add(item)
            } while (result.moveToNext())
        }
        return list
    }
}