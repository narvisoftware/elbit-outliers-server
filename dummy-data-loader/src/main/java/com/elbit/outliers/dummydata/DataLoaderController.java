package com.elbit.outliers.dummydata;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 *
 * @author Mihai
 */
@Controller
public class DataLoaderController {

	@Autowired
	private DummyDataService dummyDataService;
	
    @GetMapping("/loaddata")
    public String main(Model model) {
		DummyLoadResult result = dummyDataService.loadData();
        model.addAttribute("messages", result);
        return "loaddata";
    }
}
