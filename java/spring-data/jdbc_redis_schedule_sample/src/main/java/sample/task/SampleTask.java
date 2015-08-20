package sample.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import sample.service.SampleService;

import java.util.List;

@Component
public class SampleTask {
	private final static Logger log = LoggerFactory.getLogger(SampleTask.class);

	@Autowired
	private SampleService sampleService;

	@Scheduled(cron = "${sample.cron}")
	public void doTask() {
		try {
			List<String> res = sampleService.deleteExpired();
			printLog(res);
		} catch (Exception ex) {
			log.error("failed", ex);
		}
	}

	private void printLog(List<String> res) {
		log.info("deleted rows : {}", res.size());

		if (log.isDebugEnabled()) {
			res.forEach(r -> log.debug("id = {}", r));
		}
	}
}
