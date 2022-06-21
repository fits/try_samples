package com.example.batch;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Component
public class SampleTasklet implements Tasklet {
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        var ctx = chunkContext.getStepContext().getStepExecutionContext();

        System.out.println("*** execute: " + ctx.get("name"));

        Thread.currentThread().sleep(3000);

        return RepeatStatus.FINISHED;
    }
}
