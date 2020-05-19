package com.elbit.outliers.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.apache.commons.math3.stat.descriptive.rank.Median;

/**
 *
 * @author Mihai
 */
public class SensorData {
	
	private String id = UUID.randomUUID().toString();
	
	private String publisher;
	
	private LocalDateTime time;
	
	private Double medianReading;

	public SensorData() {
	}

	public SensorData(String publisher, LocalDateTime time) {
		this.publisher = publisher;
		this.time = time;
	}
	
	public SensorData(String publisher, LocalDateTime time, Double median) {
		this.publisher = publisher;
		this.time = time;
		this.medianReading = median;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public void setMedian(List<Integer> values) {
		double[] doubleValues = values.stream().mapToDouble(num -> (double)num).toArray();
		medianReading = new Median().evaluate(doubleValues);
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

	public Double getMedianReading() {
		return medianReading;
	}

	public void setMedianReading(Double medianReading) {
		this.medianReading = medianReading;
	}
}
