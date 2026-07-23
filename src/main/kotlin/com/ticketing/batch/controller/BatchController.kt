package com.ticketing.batch.controller

import org.springframework.batch.core.job.Job
import org.springframework.batch.core.job.parameters.JobParametersBuilder
import org.springframework.batch.core.launch.JobOperator
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/batch")
class BatchController(
    private val jobOperator: JobOperator,
    private val nearSelloutStatsJob: Job
) {

    @PostMapping("/near-sellout")
    fun runNearSelloutStatsJob(): String {
        val jobParameters = JobParametersBuilder()
            .addLong("timestamp", System.currentTimeMillis())
            .toJobParameters()

        jobOperator.start(nearSelloutStatsJob, jobParameters)
        return "배치 실행 완료"
    }
}