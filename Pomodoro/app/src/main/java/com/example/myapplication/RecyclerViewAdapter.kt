package com.example.myapplication

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(private val context: Context, private val taskList: MutableList<Task>) :
    RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskTextView: TextView = itemView.findViewById(R.id.taskTextView)
        val checkButton: Button = itemView.findViewById(R.id.checkButton)
        val deleteButton: Button = itemView.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.task_item, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = taskList[position]
        
        holder.taskTextView.text = task.task

        holder.checkButton.setOnClickListener {
            if (holder.taskTextView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG > 0) {
                // If already marked as complete, unmark it
                holder.taskTextView.paintFlags = holder.taskTextView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            } else {
                // Mark task as complete
                holder.taskTextView.paintFlags = holder.taskTextView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            }
        }


        holder.deleteButton.setOnClickListener {

            taskList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, taskList.size)

            (context as MainActivity).deleteTask(task.id)
        }
    }

    override fun getItemCount(): Int {
        return taskList.size
    }
}