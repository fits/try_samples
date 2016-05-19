package sample;

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
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;
import java.sql.DriverManager;
import java.sql.SQLException;

@Configuration
@EnableBatchProcessing
@EnableAutoConfiguration
public class BatchSample {
    private final static String DS_URL = "jdbc:mysql://localhost/batch?user=root";

    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean(name = "dataSource")
    public DataSource dataSource() throws SQLException {
        SimpleDriverDataSource ds = new SimpleDriverDataSource();

        ds.setUrl(DS_URL);
        ds.setDriver(DriverManager.getDriver(DS_URL));

        return ds;
    }

    @Bean(name = "step1")
    public Step step1() {
        return stepBuilderFactory.get("step1")
                .tasklet( (cont, chunk) -> {
                    System.out.println(cont);
                    System.out.println(chunk);

                    System.out.println("*** process step1");

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
}
