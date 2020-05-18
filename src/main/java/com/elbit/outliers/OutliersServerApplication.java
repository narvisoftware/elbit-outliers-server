package com.elbit.outliers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import static org.codelibs.elasticsearch.runner.ElasticsearchClusterRunner.newConfigs;

import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.UUID;
import javax.annotation.PostConstruct;
import org.codelibs.elasticsearch.runner.ElasticsearchClusterRunner;
import org.elasticsearch.common.settings.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.kafka.test.EmbeddedKafkaBroker;

@SpringBootApplication
public class OutliersServerApplication {

	private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private EmbeddedKafkaBroker embeddedKafka;

	private ElasticsearchClusterRunner runner;

	@Autowired
	private Environment env;

	@Value(value = "${publishers.topic.name}")
	private String publishersTopicName;

	@Value(value = "${kafka.bootstrapPort}")
	private Integer kafkaBootstrapPort;

	@Value(value = "${elasticsearch.bootstrapPort}")
	private Integer elasticsearchBootstrapPort;

	private final String ELASTIC_CLUSTER_NAME = "es-cl-run";
	private final int ELASTIC_NUM_OF_NODES = 3;

	public static void main(String[] args) {
		SpringApplication.run(OutliersServerApplication.class, args);
	}

	@PostConstruct
	private void startKafkaLocalServer() throws Exception {
		if (isDevEnv()) {
			this.embeddedKafka = new EmbeddedKafkaBroker(1, true, 2, publishersTopicName);
			this.embeddedKafka.kafkaPorts(kafkaBootstrapPort);
			this.embeddedKafka.setZkPort(2181);
			this.embeddedKafka.afterPropertiesSet();

			LOG.debug("Broker address: " + Arrays.toString(embeddedKafka.getBrokerAddresses()));
		}
	}

	@PostConstruct
	private void startElasticsearchLocalServer() throws Exception {
		if (isDevEnv()) {
//			runner = new ElasticsearchClusterRunner();
//			runner.onBuild((int number, Settings.Builder settingsBuilder) -> {
//				settingsBuilder.put("http.cors.enabled", true);
//				settingsBuilder.put("http.cors.allow-origin", "*");
//				settingsBuilder.putList("discovery.seed_hosts", "127.0.0.1:" + elasticsearchBootstrapPort, "127.0.0.1:9302");
//				settingsBuilder.putList("cluster.initial_master_nodes", "127.0.0.1:" + elasticsearchBootstrapPort);
//			}).build(newConfigs().clusterName(ELASTIC_CLUSTER_NAME).numOfNode(ELASTIC_NUM_OF_NODES));
//			runner.ensureGreen();

			runner = new ElasticsearchClusterRunner();
			// create ES nodes
			runner.onBuild(new ElasticsearchClusterRunner.Builder() {
				@Override
				public void build(final int number, final Settings.Builder settingsBuilder) {
				}
			}).build(
					newConfigs().numOfNode(1)
							.clusterName(UUID.randomUUID().toString()));

			runner.ensureGreen();
		}
	}

	private boolean isDevEnv() {
		LOG.error("ACTIVE SPRING PROFILE: " + Arrays.toString(env.getActiveProfiles()));
		return (env.acceptsProfiles(Profiles.of("default", "dev")));
	}

	@Bean
	public ObjectMapper mapper() {
		ObjectMapper objectMapper = new ObjectMapper();

		JavaTimeModule javaTimeModule = new JavaTimeModule();
		Jdk8Module jdk8Module = new Jdk8Module();

		objectMapper.registerModule(javaTimeModule);
		objectMapper.registerModule(jdk8Module);

		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		return objectMapper;
	}

}
