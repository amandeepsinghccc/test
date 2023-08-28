package com.example.android.architecture.blueprints.todoapp.tasks


import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.android.architecture.blueprints.todoapp.Event
import com.example.android.architecture.blueprints.todoapp.MainCoroutineRule
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.fake.FakeTestRepository
import getOrAwaitValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.not
import org.hamcrest.Matchers.nullValue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import com.example.android.architecture.blueprints.todoapp.R
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.runner.RunWith

class TasksViewModelTest {

    private lateinit var tasksViewModel: TasksViewModel
    private lateinit var tasksRepository: FakeTestRepository

//    @ExperimentalCoroutinesApi
//    val testDispatcher  = UnconfinedTestDispatcher()
//
//    @ExperimentalCoroutinesApi
//    @Before
//    fun setupDispatcher() {
//        Dispatchers.setMain(testDispatcher)
//    }
//
//    @ExperimentalCoroutinesApi
//    @After
//    fun tearDownDispatcher() {
//        Dispatchers.resetMain()
//    }

//    @OptIn(ExperimentalCoroutinesApi::class)
//    @get:Rule
//    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setupViewModel() {
        tasksRepository = FakeTestRepository()
        val task1 = Task("Title1", "Description1")
        val task2 = Task("Title2", "Description2", true)
        val task3 = Task("Title3", "Description3", true)
        tasksRepository.addTasks(task1, task2, task3)

        tasksViewModel = TasksViewModel(tasksRepository)
    }

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Test
    fun addNewTask_setsNewTaskEvent() {

        tasksViewModel.addNewTask()

        val value = tasksViewModel.newTaskEvent.getOrAwaitValue()

        assertThat(value.getContentIfNotHandled(), not(nullValue()))

    }


    @Test
    fun setFilterAllTasks_tasksAddViewVisible() {

        tasksViewModel.setFiltering(TasksFilterType.ALL_TASKS)

        assertThat(tasksViewModel.tasksAddViewVisible.getOrAwaitValue(), `is`(true))
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun completeTask_dataAndSnackbarUpdated() {
        // Create an active task and add it to the repository.
        Dispatchers.setMain(UnconfinedTestDispatcher())

            val task = Task("Title", "Description")
            tasksRepository.addTasks(task)
            // Mark the task as complete task.

            tasksViewModel.completeTask(task, true)
            // Verify the task is completed.
            assertThat(tasksRepository.tasksServiceData[task.id]?.isCompleted, `is`(true))

        // Assert that the snackbar has been updated with the correct text.
        val snackbarText: Event<Int> = tasksViewModel.snackbarText.getOrAwaitValue()
        assertThat(snackbarText.getContentIfNotHandled(), `is`(R.string.task_marked_complete))
    }

}