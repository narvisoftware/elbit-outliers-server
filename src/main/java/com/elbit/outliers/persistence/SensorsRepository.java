package com.elbit.outliers.persistence;

import com.elbit.outliers.domain.NullSensorData;
import com.elbit.outliers.domain.SensorData;
import com.elbit.outliers.domain.SensorsCollection;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.metrics.Percentile;
import org.elasticsearch.search.aggregations.metrics.Percentiles;
import org.elasticsearch.search.aggregations.metrics.PercentilesAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
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

	public SensorsCollection getReadings(String publisherName, int maxResults) {

		SensorsCollection sensors = getSensorsCollection(publisherName);

		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.query(QueryBuilders.termQuery("publisher", publisherName));
		sourceBuilder.from(0);
		sourceBuilder.size(maxResults);
		sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
		sourceBuilder.sort(new FieldSortBuilder("time").order(SortOrder.DESC));

		SearchRequest searchRequest = new SearchRequest();
		searchRequest.source(sourceBuilder);

		SearchResponse searchResponse;
		try {
			searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
			List<SearchHit> searchHits = Arrays.asList(searchResponse.getHits().getHits());
			for (SearchHit hit : searchHits) {
				sensors.add(objectMapper.readValue(hit.getSourceAsString(), SensorData.class));
			}
			return sensors;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private SensorsCollection getSensorsCollection(String publisherName) {
		SearchSourceBuilder search = new SearchSourceBuilder();
		search.query(QueryBuilders.termQuery("publisher", publisherName));
		PercentilesAggregationBuilder aggregation
				= AggregationBuilders
						.percentiles("agg")
						.field("medianReading")
						.percentiles(25.0, 75.0);
		search.aggregation(aggregation);
		SearchRequest request = new SearchRequest();
		request.source(search);
		SearchResponse response;
		try {
			response = client.search(request, RequestOptions.DEFAULT);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		double q1 = 0;
		double q3 = 0;
		Percentiles agg = response.getAggregations().get("agg");
		for (Percentile entry : agg) {
			double percent = entry.getPercent();
			double value = entry.getValue();
			LOG.info("percent [{}], value [{}]", percent, value);
			if (percent == 25.0) {
				q1 = value;
			}
			if (percent == 75.0) {
				q3 = value;
			}
		}

		return new SensorsCollection(q1, q3);

	}

	public SensorData getLastReading() {
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.from(0);
		sourceBuilder.size(1);
		sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

		SearchRequest searchRequest = new SearchRequest();
		searchRequest.source(sourceBuilder);

		SearchResponse searchResponse;
		try {
			searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
			List<SearchHit> searchHits = Arrays.asList(searchResponse.getHits().getHits());
			for (SearchHit hit : searchHits) {
				return objectMapper.readValue(hit.getSourceAsString(), SensorData.class);
			}
			return new NullSensorData();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
