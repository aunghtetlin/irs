package com.irs.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.irs.datamodel.Informations;
import com.irs.service.InformationsService;

@Controller
@RequestMapping(value = "/")
public class DashboardController {

	@Autowired
	private InformationsService service;
	
	@GetMapping
	public String dashboardPage() {
		return "pages/dashboard";
	}
	
	@PostMapping(value = "/search")
	public @ResponseBody List<Informations> searchInformations(@RequestParam("searchCondition") String searchCondition, @RequestParam("searchValue") String searchValue) {
		return service.findInformations(searchCondition, searchValue);
	}
	
	@PostMapping("/find")
	public @ResponseBody Informations findInformation(@RequestParam(value = "id") long id) {
		return service.findInformation(id);
	}
	
	@PostMapping(value = "/stopwords")
	public @ResponseBody List<String> getStopWords() {
		return service.getStopWords();
	}
}
