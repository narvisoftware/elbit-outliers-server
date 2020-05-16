package com.elbit.outliers.service;

import java.util.List;

/**
 *
 * @author Mihai
 */
public class DummyLoadResult {
	private int publishCount;
	private List<String> readsInfo;

	public DummyLoadResult(int publishCount, List<String> readsInfo) {
		this.publishCount = publishCount;
		this.readsInfo = readsInfo;
	}
	
	public int getPublishCount() {
		return publishCount;
	}

	public void setPublishCount(int publishCount) {
		this.publishCount = publishCount;
	}

	public List<String> getReadsInfo() {
		return readsInfo;
	}

	public void setReadsInfo(List<String> readsInfo) {
		this.readsInfo = readsInfo;
	}
	
}
