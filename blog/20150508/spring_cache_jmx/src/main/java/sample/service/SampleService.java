package sample.service;

import org.springframework.stereotype.Service;

import javax.cache.annotation.CacheResult;

@Service
public class SampleService {
	@CacheResult(cacheName = "sample")
	public String sample(String id) {
		return "sample: " + id + ", " + System.currentTimeMillis();
	}
}
