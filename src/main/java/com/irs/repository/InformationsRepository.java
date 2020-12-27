package com.irs.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.irs.datamodel.Informations;

@Repository
public interface InformationsRepository extends CrudRepository<Informations, Long>, JpaRepository<Informations, Long>, JpaSpecificationExecutor<Informations>, DataTablesRepository<Informations, Long> {

	@Query("SELECT COUNT(i.id) FROM Informations i")
	long count();
	
	@Query("SELECT new com.irs.datamodel.Informations(i.id, i.title, i.description, i.content) FROM Informations i WHERE "
			+ "("
			+ "(lower(i.title) LIKE lower(concat('%', :searchValue  ,'%'))) OR "
			+ "(lower(i.description) LIKE lower(concat('%', :searchValue  ,'%'))) OR "
			+ "(lower(i.content) LIKE lower(concat('%', :searchValue,'%')))"
			+ ")")
	Page<Informations> findInformations(Pageable page, @Param("searchValue") String searchValue);
}
