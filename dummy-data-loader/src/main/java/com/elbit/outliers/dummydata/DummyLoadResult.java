package com.elbit.outliers.dummydata;

import java.util.ArrayList;
import java.util.List;
import javafx.util.Pair;

/**
 *
 * @author Mihai
 */
public class DummyLoadResult {
	private int publishCount;
	private List<Pair<String, String>> readsInfo = new ArrayList<>();

	public DummyLoadResult(int publishCount) {
		this.publishCount = publishCount;
	}
	
	public void add(String name, String readings) {
		readsInfo.add(new Pair(name, readings));
	}
	
	public int getPublishCount() {
		return publishCount;
	}

	public void setPublishCount(int publishCount) {
		this.publishCount = publishCount;
	}

	public List<Pair<String, String>> getReadsInfo() {
		return readsInfo;
	}

	public void setReadsInfo(List<Pair<String, String>> readsInfo) {
		this.readsInfo = readsInfo;
	}
	
}
