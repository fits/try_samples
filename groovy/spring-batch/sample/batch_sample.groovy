
@GrabConfig(systemClassLoader = true)
@Grab('org.springframework.boot:spring-boot-starter-batch:1.3.3.RELEASE')
@Grab('org.codehaus.jettison:jettison:1.3.7')
import org.springframework.batch.core.*
import org.springframework.batch.core.configuration.annotation.*
import org.springframework.beans.factory.annotation.*
import org.springframework.boot.autoconfigure.*
import org.springframework.context.annotation.*

import org.springframework.boot.SpringApplication
import org.springframework.batch.core.launch.support.RunIdIncrementer

@Configuration
@EnableBatchProcessing
@EnableAutoConfiguration
class SampleBatch {
    @Autowired
    private JobBuilderFactory jobBuilderFactory
    @Autowired
    private StepBuilderFactory stepBuilderFactory

    @Bean(name = 'step1')
    Step step1() {
        stepBuilderFactory.get("step1").tasklet { cont, ctx -> 
            println "*** step1 cont: ${cont}, context: ${ctx}"
        }.build()
    }

    @Bean(name = 'step2')
    Step step2() {
        def value = 'sample'

        stepBuilderFactory.get("step2").chunk(1)
            .reader {
                def res = value
                if (value) {
                    // 処理を終了する際に null を返す（この処理が何度も実行される）
                    value = null
                }
                res
            }.processor {
                "${it}!!!"
            }.writer {
                println "### result : $it"
            }.build()
    }

    @Bean
    Job job(
        @Qualifier('step1') Step step1, 
        @Qualifier('step2') Step step2
    ) throws Exception {
        jobBuilderFactory.get("job1")
            .incrementer(new RunIdIncrementer())
            .start(step1)
            .next(step2)
            .build()
    }
}

SpringApplication.run(SampleBatch, args)
