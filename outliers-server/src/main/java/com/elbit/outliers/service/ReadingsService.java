package com.elbit.outliers.service;

import com.elbit.outliers.domain.SensorData;
import com.elbit.outliers.domain.SensorsCollection;
import com.elbit.outliers.persistence.SensorsRepository;
import java.util.List;
import org.elasticsearch.action.search.SearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Mihai
 */
@Service
public class ReadingsService {
	
	@Autowired
	SensorsRepository repository;
	
	public void saveReading(SensorData sensorData) {
		repository.saveSensorRead(sensorData);
	}
	
	public SensorsCollection getReadings(String publisherName, Integer maxResults) {
		Double[] percentiles = repository.getPercentiles(publisherName, 25.0, 75.0);
		SensorsCollection collection = new SensorsCollection(percentiles[0], percentiles[1]);
		List<SensorData> data = repository.getReadings(publisherName, maxResults);
		collection.add(data);
		return collection;
	}
	
	public SensorData getLastReading() {
		return repository.getLastReading();
	}
	
}
