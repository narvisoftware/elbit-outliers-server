package com.elbit.outliers.service;

import com.elbit.outliers.domain.SensorData;
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
	
	private static final int DEFAULT_MAX_RESULTS = 10;
	
	public void saveReading(SensorData sensorData) {
		repository.saveSensorRead(sensorData);
	}
	
	public List<SensorData> getReadings(String publisherName, Integer maxResults) {
		if(maxResults == null) {
			maxResults = DEFAULT_MAX_RESULTS;
		}
		return repository.getReadings(publisherName, maxResults);
	}
	
}
