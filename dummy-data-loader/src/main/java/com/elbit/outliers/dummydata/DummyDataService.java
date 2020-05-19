package com.elbit.outliers.dummydata;

import java.time.LocalDateTime;
import java.util.Arrays;
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
		Random rnd = new Random();
		DummyLoadResult result = new DummyLoadResult(SENSORS_READINGS_TO_GENERATE);
		for (int i = 0; i < SENSORS_READINGS_TO_GENERATE; i++) {
			Integer[] readings = new Integer[READINGS_PER_SENSOR_COUNT];
			for (int a = 0; a < READINGS_PER_SENSOR_COUNT; a++) {
				readings[a] = rnd.nextInt(2000);
			}
			
			if ((i+1) % 9 == 0) {
				//insert low outlier
				for (int a = 0; a < READINGS_PER_SENSOR_COUNT; a++) {
					readings[a] = -100 * readings[a];
				}
			}
			
			if ((i+1) % 10 == 0) {
				//insert high outlier
				for (int a = 0; a < READINGS_PER_SENSOR_COUNT; a++) {
					readings[a] = 100 * readings[a];
				}
			}
			
			String name = "dummy-publisher-" + rnd.nextInt(SENSORS_COUNT);
			result.add(name, Arrays.toString(readings));
			publisherProducerRepository.publish(name, LocalDateTime.now(), readings);
		}
		return result;
	}
}
