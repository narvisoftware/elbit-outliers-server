package com.elbit.outliers.persistence;

import com.elbit.outliers.domain.SensorData;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Mihai
 */
@Repository
public class SensorsRepository {

	private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Autowired
	private RestHighLevelClient client;

	@Autowired
	private ObjectMapper objectMapper;

	private static final String INDEX = "sensor-index";

	public void saveSensorRead(SensorData data) {
		Map dataMap = objectMapper.convertValue(data, Map.class);
		IndexRequest indexRequest = new IndexRequest(INDEX)
				.id(UUID.randomUUID().toString())
				.source(dataMap);
		try {
			IndexResponse response = client.index(indexRequest, RequestOptions.DEFAULT);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public List<SensorData> getReadings(String publisherName, int maxResults) {
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.query(QueryBuilders.termQuery("publisher", publisherName));
		sourceBuilder.from(0);
		sourceBuilder.size(maxResults);
		sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
//		sourceBuilder.sort(new FieldSortBuilder("time").order(SortOrder.DESC));

		SearchRequest searchRequest = new SearchRequest();
		searchRequest.source(sourceBuilder);

		SearchResponse searchResponse;
		try {
			searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
			List<SearchHit> searchHits = Arrays.asList(searchResponse.getHits().getHits());
			List<SensorData> results = new ArrayList<SensorData>();
			for (SearchHit hit : searchHits) {
				results.add(objectMapper.readValue(hit.getSourceAsString(), SensorData.class));
			}
			return results;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
