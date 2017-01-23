package sample.counter.api;

import lombok.Value;

@Value
public class Counter {
	private String id;
	private String name;
	private int count;
}
