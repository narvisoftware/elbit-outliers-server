package com.elbit.outliers;

import com.elbit.outliers.publishers.PublisherReading;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@Configuration
@EnableKafka
public class KafkaConfig {

	@Value(value = "${kafka.bootstrapHost}")
	private String bootstrapHost;

	@Value(value = "${kafka.bootstrapPort}")
	private Integer bootstrapPort;

	@Value(value = "${publishers.topic.name}")
	private String publishersTopicName;

	@Bean
	public ProducerFactory<String, PublisherReading> publisherProducerFactory() {
		Map<String, Object> configProps = new HashMap<>();
		configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapHost + ":" + bootstrapPort);
		configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
		return new DefaultKafkaProducerFactory<>(configProps);
	}

	@Bean
	public KafkaTemplate<String, PublisherReading> publisherKafkaTemplate() {
		KafkaTemplate template = new KafkaTemplate<>(publisherProducerFactory());
		template.setDefaultTopic(publishersTopicName);
		return template;
	}

	public ConsumerFactory<String, PublisherReading> greetingConsumerFactory() {
		Map<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapHost + ":" + bootstrapPort);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, publishersTopicName);
		return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new JsonDeserializer<>(PublisherReading.class));
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, PublisherReading> publisherReadingsKafkaListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, PublisherReading> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(greetingConsumerFactory());
		return factory;
	}

}
