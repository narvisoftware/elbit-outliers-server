package com.elbit.outliers.integration;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Mihai
 */
public class PublisherReading {
	private String publisher;
	private LocalDateTime time;
	private List<Integer> readings;

	public PublisherReading() {
	}
	
	public PublisherReading(String publisher, LocalDateTime time, Integer... readings) {
		this.publisher = publisher;
		this.time = time;
		this.readings = Arrays.asList(readings);
	}
	
	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public LocalDateTime getTime() {
		return time;
	}

	public void setTime(LocalDateTime time) {
		this.time = time;
	}

	public List<Integer> getReadings() {
		return readings;
	}

	public void setReadings(List<Integer> readings) {
		this.readings = readings;
	}

	@Override
	public String toString() {
		return "PublisherReading{" + "publisher=" + publisher + ", time=" + time + ", readings=" + readings + '}';
	}
	
}
