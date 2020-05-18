package com.elbit.outliers.service;

import com.elbit.outliers.publishers.PublisherReading;
import com.elbit.outliers.domain.SensorData;
import com.elbit.outliers.publishers.producer.PublisherProducerRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Mihai
 */
@Service
public class DummyDataService {

	@Autowired
	PublisherProducerRepository publisherProducerRepository;

	public static final int READINGS_PER_SENSOR_COUNT = 8;
	public static final int SENSORS_READINGS_TO_GENERATE = 50;
	public static final int SENSORS_COUNT = 5;

	public DummyLoadResult loadData() {
		List<String> totalReads = new ArrayList<>();
		Random rnd = new Random();
		for (int i = 0; i < SENSORS_READINGS_TO_GENERATE; i++) {
			Integer[] readings = new Integer[8];
			for (int a = 0; a < READINGS_PER_SENSOR_COUNT; a++) {
				
				readings[a] = rnd.nextInt(2000);
			}
			String read = "dummy-publisher-" + rnd.nextInt(SENSORS_COUNT) + ", values: " + Arrays.toString(readings);
			totalReads.add(read);
			publisherProducerRepository.publish("dummy-publisher-" + rnd.nextInt(SENSORS_COUNT), LocalDateTime.now(), readings);
		}
		return new DummyLoadResult(SENSORS_READINGS_TO_GENERATE, totalReads);
	}
}
