package com.example.android.architecture.blueprints.todoapp

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import kotlin.coroutines.CoroutineContext

@ExperimentalCoroutinesApi
class MainCoroutineRule():TestWatcher() {
    private val testDispatcher =  UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher)
    override fun starting(description: Description) {
        super.starting(description)
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        super.finished(description)
        Dispatchers.resetMain()
    }

    fun runBlockingTest(block: suspend TestScope.() -> Unit) {
        Dispatchers.resetMain()
        TestScope(StandardTestDispatcher()).runTest { block() }
    }

    fun runUnConfinedTest(block: suspend TestScope.() -> Unit) {
        Dispatchers.resetMain()
        TestScope(UnconfinedTestDispatcher()).runTest { block() }
    }
}