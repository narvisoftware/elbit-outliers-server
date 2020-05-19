package com.elbit.outliers.service;

import com.elbit.outliers.domain.SensorData;
import com.elbit.outliers.persistence.SensorsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Mihai
 */
@Service
public class OutliersService {
	
	@Autowired
	SensorsRepository repository;
	
	public void getOutliers(SensorData sensorData) {
		repository.saveSensorRead(sensorData);
	}
	
}
