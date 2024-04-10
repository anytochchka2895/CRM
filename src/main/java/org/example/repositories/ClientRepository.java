package org.example.repositories;

import org.example.entities.ClientEntity;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.*;

@Repository
public interface ClientRepository extends JpaRepository<ClientEntity, UUID> {

	ClientEntity findByName(String name);

	@Query(value = "select c from ClientEntity c " +
			" where c.deleted = false ")
	List<ClientEntity> findAllByStatusNotDeleted();

	List<ClientEntity> findAllByManagerId(UUID managerId);

	@Modifying
	@Query (value = "update ClientEntity c " +
			"set c.managerId = :subManagerId, c.updatedAt = :currentTime " +
			" where c.managerId = :managerId ")
	void updateClientsByManagerId(UUID managerId, UUID subManagerId, ZonedDateTime currentTime);
}
