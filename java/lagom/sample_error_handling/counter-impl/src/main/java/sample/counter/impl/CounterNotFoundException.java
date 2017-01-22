package sample.counter.impl;

import lombok.Value;

@Value
public class CounterNotFoundException extends Exception {
	private String id;
}
