package com.example.android.architecture.blueprints.todoapp

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.room.Room
import com.example.android.architecture.blueprints.todoapp.data.source.DefaultTasksRepository
import com.example.android.architecture.blueprints.todoapp.data.source.TaskRepository
import com.example.android.architecture.blueprints.todoapp.data.source.TasksDataSource
import com.example.android.architecture.blueprints.todoapp.data.source.local.TasksLocalDataSource
import com.example.android.architecture.blueprints.todoapp.data.source.local.ToDoDatabase
import com.example.android.architecture.blueprints.todoapp.data.source.remote.TasksRemoteDataSource
import kotlinx.coroutines.runBlocking

object ServiceLocator {
    private var database: ToDoDatabase? = null
    @Volatile
    var tasksRepository: TaskRepository? = null
        @VisibleForTesting set

    private val lock = Any()

    @VisibleForTesting
    fun resetRepository() {
        synchronized(lock) {
            runBlocking {
                TasksRemoteDataSource.deleteAllTasks()
            }
            // Clear all data to avoid test pollution.
            database?.apply {
                clearAllTables()
                close()
            }
            database = null
            tasksRepository = null
        }
    }

    fun provideTasksRepository(context:Context):TaskRepository{
        synchronized(this){
            return tasksRepository ?: createTasksRepository(context)
        }
    }

    private fun createLocalDataSource(context: Context): TasksDataSource {
        val database = database ?: createDataBase(context)
        return TasksLocalDataSource(database.taskDao())
    }

    private fun createTasksRepository(context: Context): TaskRepository {
        val newRepo = DefaultTasksRepository(TasksRemoteDataSource,createLocalDataSource(context))
        tasksRepository = newRepo
        return newRepo
    }

    private fun createDataBase(context: Context): ToDoDatabase {
        val result = Room.databaseBuilder(
            context.applicationContext,
            ToDoDatabase::class.java, "Tasks.db"
        ).build()
        database = result
        return result
    }
}