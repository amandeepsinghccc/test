package com.example.android.architecture.blueprints.todoapp.statistics

import com.example.android.architecture.blueprints.todoapp.data.Task
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class StatisticsUtilsTest{
    @Test
    fun getActiveAndCompletedStats_noCompleted_returnsHundredZero(){
        val tasks = listOf<Task>(
            Task("Title","desc", isCompleted = false),
        )
        val result = getActiveAndCompletedStats(tasks)
        assertThat(result.activeTasksPercent,`is`(100f))
        assertThat(result.completedTasksPercent, `is`(0f))
    }

    @Test
    fun getActiveAndCompletedStats_noActive_returnsZeroHundred(){
        val tasks = listOf<Task>(
            Task("Title","desc", isCompleted = true),
        )
        val result = getActiveAndCompletedStats(tasks)
        assertThat(result.activeTasksPercent,`is`(0f))
        assertThat(result.completedTasksPercent, `is`(100f))
    }

    @Test
    fun getActiveAndCompletedStats_empty_returnsZero(){
        val tasks = listOf<Task>(
        )
        val result = getActiveAndCompletedStats(tasks)
        assertThat(result.activeTasksPercent,`is`(0f))
        assertThat(result.completedTasksPercent, `is`(0f))
    }
    @Test
    fun getActiveAndCompletedStats_error_returnsZero(){
        val tasks = null
        val result = getActiveAndCompletedStats(tasks)
        assertThat(result.activeTasksPercent,`is`(0f))
        assertThat(result.completedTasksPercent, `is`(0f))
    }
    @Test
    fun getActiveAndCompletedStats_both_returnsSixtyForty(){
        val tasks = listOf<Task>(
            Task("Title","desc", isCompleted = false),
            Task("Title","desc", isCompleted = false),
            Task("Title","desc", isCompleted = false),
            Task("Title","desc", isCompleted = true),
            Task("Title","desc", isCompleted = true),
        )
        val result = getActiveAndCompletedStats(tasks)
        assertThat(result.activeTasksPercent,`is`(60f))
        assertThat(result.completedTasksPercent, `is`(40f))
    }
}