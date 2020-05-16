package com.elbit.outliers.publishers.consumer;

import com.elbit.outliers.domain.SensorData;
import com.elbit.outliers.publishers.PublisherReading;
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
	public void greetingListener(PublisherReading publisherReading) {
		SensorData data = new SensorData(publisherReading.getPublisher(), publisherReading.getTime());
		data.setMedian(publisherReading.getReadings());
		readingsService.saveReading(data);
		LOG.debug("Recieved reading: " + publisherReading + ", median: " + data.getMedianReading());
	}
}
