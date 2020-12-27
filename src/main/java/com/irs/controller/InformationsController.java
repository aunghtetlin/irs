package com.irs.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.irs.datamodel.Informations;
import com.irs.service.InformationsService;

@Controller
@RequestMapping(value = "/informations")
public class InformationsController {

	@Autowired
	private InformationsService service;
	
	@GetMapping
	public String informationsPage(Model model, HttpSession session) {
		if (session.getAttribute("message") != null) {
			model.addAttribute("message", session.getAttribute("message"));
			session.removeAttribute("message");
		}
		return "pages/informations";
	}
	
	@PostMapping("/datatable")
	public @ResponseBody DataTablesOutput<Informations> informations(@RequestBody DataTablesInput dataTablesInput) {
		return service.findInformations(dataTablesInput);
	}
	
	@GetMapping(value = "/add")
	public String addInformationsPage(Model model) {
		model.addAttribute("information", new Informations());
		return "pages/add-information";
	}
	
	@PostMapping(value = "/add")
	public String addInformations(Informations information, HttpSession session) {
		service.saveInformation(information);
		session.setAttribute("message", "Successfully saved.");
		return "redirect:/informations";
	}
	
	@GetMapping(value = "/detail")
	public String editInformations(Model model, @RequestParam(value = "id") long id) {
		model.addAttribute("information", service.findInformation(id));
		return "pages/detail-information";
	}
	
	@GetMapping(value = "/edit")
	public String editInformationsPage(Model model, @RequestParam(value = "id") long id) {
		model.addAttribute("information", service.findInformation(id));
		return "pages/edit-information";
	}
	
	@PostMapping(value = "/edit")
	public String editInformations(Informations information, HttpSession session) {
		service.saveInformation(information);
		session.setAttribute("message", "Successfully updated.");
		return "redirect:/informations";
	}
	
	@PostMapping(value = "/delete")
	public @ResponseBody String deleteInformations(@RequestParam(value = "id") long id) {
		service.deleteInformation(id);
		return "Successfully deleted.";
	}
}
