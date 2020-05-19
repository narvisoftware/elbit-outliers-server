package com.elbit.outliers.integration;

import com.elbit.outliers.domain.SensorData;
import com.elbit.outliers.integration.PublisherReading;
import com.elbit.outliers.service.ReadingsService;
import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 *
 * @author Mihai
 */
@Component
public class PublisherConsumer {
	private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	@Autowired
	private ReadingsService readingsService;

	@KafkaListener(topics = "${publishers.topic.name}", containerFactory = "publisherReadingsKafkaListenerContainerFactory")
	public void publisherListener(PublisherReading publisherReading) {
		SensorData data = new SensorData(publisherReading.getPublisher(), publisherReading.getTime());
		data.setMedian(publisherReading.getReadings());
		LOG.debug("Recieved reading: " + publisherReading + ", median: " + data.getMedianReading());
		readingsService.saveReading(data);
	}
}
