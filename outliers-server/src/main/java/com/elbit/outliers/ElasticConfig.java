package com.elbit.outliers;

import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.springframework.kafka.annotation.EnableKafka;

import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableKafka
public class ElasticConfig {

	private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private static final String INDEX = "sensor-index";
	
	@Value(value = "${kafka.bootstrapHost}")
	private String bootstrapHost;

	@Value(value = "${kafka.bootstrapPort}")
	private Integer bootstrapPort;

	@Bean
	public RestHighLevelClient elasticsearchClient() throws Exception {

		RestHighLevelClient client = new RestHighLevelClient(
				RestClient.builder(new HttpHost("localhost", 9201, "http")));

		GetIndexRequest request = new GetIndexRequest(INDEX);
		boolean idxExists = client.indices().exists(request, RequestOptions.DEFAULT);

		if (!idxExists) {
			CreateIndexRequest indexRequest = new CreateIndexRequest(INDEX);
//			indexRequest.settings(getSettings());
			indexRequest.mapping(getMapping());
			CreateIndexResponse response = client.indices().create(indexRequest, RequestOptions.DEFAULT);
			LOG.info(response.index());
		}

		return client;
	}

	private Map getMapping() throws Exception {
		Map<String, Object> textType = new HashMap<>();
		textType.put("type", "keyword");

		Map<String, Object> timeType = new HashMap<>();
		timeType.put("type", "date_nanos");

		Map<String, Object> doubleType = new HashMap<>();
		doubleType.put("type", "double");

		Map<String, Object> properties = new HashMap<>();
		properties.put("id", textType);
		properties.put("publisher", textType);
		properties.put("time", timeType);
		properties.put("medianReading", doubleType);

		Map<String, Object> mapping = new HashMap<>();
		mapping.put("properties", properties);

		return mapping;
	}

}
