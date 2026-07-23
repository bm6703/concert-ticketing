package com.ticketing.batch.job

import com.ticketing.tickettype.domain.TicketType
import com.ticketing.tickettype.repository.TicketTypeRepository
import org.slf4j.LoggerFactory
import org.springframework.batch.core.job.Job
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.Step
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.infrastructure.item.ItemProcessor
import org.springframework.batch.infrastructure.item.ItemWriter
import org.springframework.batch.infrastructure.item.support.ListItemReader
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.batch.core.configuration.annotation.StepScope

@Configuration
class NearSelloutStatsJobConfig(
    private val ticketTypeRepository: TicketTypeRepository
) {

    private val log = LoggerFactory.getLogger(NearSelloutStatsJobConfig::class.java)

    @Bean
    @StepScope
    fun nearSelloutStatsReader(): ListItemReader<TicketType> {
        return ListItemReader(ticketTypeRepository.findAll())
    }

    @Bean
    fun nearSelloutStatsProcessor(): ItemProcessor<TicketType, TicketType> {
        return ItemProcessor { ticketType ->
            if (ticketType.totalQuantity <= 0) {
                null
            } else {
                val remainingRatio = ticketType.remainingQuantity.toDouble() / ticketType.totalQuantity.toDouble()
                if (remainingRatio <= 0.1) ticketType else null
            }
        }
    }

    @Bean
    fun nearSelloutStatsWriter(): ItemWriter<TicketType> {
        return ItemWriter { chunk ->
            chunk.forEach { ticketType ->
                log.info(
                    "[매진임박] 콘서트=${ticketType.concert.title}, 등급=${ticketType.gradeName}, " +
                            "잔여수량=${ticketType.remainingQuantity}/${ticketType.totalQuantity}"
                )
            }
        }
    }

    @Bean
    fun nearSelloutStatsStep(
        jobRepository: JobRepository,
        transactionManager: PlatformTransactionManager
    ): Step {
        return StepBuilder("nearSelloutStatsStep", jobRepository)
            .chunk<TicketType, TicketType>(10, transactionManager)
            .reader(nearSelloutStatsReader())
            .processor(nearSelloutStatsProcessor())
            .writer(nearSelloutStatsWriter())
            .build()
    }

    @Bean
    fun nearSelloutStatsJob(
        jobRepository: JobRepository,
        nearSelloutStatsStep: Step
    ): Job {
        return JobBuilder("nearSelloutStatsJob", jobRepository)
            .start(nearSelloutStatsStep)
            .build()
    }
}