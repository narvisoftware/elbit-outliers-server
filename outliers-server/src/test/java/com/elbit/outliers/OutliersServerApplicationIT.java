package com.elbit.outliers;

import com.elbit.outliers.domain.SensorData;
import com.elbit.outliers.domain.SensorsCollection;
import com.elbit.outliers.persistence.SensorsRepository;
import com.elbit.outliers.service.ReadingsService;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class OutliersServerApplicationIT {

	@Autowired
	SensorsRepository sensorsRepository;
	
	@Autowired
	ReadingsService readingsService;

	@Test
	void testOutliersComputation() {

		for (int a = 0; a < 10; a++) {
			SensorData lowValue = new SensorData("dummy", LocalDateTime.now(), 100d);
			SensorData highValue = new SensorData("dummy", LocalDateTime.now(), 150d);
			sensorsRepository.saveSensorReadSyncronously(lowValue);
			sensorsRepository.saveSensorReadSyncronously(highValue);
		}

		SensorData insideMinEdge = new SensorData("dummy", LocalDateTime.now(), 25d);
		SensorData insideMaxEdge = new SensorData("dummy", LocalDateTime.now(), 225d);

		SensorData outsideMinEdge = new SensorData("dummy", LocalDateTime.now(), 24d);
		SensorData outsideMaxEdge = new SensorData("dummy", LocalDateTime.now(), 226d);

		sensorsRepository.saveSensorReadSyncronously(insideMinEdge);
		sensorsRepository.saveSensorReadSyncronously(insideMaxEdge);
		sensorsRepository.saveSensorReadSyncronously(outsideMinEdge);
		sensorsRepository.saveSensorReadSyncronously(outsideMaxEdge);

		SensorsCollection collection = readingsService.getReadings("dummy", 200);

		Assertions.assertEquals(24, collection.getSensorDatas().size(), 24, "Incorect number of inserts");
		
		Assertions.assertFalse(collection.getById(insideMinEdge.getId()).isOutlier(), "Incorect outlier detection");
		Assertions.assertFalse(collection.getById(insideMaxEdge.getId()).isOutlier(), "Incorect outlier detection");

		Assertions.assertTrue(collection.getById(outsideMinEdge.getId()).isOutlier(), "Incorect outlier detection");
		Assertions.assertTrue(collection.getById(outsideMaxEdge.getId()).isOutlier(), "Incorect outlier detection");

	}

}
