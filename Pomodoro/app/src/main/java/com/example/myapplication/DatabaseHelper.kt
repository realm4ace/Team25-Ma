package com.example.myapplication

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        val createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_TASK + " TEXT)"
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME)
        onCreate(db)
    }

    fun addTask(task: String?): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COL_TASK, task)
        val result = db.insert(TABLE_NAME, null, values)
        return result != -1L
    }

    val allTasks: Cursor
        get() {
            val db = this.readableDatabase
            return db.rawQuery("SELECT * FROM " + TABLE_NAME, null)
        }

    fun deleteTask(id: Int): Boolean {
        val db = this.writableDatabase
        return db.delete(TABLE_NAME, COL_ID + "=" + id, null) > 0
    }

    fun updateTask(id: Int, newTask: String?): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COL_TASK, newTask)
        return db.update(TABLE_NAME, values, COL_ID + "=" + id, null) > 0
    }

    companion object {
        private const val DATABASE_NAME = "todolist.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "tasks"
        private const val COL_ID = "id"
        private const val COL_TASK = "task"
    }
}