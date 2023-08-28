package com.example.android.architecture.blueprints.todoapp.data.source

import android.util.Log
import com.example.android.architecture.blueprints.todoapp.MainCoroutineRule
import com.example.android.architecture.blueprints.todoapp.data.Result
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.fake.FakeDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.function.Predicate.isEqual

@ExperimentalCoroutinesApi
class DefaultTasksRepositoryTest {
    private val task1 = Task("Title1", "Description1")
    private val task2 = Task("Title2", "Description2", true)
    private val task3 = Task("Title3", "Description3", true)
    private val task4 = Task("Title4", "Description4", true)
    private val remoteTasks = listOf(task1, task2).sortedBy { it.id }.toMutableList()
    private val localTasks = listOf(task3).sortedBy { it.id }.toMutableList()
    private val newTasks = listOf(task4).sortedBy { it.id }

    private lateinit var tasksRemoteDataSource: FakeDataStore
    private lateinit var tasksLocalDataSource: FakeDataStore

    // Class under test
    private lateinit var tasksRepository: DefaultTasksRepository

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun createRepository() {
        tasksRemoteDataSource = FakeDataStore(remoteTasks.toMutableList())
        tasksLocalDataSource = FakeDataStore(localTasks.toMutableList())
        // Get a reference to the class under test
        tasksRepository = DefaultTasksRepository(
            // TODO Dispatchers.Unconfined should be replaced with Dispatchers.Main
            //  this requires understanding more about coroutines + testing
            //  so we will keep this as Unconfined for now.
            tasksRemoteDataSource, tasksLocalDataSource, Dispatchers.Main
        )
    }

    @Test
    fun getTasks_requestsAllTasksFromRemoteDataSource() = runBlocking {
        // When tasks are requested from the tasks repository
        val tasks = tasksRepository.getTasks(true) as Result.Success

        // Then tasks are loaded from the remote data source
        assertThat(tasks.data, IsEqual(remoteTasks))
    }

    @Test
    fun getTasks_requestsAllTasksFromLocalDataSource() = runBlocking {
        // When tasks are requested from the tasks repository
        val tasks = tasksRepository.getTasks(false) as Result.Success

        // Then tasks are loaded from the remote data source
        assertThat(tasks.data, IsEqual(localTasks))
    }

    @Test
    fun getTasks_requestTaskFromLocalDataSourceById() = runBlocking {
        // When tasks are requested from the tasks repository
        val tasks = tasksRepository.getTask(localTasks[0].id, false) as Result.Success

        // Then tasks are loaded from the remote data source
        assertThat(tasks.data, IsEqual(localTasks[0]))
    }

    @Test
    fun getTasks_requestTaskFromRemoteDataSourceById() = runBlocking {
        // When tasks are requested from the tasks repository
        val tasks = tasksRepository.getTask(remoteTasks[0].id, true) as Result.Success

        // Then tasks are loaded from the remote data source
        assertThat(tasks.data, IsEqual(remoteTasks[0]))
    }

    @Test
    fun addTask_addTaskToRemoteDataSource() = runBlocking {
        tasksRemoteDataSource.saveTask(newTasks[0])

        val remoteData = tasksRepository.getTasks(true) as Result.Success

        remoteTasks.add(task4)
        remoteTasks.sortedBy {
            it.id
        }
        assertThat(remoteData.data, IsEqual(remoteTasks))
    }

    @Test
    fun addTask_addTaskToLocalDataSource() = runBlocking {

        tasksLocalDataSource.saveTask(newTasks[0])
        val localData = tasksRepository.getTasks(false) as Result.Success
        localTasks.add(task4)
        localTasks.sortedBy {
            it.id
        }
        assertThat(localData.data, IsEqual(localTasks))

    }

    @Test
    fun deleteAllTasks_dataListMustBeEmpty() = runTest {
        tasksRemoteDataSource.deleteAllTasks()
        tasksLocalDataSource.deleteAllTasks()

        val remoteData = tasksRepository.getTasks(true) as Result.Success
        val localData = tasksRepository.getTasks(false) as Result.Success

        assertThat(localData.data, IsEqual(emptyList()))
        assertThat(remoteData.data, IsEqual(emptyList()))
    }
}