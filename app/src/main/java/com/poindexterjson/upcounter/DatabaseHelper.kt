package com.poindexterjson.upcounter

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.time.LocalDate

const val DATABASE_NAME = "COUNT_UP"
const val TABLE_NAME = "Counters"
const val COL_TYPE = "type"
const val COL_START_DATE = "startDate"
const val COL_ID = "id"

class DataBaseHandler(private var context: Context) : SQLiteOpenHelper(
    context, DATABASE_NAME, null,
    1
) {
    override fun onCreate(db: SQLiteDatabase?) {
        val createTable =
            "CREATE TABLE $TABLE_NAME ($COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,$COL_TYPE VARCHAR(256),$COL_START_DATE TEXT)"
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //onCreate(db);
    }

    // Create new entry in TABLENAME
    fun insertData(item: CounterModel): Number {
        val database = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COL_TYPE, item.type)
        contentValues.put(COL_START_DATE, item.startDate.toString())
        return database.insert(TABLE_NAME, null, contentValues)
    }

    // Delete entry from TABLE_NAME
    fun deleteData(id: Number) {
        val database = this.writableDatabase
        val result = database.delete(TABLE_NAME, "$COL_ID =?", arrayOf(id.toString()))
    }


    fun readData(): MutableList<CounterModel> {
        val list: MutableList<CounterModel> = ArrayList()
        val db = this.readableDatabase
        val query = "Select * from $TABLE_NAME"
        val result = db.rawQuery(query, null)
        if (result.moveToFirst()) {
            do {
                @SuppressLint("Range")
                val type = result.getString(result.getColumnIndex(COL_TYPE))

                @SuppressLint("Range")
                val startDate = result.getString(result.getColumnIndex(COL_START_DATE))

                @SuppressLint("Range")
                val id = result.getInt(result.getColumnIndex(COL_ID))

                val item = CounterModel(id, type, LocalDate.parse(startDate))
                list.add(item)
            } while (result.moveToNext())
        }
        return list
    }
}