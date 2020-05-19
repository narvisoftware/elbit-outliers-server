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
		return repository.getReadings(publisherName, maxResults);
	}
	
	public SensorData getLastReading() {
		return repository.getLastReading();
	}
	
}
