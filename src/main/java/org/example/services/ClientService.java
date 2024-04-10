package org.example.services;

import lombok.RequiredArgsConstructor;
import org.example.dtos.*;
import org.example.entities.*;
import org.example.exceptions.*;
import org.example.mappers.*;
import org.example.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.*;

@RequiredArgsConstructor
@Service
public class ClientService {
	private final ClientRepository clientRepository;
	private final ManagerRepository managerRepository;


	@Transactional
	public ClientDto saveClient(ClientDto clientDto) {
		if (Objects.isNull(clientDto.getId())) {
			return newClient(clientDto);
		}
		else {
			return updateClient(clientDto);
		}
	}


	private ClientDto newClient(ClientDto clientDto) {
		ClientEntity clientByName = clientRepository.findByName(clientDto.getName());
		if (Objects.nonNull(clientByName)) {
			throw new ClientException("Клиент с таким именем уже существует");
		}

		ClientEntity newClient = ClientEntity.builder()
		                                     .id(UUID.randomUUID())
		                                     .name(clientDto.getName())
		                                     .address(clientDto.getAddress())
		                                     .managerId(clientDto.getManagerId())
		                                     .deleted(false)
		                                     .createdAt(ZonedDateTime.now())
		                                     .updatedAt(ZonedDateTime.now())
		                                     .build();

//		ClientEntity newClient = new ClientEntity(UUID.randomUUID(),
//		                                          clientDto.getName(),
//		                                          clientDto.getAddress(),
//		                                          clientDto.getManagerId(),
//		                                          false,
//		                                          ZonedDateTime.now(),
//		                                          ZonedDateTime.now());
		clientRepository.save(newClient);
		return ClientMapper.clientEntityToDto(newClient);
	}


	private ClientDto updateClient(ClientDto clientDto) {
		Optional<ClientEntity> clientEntityOptionalById = clientRepository.findById(clientDto.getId());
		if (clientEntityOptionalById.isEmpty()) {
			throw new ClientException("Клиента с таким id не существует");
		}
		ClientEntity clientEntityById = clientEntityOptionalById.get();
		checkThisNameInBD(clientEntityById, clientDto);

		clientEntityById.setName(clientDto.getName());
		clientEntityById.setAddress(clientDto.getAddress());
		clientEntityById.setManagerId(clientDto.getManagerId());
		clientEntityById.setUpdatedAt(ZonedDateTime.now());

		clientRepository.save(clientEntityById);
		return ClientMapper.clientEntityToDto(clientEntityById);

	}


	private void checkThisNameInBD(ClientEntity clientEntityById, ClientDto clientDto) {
		if (!clientEntityById.getName().equals(clientDto.getName())) {
			ClientEntity findClientByName = clientRepository.findByName(clientDto.getName());
			if (Objects.nonNull(findClientByName)) {
				throw new ClientException("Клиент с таким именем уже существует");
			}
		}
	}


	public List<ClientManagerDto> getAllClients() {
		List<ClientEntity> allClients = clientRepository.findAllByStatusNotDeleted();
		List<UUID> managersIdsFromAllClients = allClients
				.stream()
				.map(ClientEntity::getManagerId)
				.toList();

		List<ManagerEntity> allManagersByIds = managerRepository.findAllById(managersIdsFromAllClients);

		List<UUID> subManagersIdsFromAllClients = allManagersByIds
				.stream()
				.map(ManagerEntity::getSubManagerId)
				.toList();

		List<ManagerEntity> allSubManagersByIds = managerRepository.findAllById(subManagersIdsFromAllClients);

		Map<UUID, ManagerEntity> managersMap = new HashMap<>();
		for (ManagerEntity managerEntity : allManagersByIds) {
			managersMap.put(managerEntity.getId(), managerEntity);
		}
		for (ManagerEntity managerEntity : allSubManagersByIds) {
			managersMap.put(managerEntity.getId(), managerEntity);
		}

		return allClients.stream()
		                 .map(allClient -> toClientManagerDto(allClient, managersMap))
		                 .toList();
	}


	private ClientManagerDto toClientManagerDto(ClientEntity clientEntity,
	                                            Map<UUID, ManagerEntity> managersMap) {
		ClientManagerDto clientManagerDto = new ClientManagerDto();
		clientManagerDto.setId(clientEntity.getId());
		clientManagerDto.setName(clientEntity.getName());
		clientManagerDto.setAddress(clientEntity.getAddress());

		ManagerEntity managerEntity = managersMap.get(clientEntity.getManagerId());
		clientManagerDto.setManager(ManagerMapper.managerEntityToDto(managerEntity));

		UUID subManagerId = managerEntity.getSubManagerId();
		ManagerEntity subManagerFromMapEntity = null;
		if (Objects.nonNull(managerEntity.getSubManagerId())) {
			subManagerFromMapEntity = managersMap.get(subManagerId);

		}
		clientManagerDto.setSubManager(ManagerMapper.managerEntityToDto(subManagerFromMapEntity));

		clientManagerDto.setCreatedAt(clientEntity.getCreatedAt());
		clientManagerDto.setUpdatedAt(clientEntity.getUpdatedAt());
		return clientManagerDto;
	}


	public ClientManagerDto findByClientId(UUID clientId) {
		ClientEntity clientEntityById = clientRepository
				.findById(clientId)
				.orElseThrow(() -> new ClientException("Клиент не найден"));

		ManagerEntity managerEntityForClient = managerRepository
				.findById(clientEntityById.getManagerId())
				.orElseThrow(() -> new ManagerException("Менеджер не найден"));

		ManagerEntity subManagerEntityForClient = null;
		if (Objects.nonNull(managerEntityForClient.getSubManagerId())) {
			subManagerEntityForClient = managerRepository
					.findById(managerEntityForClient.getSubManagerId())
					.orElseThrow(() -> new ManagerException("Заместитель менеджера не найден"));
		}
		return newClientManagerDto(clientEntityById, managerEntityForClient, subManagerEntityForClient);
	}


	private ClientManagerDto newClientManagerDto(ClientEntity clientEntityById,
	                                             ManagerEntity managerEntityForClient,
	                                             ManagerEntity subManagerEntityForClient) {
		ClientManagerDto clientManagerDto = new ClientManagerDto();
		clientManagerDto.setId(clientEntityById.getId());
		clientManagerDto.setName(clientEntityById.getName());
		clientManagerDto.setAddress(clientEntityById.getAddress());
		clientManagerDto.setManager(ManagerMapper.managerEntityToDto(managerEntityForClient));
		clientManagerDto.setSubManager(ManagerMapper.managerEntityToDto(subManagerEntityForClient));
		clientManagerDto.setCreatedAt(clientEntityById.getCreatedAt());
		clientManagerDto.setUpdatedAt(ZonedDateTime.now());
		return clientManagerDto;
	}


	@Transactional
	public ClientDto deleteClientById(UUID clientId) {
		ClientEntity clientEntityById = clientRepository
				.findById(clientId)
				.orElseThrow(() -> new ClientException("Клиент не найден"));
		clientEntityById.setDeleted(true);
		clientRepository.save(clientEntityById);
		return ClientMapper.clientEntityToDto(clientEntityById);
	}

}
