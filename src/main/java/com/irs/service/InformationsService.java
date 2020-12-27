package com.irs.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.irs.datamodel.Informations;
import com.irs.repository.InformationsRepository;

@Service
public class InformationsService {

	private static final String AND = "AND";
	
	@Autowired
	private InformationsRepository repo;
	
	public List<Informations> findInformations(String condition, String searchValue){
		Specification<Informations> spec = null;
		List<String> searchList = this.removeStopWords(searchValue.split(" "));
		
		if (condition.contains(AND)) {
			for (int i = 0; i < searchList.size(); i++) {
				if (i == 0) {
					spec = filterInformationsWtihAND(searchList.get(i));
				} else {
					spec = spec.and(filterInformationsWtihAND(searchList.get(i)));
				}
			}
			return repo.findAll(spec);
		}
		
		return repo.findAll(filterInformationsWithOR(searchList));
		
	}
	
	private Specification<Informations> filterInformationsWtihAND(String searchValue){
		return new Specification<Informations>() {

			private static final long serialVersionUID = -3716515706153149746L;

			@Override
			public Predicate toPredicate(Root<Informations> root, CriteriaQuery<?> query,
					CriteriaBuilder criteriaBuilder) {
				List<Predicate> predicates = new ArrayList<>();

				predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + searchValue.toLowerCase() + "%"));
				predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%" + searchValue.toLowerCase() + "%"));
				predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("content")), "%" + searchValue.toLowerCase() + "%"));
				return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
			}
		};
	}
	
	private Specification<Informations> filterInformationsWithOR(List<String> searchList){
		return new Specification<Informations>() {

			private static final long serialVersionUID = -3716515706153149746L;

			@Override
			public Predicate toPredicate(Root<Informations> root, CriteriaQuery<?> query,
					CriteriaBuilder criteriaBuilder) {
				List<Predicate> predicates = new ArrayList<>();

				for (String value : searchList) {
					predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + value.toLowerCase() + "%"));
					predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%" + value.toLowerCase() + "%"));
					predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("content")), "%" + value.toLowerCase() + "%"));
				}
				return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
			}
		};
	}
	
	public Informations findInformation(Long id) {
		Informations info = null;
		Optional<Informations> information = repo.findById(id);
		if (information.isPresent()) {
			return information.get();
		}
		return info;
	}
	
	public DataTablesOutput<Informations> findInformations(DataTablesInput dataTableInput) {
		DataTablesOutput<Informations> output = new DataTablesOutput<Informations>();

		Long totalCnt = repo.count();
		if (totalCnt <= 0) {
			return output;
		}
		String searchValue = dataTableInput.getSearch().getValue() == null ? Strings.EMPTY : dataTableInput.getSearch().getValue();

		Page<Informations> informations = repo.findInformations(getPage(dataTableInput, totalCnt), searchValue);
		output.setData(informations.getContent());
		output.setDraw(dataTableInput.getDraw());
		output.setRecordsFiltered(searchValue == Strings.EMPTY ? totalCnt : informations.getTotalElements());
		output.setRecordsTotal(totalCnt);
		return output;
	}
	
	public Pageable getPage(DataTablesInput dataTablesInput, Long totalCnt) {
		Integer start = dataTablesInput.getStart();
		Integer length = (int) (dataTablesInput.getLength() < 0 ? totalCnt : dataTablesInput.getLength());
		if (start > 0) {
			start = start / length;
		}
		
		String sortColName = dataTablesInput.getColumns().get(dataTablesInput.getOrder().get(0).getColumn()).getName();

		String dir = dataTablesInput.getOrder().get(0).getDir();
		Sort sort = null;
		if (dir.equals("asc")) {
			sort = Sort.by(Sort.Direction.ASC, sortColName);
		} else {
			sort = Sort.by(Sort.Direction.DESC, sortColName);

		}
		return PageRequest.of(start, length, sort);
	}
	
	public Informations saveInformation(Informations information) {
		return repo.save(information);
	}
	
	public void deleteInformation(long id) {
		repo.deleteById(id);
	}
	
	public List<String> getStopWords() {
		
		ClassPathResource cpr = new ClassPathResource("stopwords.txt");
		ArrayList<String> words = new ArrayList<String>();
		
		try {
			Scanner sc = new Scanner(cpr.getInputStream());
			while (sc.hasNext()){
				words.add(sc.next());
			}
			sc.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return words;
	}
	
	private List<String> removeStopWords(String[] searchValue) {
		List<String> searchList = new ArrayList<String>(Arrays.asList(searchValue));
		List<String> stopWordList = this.getStopWords();
		searchList.removeIf(value -> stopWordList.contains(value));
		return searchList;
	}
}
