package sample.task;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SampleTask {
	@Scheduled(cron = "${sample.cron}")
	public void doTask() {
		System.out.println("*** doTask");
	}
}
