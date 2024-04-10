package org.example.services;

import lombok.RequiredArgsConstructor;
import org.example.dtos.*;
import org.example.entities.*;
import org.example.exceptions.ManagerException;
import org.example.mappers.*;
import org.example.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ManagerService {
	private final ManagerRepository managerRepository;
	private final ClientRepository clientRepository;


	@Transactional
	public ManagerDto saveManager(ManagerDto managerDto) {

		if (Objects.isNull(managerDto.getId())) {
			return newManager(managerDto);
		}
		else {
			return updateManager(managerDto);
		}
	}

	private ManagerDto newManager(ManagerDto managerDto) {
		ManagerEntity newManagerEntity = new ManagerEntity();
		newManagerEntity.setId(UUID.randomUUID());
		newManagerEntity.setFullName(managerDto.getFullName());
		newManagerEntity.setPhone(managerDto.getPhone());
		newManagerEntity.setSubManagerId(managerDto.getSubManagerId());
		newManagerEntity.setDeleted(false);
		newManagerEntity.setCreatedAt(ZonedDateTime.now());
		newManagerEntity.setUpdatedAt(ZonedDateTime.now());
		managerRepository.save(newManagerEntity);
		return ManagerMapper.managerEntityToDto(newManagerEntity);
	}

	private ManagerDto updateManager(ManagerDto managerDto) {
		Optional<ManagerEntity> managerEntityOptionalById = managerRepository.findById(managerDto.getId());
		ManagerEntity updateManagerEntityById = managerEntityOptionalById
				.orElseThrow(() -> new ManagerException("Менеджер не найден"));
		updateManagerEntityById.setFullName(managerDto.getFullName());
		updateManagerEntityById.setPhone(managerDto.getPhone());
		updateManagerEntityById.setSubManagerId(managerDto.getSubManagerId());
		updateManagerEntityById.setUpdatedAt(ZonedDateTime.now());
		managerRepository.save(updateManagerEntityById);
		return ManagerMapper.managerEntityToDto(updateManagerEntityById);
	}


	public List<ManagerWithSubManagerDto> getAllManagers() {
		List<ManagerEntity> allManagersByStatus = managerRepository.findAllByStatusNotDeleted();

		Map<UUID, ManagerEntity> allManagersMap = allManagersByStatus
				.stream()
				.collect(Collectors.toMap(ManagerEntity::getId, ManagerEntity -> ManagerEntity));

		return allManagersByStatus
				.stream()
				.map(allManagerMap -> toManagerWithSubManagerDto(allManagerMap, allManagersMap))
				.toList();
	}


	private ManagerWithSubManagerDto toManagerWithSubManagerDto(ManagerEntity managerEntity,
	                                                            Map<UUID, ManagerEntity> allManagersMap) {
		ManagerWithSubManagerDto managerWithSubManagerDto = new ManagerWithSubManagerDto();

		managerWithSubManagerDto.setManager(ManagerMapper.managerEntityToDto(managerEntity));
		ManagerEntity subManagerFromMapEntity = null;
		if (Objects.nonNull(managerEntity.getSubManagerId())) {
			subManagerFromMapEntity = allManagersMap.get(managerEntity.getSubManagerId());
		}
		managerWithSubManagerDto.setSubManager(ManagerMapper.managerEntityToDto(subManagerFromMapEntity));
		return managerWithSubManagerDto;
	}


	public List<ClientDto> getByManagerId(UUID managerId) {
		List<ClientEntity> allByManagerId = clientRepository.findAllByManagerId(managerId);
		if (Objects.isNull(allByManagerId) || allByManagerId.size() == 0) {
			throw new ManagerException("По данному id ничего не найдено");
		}
		return allByManagerId.stream()
		                     .map(ClientMapper::clientEntityToDto)
		                     .toList();
	}


	@Transactional
	public ManagerDto deleteManagerById(UUID managerId) {
		ManagerEntity managerEntity = managerRepository
				.findById(managerId)
				.orElseThrow(() -> new ManagerException("Менеджер не найден"));
		if (Objects.isNull(managerEntity.getSubManagerId())) {
			throw new ManagerException("Удаление менеджера запрещено, так как не назначен его заместитель");
		}
		clientRepository.updateClientsByManagerId(managerId, managerEntity.getSubManagerId(), ZonedDateTime.now());
		managerRepository.updateManagersBySubManagerId(managerId, ZonedDateTime.now());
		managerEntity.setDeleted(true);
		managerRepository.save(managerEntity);
		return ManagerMapper.managerEntityToDto(managerEntity);
	}


}
