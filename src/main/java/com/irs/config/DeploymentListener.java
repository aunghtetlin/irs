package com.irs.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.irs.datamodel.Informations;
import com.irs.repository.InformationsRepository;
import com.irs.util.CommonUtil;

@Component
public class DeploymentListener {

	@Autowired
	private InformationsRepository repo;
	
	@PostConstruct
	private void init() {
		
		if (repo.findAll().isEmpty()) {
			
			ClassPathResource cpr = new ClassPathResource("data.json");
			try {
				byte[] byteData = FileCopyUtils.copyToByteArray(cpr.getInputStream());
				String data = new String(byteData, StandardCharsets.UTF_8);
				
				List<Informations> informations = CommonUtil.convertToObject(data, new TypeReference<List<Informations>>() {});
				repo.saveAll(informations);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
}
