package com.elbit.outliers.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 *
 * @author Mihai
 */
public class SensorsCollection {

	private final double q1;
	private final double q3;
	private final double lowerLimit;
	private final double upperLimit;

	private final List<Outlier> readings = new ArrayList<>();

	public SensorsCollection(double q1, double q3) {
		this.q1 = q1;
		this.q3 = q3;
		this.lowerLimit = q1 - 1.5 * (q3 - q1);
		this.upperLimit = q3 + 1.5 * (q3 - q1);
	}

	public void add(List<SensorData> data) {
		readings.addAll(data.stream().map(d -> new Outlier(d)).collect(Collectors.toList()));
	}
	
	public void add(SensorData data) {
		readings.add(new Outlier(data));
	}
	
	public Outlier getById(String id) {
		for(Outlier read : readings) {
			if(read.getId().equals(id)) {
				return read;
			}
		}
		throw new NoSuchElementException("Cannot find element " + id);
	}

	public List<Outlier> getSensorDatas() {
		return Collections.unmodifiableList(readings);
	}

	public class Outlier {

		private SensorData sensorData;

		public Outlier(SensorData sensorData) {
			this.sensorData = sensorData;
		}
		
		public String getId() {
			return sensorData.getId();
		}
		
		public String getPublisher() {
			return sensorData.getPublisher();
		}

		public LocalDateTime getTime() {
			return sensorData.getTime();
		}

		public Double getMedianReading() {
			return sensorData.getMedianReading();
		}
		
		public boolean isOutlier() {
			return (sensorData.getMedianReading() < SensorsCollection.this.lowerLimit || sensorData.getMedianReading() > SensorsCollection.this.upperLimit);
		}

	}
}
