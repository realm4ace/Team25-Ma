package com.example.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.os.CountDownTimer
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private lateinit var taskEditText: EditText
    private lateinit var addTaskButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var db: DatabaseHelper
    private lateinit var taskList: MutableList<Task>
    private lateinit var pomodoroTimerTextView: TextView
    private lateinit var startTimerButton: Button
    private val pomodoroTimeInMillis = 25 * 60 * 1000L
    private val shortBreakTimeInMillis = 5 * 60 * 1000L
    private val longBreakTimeInMillis = 15 * 60 * 1000L
    private var pomodoroCount = 0
    private val maxPomodorosBeforeLongBreak = 4
    private var isTimerRunning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        taskEditText = findViewById(R.id.taskEditText)
        addTaskButton = findViewById(R.id.addTaskButton)
        recyclerView = findViewById(R.id.recyclerView)

        recyclerView.layoutManager = LinearLayoutManager(this)

        db = DatabaseHelper(this)
        taskList = ArrayList()

        loadTasks()

        pomodoroTimerTextView = findViewById(R.id.pomodoroTimer)
        startTimerButton = findViewById(R.id.startTimerButton)

        startTimerButton.setOnClickListener {
            if (!isTimerRunning) {
                startPomodoroTimer(pomodoroTimeInMillis)
            } else {
                Toast.makeText(this, "Timer is already running", Toast.LENGTH_SHORT).show()
            }
        }

        addTaskButton.setOnClickListener {
            val task = taskEditText.text.toString()
            if (task.isNotEmpty()) {
                db.addTask(task)
                taskEditText.text.clear()
                loadTasks()
            } else {
                Toast.makeText(this@MainActivity, "Task cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startPomodoroTimer(duration: Long) {
        object : CountDownTimer(duration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                isTimerRunning = true
                val minutes = (millisUntilFinished / 1000) / 60
                val seconds = (millisUntilFinished / 1000) % 60
                pomodoroTimerTextView.text = String.format("%02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                isTimerRunning = false
                pomodoroCount++

                if (pomodoroCount % maxPomodorosBeforeLongBreak == 0) {
                    pomodoroTimerTextView.text = "Long Break!"
                    Toast.makeText(this@MainActivity, "Long break time!", Toast.LENGTH_SHORT).show()
                    startPomodoroTimer(longBreakTimeInMillis)
                } else {
                    pomodoroTimerTextView.text = "Short Break!"
                    Toast.makeText(this@MainActivity, "Short break time!", Toast.LENGTH_SHORT).show()
                    startPomodoroTimer(shortBreakTimeInMillis)
                }
            }
        }.start()
    }

    private fun loadTasks() {
        taskList.clear()
        val cursor = db.allTasks
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(0)
                val task = cursor.getString(1)
                taskList.add(Task(id, task))
            } while (cursor.moveToNext())
        }
        cursor.close()

        taskAdapter = TaskAdapter(this, taskList)
        recyclerView.adapter = taskAdapter
    }
    fun deleteTask(taskId: Int) {
        db.deleteTask(taskId)
        loadTasks()
    }
}