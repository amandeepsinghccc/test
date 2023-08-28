package com.example.android.architecture.blueprints.todoapp.data.source.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.ApplicationProvider.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.android.architecture.blueprints.todoapp.data.Result
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.succeeded
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest
class TasksLocalDataSourceTest {

    private lateinit var localDataSource: TasksLocalDataSource
    private lateinit var database: ToDoDatabase


    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
     fun setup() {
        // Using an in-memory database for testing, because it doesn't survive killing the process.
        Room.inMemoryDatabaseBuilder(
            context = getApplicationContext(),
            klass = ToDoDatabase::class.java
        )
            .allowMainThreadQueries()
            .build().also { database = it }

        localDataSource =
            TasksLocalDataSource(
                database.taskDao(),
                Dispatchers.Main
            )
    }

    @After
    fun cleanUp() {
        database.close()
    }

    @Test
    fun saveTask_retrievesTask() = runTest {
        // GIVEN - A new task saved in the database.
        val newTask = Task("title", "description", false)
        localDataSource.saveTask(newTask)

        // WHEN  - Task retrieved by ID.
        val result = localDataSource.getTask(newTask.id)

        // THEN - Same task is returned.
        assertThat(result.succeeded, `is`(true))
        result as Result.Success
        assertThat(result.data.title, `is`("title"))
        assertThat(result.data.description, `is`("description"))
        assertThat(result.data.isCompleted, `is`(false))
    }

    @Test
    fun completeTask_retrievedTaskIsComplete() = runTest{
        // 1. Save a new active task in the local data source.
        val newTask = Task("title", "description", false)
        localDataSource.saveTask(newTask)

        localDataSource.completeTask(newTask)

        val result = localDataSource.getTask(newTask.id)

        // THEN - Same task is returned.
        assertThat(result.succeeded, `is`(true))
        result as Result.Success
        assertThat(result.data.title, `is`("title"))
        assertThat(result.data.description, `is`("description"))
        assertThat(result.data.isCompleted, `is`(true))
        // 2. Mark it as complete.

        // 3. Check that the task can be retrieved from the local data source and is complete.

    }

}