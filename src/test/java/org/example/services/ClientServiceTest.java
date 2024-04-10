package org.example.services;

import org.example.dtos.*;
import org.example.entities.*;
import org.example.exceptions.*;
import org.example.repositories.*;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

	@Mock ClientRepository clientRepository;
	@Mock ManagerRepository managerRepository;

	ClientService clientService;

	EasyRandom generator = new EasyRandom();

	@BeforeEach
	void init() {
		clientService = new ClientService(clientRepository, managerRepository);
	}


	@Test
	void saveClientNewClientTest() {
		//GIVEN
		ClientDto clientDto = generator.nextObject(ClientDto.class);
		clientDto.setId(null);
		clientDto.setCreatedAt(null);
		clientDto.setUpdatedAt(null);
		//MOCK
		when(clientRepository.findByName(clientDto.getName())).thenReturn(null);
		//WHEN
		ClientDto resultClientDto = clientService.saveClient(clientDto);
		//THEN
		verify(clientRepository, times(1)).save(any());
		assertNotNull(resultClientDto);
		assertNotNull(resultClientDto.getId());
		assertNotNull(resultClientDto.getCreatedAt());
		assertNotNull(resultClientDto.getUpdatedAt());
		assertEquals(clientDto.getName(), resultClientDto.getName());
		assertEquals(clientDto.getAddress(), resultClientDto.getAddress());
		assertEquals(clientDto.getManagerId(), resultClientDto.getManagerId());
	}


	@Test
	void saveClientNewClientWithExceptionTest() {
		//GIVEN
		ClientDto clientDto = generator.nextObject(ClientDto.class);
		clientDto.setId(null);
		clientDto.setCreatedAt(null);
		clientDto.setUpdatedAt(null);
		ClientEntity clientByName = generator.nextObject(ClientEntity.class);
		clientByName.setName(clientDto.getName());
		//MOCK
		when(clientRepository.findByName(clientDto.getName())).thenReturn(clientByName);
		//WHEN
		ClientException thrown = assertThrows(
				ClientException.class,
				() -> clientService.saveClient(clientDto));
		//THEN
		assertTrue(thrown.getMessage().contains("Клиент с таким именем уже существует"));
	}


	@Test
	void saveClientUpdateClientTest() {
		//GIVEN
		ClientDto clientDto = generator.nextObject(ClientDto.class);
		ClientEntity clientEntityById = generator.nextObject(ClientEntity.class);
		clientEntityById.setId(clientDto.getId());
		//MOCK
		when(clientRepository.findById(clientDto.getId())).thenReturn(Optional.of(clientEntityById));
		//WHEN
		ClientDto resultClientDto = clientService.saveClient(clientDto);
		//THEN
		verify(clientRepository, times(1)).save(any());
		assertNotNull(resultClientDto);
		assertEquals(clientDto.getId(), resultClientDto.getId());
		assertEquals(clientDto.getName(), resultClientDto.getName());
		assertEquals(clientDto.getAddress(), resultClientDto.getAddress());
		assertEquals(clientDto.getManagerId(), resultClientDto.getManagerId());
	}


	@Test
	void saveClientUpdateClientWithExceptionINotFoundTest() {
		//GIVEN
		ClientDto clientDto = generator.nextObject(ClientDto.class);
		ClientEntity clientEntityById = generator.nextObject(ClientEntity.class);
		//MOCK
		when(clientRepository.findById(clientDto.getId())).thenReturn(Optional.empty());
		//WHEN
		ClientException thrown = assertThrows(
				ClientException.class,
				() -> clientService.saveClient(clientDto));
		//THEN
		assertTrue(thrown.getMessage().contains("Клиента с таким id не существует"));
	}


	@Test
	void saveClientUpdateClientWithExceptionDoubleNameTest() {
		//GIVEN
		ClientDto clientDto = generator.nextObject(ClientDto.class);
		ClientEntity clientEntityById = generator.nextObject(ClientEntity.class);
		clientEntityById.setId(clientDto.getId());
		ClientEntity clientEntityByName = generator.nextObject(ClientEntity.class);
		clientEntityByName.setName(clientDto.getName());
		//MOCK
		when(clientRepository.findById(clientDto.getId())).thenReturn(Optional.of(clientEntityById));
		when(clientRepository.findByName(clientDto.getName())).thenReturn(clientEntityByName);
		//WHEN
		ClientException thrown = assertThrows(
				ClientException.class,
				() -> clientService.saveClient(clientDto));
		//THEN
		assertTrue(thrown.getMessage().contains("Клиент с таким именем уже существует"));
	}


	@Test
	void getAllClientsSingleTest() {
		//GIVEN
		ClientEntity client = generator.nextObject(ClientEntity.class);
		ManagerEntity managerById = generator.nextObject(ManagerEntity.class);
		ManagerEntity subManagerById = generator.nextObject(ManagerEntity.class);
		managerById.setId(client.getManagerId());
		subManagerById.setId(managerById.getSubManagerId());
		//MOCK
		when(clientRepository.findAllByStatusNotDeleted()).thenReturn(List.of(client));
		when(managerRepository.findAllById(any()))
				.thenReturn(List.of(managerById), List.of(subManagerById));
		//WHEN
		List<ClientManagerDto> resultClientList = clientService.getAllClients();
		//THEN
		assertNotNull(resultClientList);
		ClientManagerDto resultClient = resultClientList.get(0);
		assertEquals(client.getId(), resultClient.getId());
		assertEquals(client.getName(), resultClient.getName());
		assertEquals(client.getAddress(), resultClient.getAddress());
		assertEquals(client.getManagerId(), resultClient.getManager().getId());
		ManagerDto resultManager = resultClient.getManager();
		assertEquals(managerById.getId(), resultManager.getId());
		assertEquals(managerById.getFullName(), resultManager.getFullName());
		assertEquals(managerById.getPhone(), resultManager.getPhone());
		assertEquals(managerById.getSubManagerId(), resultManager.getSubManagerId());
		ManagerDto resultSubManager = resultClient.getSubManager();
		assertEquals(subManagerById.getId(), resultSubManager.getId());
		assertEquals(subManagerById.getFullName(), resultSubManager.getFullName());
		assertEquals(subManagerById.getPhone(), resultSubManager.getPhone());
		assertEquals(subManagerById.getSubManagerId(), resultSubManager.getSubManagerId());
	}

	@Test
	void findByClientIdTest() {
		//GIVEN
		UUID clientId = generator.nextObject(UUID.class);
		ClientEntity clientEntityById = generator.nextObject(ClientEntity.class);
		clientEntityById.setId(clientId);
		ManagerEntity managerEntityForClient = generator.nextObject(ManagerEntity.class);
		managerEntityForClient.setId(clientEntityById.getManagerId());
		ManagerEntity subManagerEntityForClient =  generator.nextObject(ManagerEntity.class);
		subManagerEntityForClient.setId(managerEntityForClient.getSubManagerId());
		//MOCK
		when(clientRepository.findById(clientId)).thenReturn(Optional.of(clientEntityById));
		when(managerRepository.findById(clientEntityById.getManagerId())).thenReturn(Optional.of(managerEntityForClient));
		when(managerRepository.findById(managerEntityForClient.getSubManagerId())).thenReturn(Optional.of(subManagerEntityForClient));
		//WHEN
		ClientManagerDto resultByClientId = clientService.findByClientId(clientId);
		//THEN
		assertNotNull(resultByClientId);
		assertEquals(clientEntityById.getId(), resultByClientId.getId());
		assertEquals(clientEntityById.getName(), resultByClientId.getName());
		assertEquals(clientEntityById.getAddress(), resultByClientId.getAddress());
		ManagerDto resultManager = resultByClientId.getManager();
		assertEquals(managerEntityForClient.getId(), resultManager.getId());
		assertEquals(managerEntityForClient.getFullName(), resultManager.getFullName());
		assertEquals(managerEntityForClient.getPhone(), resultManager.getPhone());
		assertEquals(managerEntityForClient.getSubManagerId(), resultManager.getSubManagerId());
		ManagerDto resultSubManager = resultByClientId.getSubManager();
		assertEquals(subManagerEntityForClient.getId(), resultSubManager.getId());
		assertEquals(subManagerEntityForClient.getFullName(), resultSubManager.getFullName());
		assertEquals(subManagerEntityForClient.getPhone(), resultSubManager.getPhone());
		assertEquals(subManagerEntityForClient.getSubManagerId(), resultSubManager.getSubManagerId());
	}

	@Test
	void findByClientIdWithExceptionClientNotFoundTest() {
		//GIVEN
		UUID clientId = generator.nextObject(UUID.class);
		//MOCK
		when(clientRepository.findById(clientId)).thenReturn(Optional.empty());
		//WHEN
		ClientException thrown = assertThrows(
				ClientException.class,
				() -> clientService.findByClientId(clientId));
		//THEN
		assertTrue(thrown.getMessage().contains("Клиент не найден"));
	}

	@Test
	void findByClientIdWithExceptionManagerNotFoundTest() {
		//GIVEN
		UUID clientId = generator.nextObject(UUID.class);
		ClientEntity clientEntityById = generator.nextObject(ClientEntity.class);
		clientEntityById.setId(clientId);
		//MOCK
		when(clientRepository.findById(clientId)).thenReturn(Optional.of(clientEntityById));
		when(managerRepository.findById(clientEntityById.getManagerId())).thenReturn(Optional.empty());
		//WHEN
		ManagerException thrown = assertThrows(
				ManagerException.class,
				() -> clientService.findByClientId(clientId));
		//THEN
		assertTrue(thrown.getMessage().contains("Менеджер не найден"));
	}

	@Test
	void findByClientIdWithExceptionSubManagerNotFoundTest() {
		//GIVEN
		UUID clientId = generator.nextObject(UUID.class);
		ClientEntity clientEntityById = generator.nextObject(ClientEntity.class);
		clientEntityById.setId(clientId);
		ManagerEntity managerEntityForClient = generator.nextObject(ManagerEntity.class);
		managerEntityForClient.setId(clientEntityById.getManagerId());
		//MOCK
		when(clientRepository.findById(clientId)).thenReturn(Optional.of(clientEntityById));
		when(managerRepository.findById(clientEntityById.getManagerId())).thenReturn(Optional.of(managerEntityForClient));
		when(managerRepository.findById(managerEntityForClient.getSubManagerId())).thenReturn(Optional.empty());
		//WHEN
		ManagerException thrown = assertThrows(
				ManagerException.class,
				() -> clientService.findByClientId(clientId));
		//THEN
		assertTrue(thrown.getMessage().contains("Заместитель менеджера не найден"));
	}

	@Test
	void deleteClientById() {
		//GIVEN
		UUID clientId = generator.nextObject(UUID.class);
		ClientEntity clientEntityById = generator.nextObject(ClientEntity.class);
		clientEntityById.setId(clientId);
		//MOCK
		when(clientRepository.findById(clientId)).thenReturn(Optional.of(clientEntityById));
		//WHEN
		ClientDto resultClientDto = clientService.deleteClientById(clientId);
		//THEN
		verify(clientRepository, times(1)).save(any());
		assertNotNull(resultClientDto);
		assertTrue(resultClientDto.isDeleted());
		assertEquals(clientEntityById.getId(), resultClientDto.getId());
		assertEquals(clientEntityById.getName(), resultClientDto.getName());
		assertEquals(clientEntityById.getAddress(), resultClientDto.getAddress());
		assertEquals(clientEntityById.getManagerId(), resultClientDto.getManagerId());
	}


	@Test
	void deleteClientWithExceptionById() {
		//GIVEN
		UUID clientId = generator.nextObject(UUID.class);
		//MOCK
		when(clientRepository.findById(clientId)).thenReturn(Optional.empty());
		//WHEN
		ClientException thrown = assertThrows(
				ClientException.class,
				() -> clientService.deleteClientById(clientId));
		//THEN
		assertTrue(thrown.getMessage().contains("Клиент не найден"));
	}


	//GIVEN

	//MOCK

	//WHEN

	//THEN

}