package org.example.repositories;

import org.example.entities.ManagerEntity;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.*;

@Repository
public interface ManagerRepository extends JpaRepository<ManagerEntity, UUID> {

	@Query(value = "select m from ManagerEntity m " +
			" where m.deleted = false ")
	List<ManagerEntity> findAllByStatusNotDeleted();

	List<ManagerEntity> findAllBySubManagerId(UUID subManagerId);

	@Modifying
	@Query (value = "update ManagerEntity m " +
			" set m.subManagerId = null, m.updatedAt = :currentTime " +
			" where m.subManagerId = :subManagerId ")
	void updateManagersBySubManagerId(UUID subManagerId, ZonedDateTime currentTime);
}
