package com.elbit.outliers;

import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.kafka.test.EmbeddedKafkaBroker;

@SpringBootApplication
public class OutliersServerApplication {

	private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	private EmbeddedKafkaBroker embeddedKafka;

	@Autowired
	private Environment env;

	@Value(value = "${publishers.topic.name}")
	private String publishersTopicName;

	@Value(value = "${kafka.bootstrapPort}")
	private Integer bootstrapPort;

	public static void main(String[] args) {
		SpringApplication.run(OutliersServerApplication.class, args);
	}

	@PostConstruct
	private void startLocalServer() throws Exception {
		if (isDevEnv()) {
			this.embeddedKafka = new EmbeddedKafkaBroker(1, true, 2, publishersTopicName);
			this.embeddedKafka.kafkaPorts(bootstrapPort);
			this.embeddedKafka.setZkPort(2181);
			this.embeddedKafka.afterPropertiesSet();

			LOG.debug("Broker address: " + Arrays.toString(embeddedKafka.getBrokerAddresses()));
		}
	}

	private boolean isDevEnv() {
		return (env.acceptsProfiles(Profiles.of("default", "dev")));
	}

}
