
@Grab('org.springframework.boot:spring-boot-starter-batch:1.1.9.RELEASE')
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

    @Bean
    Step step1() {
        stepBuilderFactory.get("step1").tasklet { cont, ctx -> 
            println "*** step1 cont: ${cont}, context: ${ctx}"
        }.build()
    }

	// @Bean を使うと job へ割り当てる Step が特定できずにエラーとなる
    //@Bean
    Step step2() {
    	def value = 'sample'

        stepBuilderFactory.get("step2").chunk(1)
        	.reader {
        		// 値が無くなれば null を返す必要あり
        		// 値を返すと無限に実施される
        		def res = value
				if (value) {
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
    Job job(Step step1) throws Exception {
        jobBuilderFactory.get("job1")
            .incrementer(new RunIdIncrementer())
            .start(step1)
            .next(step2())
            .build()
    }
}

SpringApplication.run(SampleBatch, args)
