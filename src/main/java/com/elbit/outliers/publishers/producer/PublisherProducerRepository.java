package com.elbit.outliers.publishers.producer;

import com.elbit.outliers.publishers.PublisherReading;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Mihai
 */
@Repository
public class PublisherProducerRepository {

	@Autowired
	private KafkaTemplate<String, PublisherReading> kafkaTemplate;

	public void publish(String publisher, LocalDateTime time, Integer... readings) {
		kafkaTemplate.sendDefault(new PublisherReading(publisher, time, readings));
	}
}
