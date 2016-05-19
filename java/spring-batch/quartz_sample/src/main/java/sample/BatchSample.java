package sample;

import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@Configuration
@EnableBatchProcessing
@EnableAutoConfiguration
public class BatchSample {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean(name = "step1")
    public Step step1() {
        return stepBuilderFactory.get("step1")
                .tasklet( (cont, chunk) -> {
                    System.out.println(cont);
                    System.out.println(chunk);

                    System.out.println("***************** process step1");

                    Thread.sleep(10000);

                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    public Job job(@Qualifier("step1") Step step1) {
        return jobBuilderFactory.get("job1")
                .incrementer(new RunIdIncrementer())
                .start(step1)
                .build();
    }

    @Bean
    public MethodInvokingJobDetailFactoryBean jobDetail() {
        MethodInvokingJobDetailFactoryBean res = new MethodInvokingJobDetailFactoryBean();

        res.setTargetClass(SampleJobDetail.class);
        res.setTargetMethod("execute");

        return res;
    }

    @Bean
    public CronTriggerFactoryBean cronTrigger(JobDetail jobDetail) {
        CronTriggerFactoryBean res = new CronTriggerFactoryBean();

        res.setCronExpression("0/5 * * * * ?");
        res.setJobDetail(jobDetail);

        return res;
    }

    @Bean
    public SchedulerFactoryBean scheduler(Trigger trigger) {
        SchedulerFactoryBean res = new SchedulerFactoryBean();

        res.setTriggers(trigger);

        return res;
    }

    static class SampleJobDetail {
        public static void execute() {
            System.out.println("*** run jobDetail");
        }
    }
}

