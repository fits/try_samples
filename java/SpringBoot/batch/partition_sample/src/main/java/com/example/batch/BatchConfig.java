package com.example.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

@Configuration
@EnableBatchProcessing
public class BatchConfig {
    @Autowired
    private JobBuilderFactory jobs;
    @Autowired
    private StepBuilderFactory steps;

    @Bean
    public Job job1(Step step1Manager) {
        return jobs.get("job1")
                .start(step1Manager)
                .build();
    }

    @Bean
    public Step step1Manager(SamplePartitioner partitioner, Step step1) {
        return steps.get("step1.manager")
                .partitioner("p1", partitioner)
                .step(step1)
                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean
    public Step step1(SampleTasklet tasklet) {
        return steps.get("step1")
                .tasklet(tasklet)
                .build();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        return new SimpleAsyncTaskExecutor();
    }
}
