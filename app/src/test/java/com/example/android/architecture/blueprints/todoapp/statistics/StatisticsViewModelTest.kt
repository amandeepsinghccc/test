package com.example.android.architecture.blueprints.todoapp.statistics

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.android.architecture.blueprints.todoapp.MainCoroutineRule
import com.example.android.architecture.blueprints.todoapp.data.source.fake.FakeTestRepository
import getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.pauseDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.resumeDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.Assertions.*

@ExperimentalCoroutinesApi
class StatisticsViewModelTest{
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

//    @get:Rule
//    var mainCoroutineRule= MainCoroutineRule()

    // Subject under test
    private lateinit var statisticsViewModel: StatisticsViewModel

    // Use a fake repository to be injected into the view model.
    private lateinit var tasksRepository: FakeTestRepository

    @Before
    fun setupStatisticsViewModel() {
        // Initialise the repository with no tasks.
        tasksRepository = FakeTestRepository()

        statisticsViewModel = StatisticsViewModel(tasksRepository)
    }

    @Test
    fun loadTasks_loading() {
        Dispatchers.setMain(StandardTestDispatcher())
        // Load the task in the view model.
            TestScope(UnconfinedTestDispatcher()).runTest {
                statisticsViewModel.refresh()
                // Then progress indicator is shown.
                assertThat(statisticsViewModel.dataLoading.getOrAwaitValue(), `is`(true))
            }
                assertThat(statisticsViewModel.dataLoading.getOrAwaitValue(), `is`(false))
    }

    @Test
    fun loadStatisticsWhenTasksAreUnavailable_callErrorToDisplay() {
        // Make the repository return errors.
        tasksRepository.setReturnError(true)
        Dispatchers.setMain(UnconfinedTestDispatcher())
            statisticsViewModel.refresh()

            // Then empty and error are true (which triggers an error message to be shown).
            assertThat(statisticsViewModel.empty.getOrAwaitValue(), `is`(true))
            assertThat(statisticsViewModel.error.getOrAwaitValue(), `is`(true))

    }
}