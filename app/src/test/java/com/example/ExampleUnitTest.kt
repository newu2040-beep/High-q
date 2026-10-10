package com.example

import com.example.model.ExportConfiguration
import com.example.model.VideoProject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testDurationFormatting() {
        val project1 = VideoProject(id = "1", title = "test1", durationSeconds = 45)
        assertEquals("00:45", project1.formattedDuration)

        val project2 = VideoProject(id = "2", title = "test2", durationSeconds = 72)
        assertEquals("01:12", project2.formattedDuration)

        val project3 = VideoProject(id = "3", title = "test3", durationSeconds = 125)
        assertEquals("02:05", project3.formattedDuration)
    }

    @Test
    fun testExportEstimatedSizeCalculation() {
        val config = ExportConfiguration(
            resolution = "4K",
            fps = 30,
            bitrateMbps = 20,
            format = "MP4"
        )
        // 20 Mbps * 45 seconds * 1.2 / 8 = 135 MB
        val size = config.calculateEstimatedSize(45)
        assertNotNull(size)
        assertEquals("135 MB", size)
    }
}
