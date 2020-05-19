package com.elbit.outliers.controllers;

import com.elbit.outliers.domain.SensorData;
import com.elbit.outliers.domain.SensorsCollection;
import com.elbit.outliers.dummydata.DummyDataService;
import com.elbit.outliers.dummydata.DummyLoadResult;
import com.elbit.outliers.service.ReadingsService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author Mihai
 */
@Controller
public class OutliersController {

	@Autowired
	private ReadingsService readingsService;
	
	private static final int DEFAULT_MAX_RESULTS = 10;
	
    @GetMapping("/outliers")
    public String outliers(Model model, @RequestParam(required = false) String publisherName, @RequestParam(required = false) Integer maxResults) {
		if(publisherName == null) {
			SensorData lastRead = readingsService.getLastReading();
			publisherName = lastRead.getPublisher();
		}
		if(maxResults == null) {
			maxResults = DEFAULT_MAX_RESULTS;
		}
		SensorsCollection readings = readingsService.getReadings(publisherName, maxResults);
        model.addAttribute("readings", readings);
		model.addAttribute("publisherName", publisherName);
		model.addAttribute("maxResults", maxResults);
        return "outliers";
    }
}
