package org.example.services;

import org.example.dtos.*;
import org.example.entities.*;
import org.example.exceptions.ManagerException;
import org.example.repositories.*;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ManagerServiceTest {

	@Mock ManagerRepository managerRepository;
	@Mock ClientRepository clientRepository;

	ManagerService managerService;

	EasyRandom generator = new EasyRandom();

	@BeforeEach
	void init() {
		managerService = new ManagerService(managerRepository, clientRepository);
	}


	@Test
	void saveManagerNewManagerTest() {
		//GIVEN
		ManagerDto managerDto = generator.nextObject(ManagerDto.class);
		managerDto.setId(null);
		//MOCK

		//WHEN
		ManagerDto resultManagerDto = managerService.saveManager(managerDto);
		//THEN
		verify(managerRepository, times(1)).save(any());
		assertNotNull(resultManagerDto);
		assertNotNull(resultManagerDto.getId());
		assertNotNull(resultManagerDto.getCreatedAt());
		assertNotNull(resultManagerDto.getUpdatedAt());
		assertEquals(managerDto.getFullName(), resultManagerDto.getFullName());
		assertEquals(managerDto.getPhone(), resultManagerDto.getPhone());
	}


	@Test
	void saveManagerUpdateManagerWithExceptionTest() {
		//GIVEN
		ManagerDto managerDto = generator.nextObject(ManagerDto.class);
		//MOCK
		when(managerRepository.findById(managerDto.getId())).thenReturn(Optional.empty());
		//WHEN
		ManagerException thrown = assertThrows(
				ManagerException.class,
				() -> managerService.saveManager(managerDto));
		//THEN
		assertTrue(thrown.getMessage().contains("Менеджер не найден"));
	}


	@Test
	void saveManagerUpdateManagerTest() {
		//GIVEN
		ManagerDto managerDto = generator.nextObject(ManagerDto.class);
		ManagerEntity managerEntity = generator.nextObject(ManagerEntity.class);
		managerEntity.setId(managerDto.getId());
		ZonedDateTime oldUpdatedAt = managerEntity.getUpdatedAt();
		//MOCK
		when(managerRepository.findById(managerDto.getId())).thenReturn(Optional.of(managerEntity));
		//WHEN
		ManagerDto resultManagerDto = managerService.saveManager(managerDto);
		//THEN
		verify(managerRepository, times(1)).save(any());
		assertNotNull(resultManagerDto);
		assertEquals(managerEntity.getId(), resultManagerDto.getId());
		assertEquals(managerDto.getFullName(), resultManagerDto.getFullName());
		assertEquals(managerDto.getSubManagerId(), resultManagerDto.getSubManagerId());
		assertEquals(managerDto.getPhone(), resultManagerDto.getPhone());
		assertEquals(managerEntity.getCreatedAt(), resultManagerDto.getCreatedAt());
		assertNotEquals(oldUpdatedAt, resultManagerDto.getUpdatedAt());
	}


	@Test
	void getAllManagersTest() {
		//GIVEN
		List<ManagerEntity> allManagersByStatus = generator.objects(ManagerEntity.class, 11).toList();
		//MOCK
		when(managerRepository.findAllByStatusNotDeleted()).thenReturn(allManagersByStatus);
		//WHEN
		List<ManagerWithSubManagerDto> resultManagers = managerService.getAllManagers();
		//THEN
		assertNotNull(resultManagers);
		assertEquals(11, resultManagers.size());
	}


	@Test
	void getAllManagersSingleTest() {
		//GIVEN
		ManagerEntity managerByStatus = generator.nextObject(ManagerEntity.class);
		//MOCK
		when(managerRepository.findAllByStatusNotDeleted()).thenReturn(List.of(managerByStatus));
		//WHEN
		List<ManagerWithSubManagerDto> resultManagers = managerService.getAllManagers();
		//THEN
		ManagerDto resultSinglManager = resultManagers.get(0).getManager();
		assertEquals(managerByStatus.getId(), resultSinglManager.getId());
		assertEquals(managerByStatus.getFullName(), resultSinglManager.getFullName());
		assertEquals(managerByStatus.getPhone(), resultSinglManager.getPhone());
		assertEquals(managerByStatus.getSubManagerId(), resultSinglManager.getSubManagerId());
	}


	@Test
	void getByManagerIdWithExceptionTest() {
		//GIVEN
		UUID managerId = generator.nextObject(UUID.class);
		//MOCK
		when(clientRepository.findAllByManagerId(managerId)).thenReturn(null);
		//WHEN
		ManagerException thrown = assertThrows(
				ManagerException.class,
				() -> managerService.getByManagerId(managerId));
		//THEN
		assertTrue(thrown.getMessage().contains("По данному id ничего не найдено"));

	}


	@Test
	void getByManagerIdTest() {
		//GIVEN
		UUID managerId = generator.nextObject(UUID.class);
		List<ClientEntity> clientEntityList = generator.objects(ClientEntity.class, 11).toList();
		List<ClientEntity> allByManagerId = clientEntityList
				.stream()
				.map(clientEntity -> {
					clientEntity.setManagerId(managerId);
					return clientEntity;
				})
				.toList();
		//MOCK
		when(clientRepository.findAllByManagerId(managerId)).thenReturn(allByManagerId);
		//WHEN
		List<ClientDto> resultByManagerId = managerService.getByManagerId(managerId);
		//THEN
		assertNotNull(resultByManagerId);
		assertEquals(11, resultByManagerId.size());
	}


	@Test
	void getByManagerIdSingleTest() {
		//GIVEN
		UUID managerId = generator.nextObject(UUID.class);
		ClientEntity byManagerId = generator.nextObject(ClientEntity.class);
		byManagerId.setManagerId(managerId);
		//MOCK
		when(clientRepository.findAllByManagerId(managerId)).thenReturn(List.of(byManagerId));
		//WHEN
		List<ClientDto> resultByManagerId = managerService.getByManagerId(managerId);
		//THEN
		assertNotNull(resultByManagerId);
		ClientDto result = resultByManagerId.get(0);
		assertEquals(byManagerId.getId(), result.getId());
		assertEquals(byManagerId.getName(), result.getName());
		assertEquals(byManagerId.getAddress(), result.getAddress());
		assertEquals(byManagerId.getManagerId(), result.getManagerId());
	}


	@Test
	void deleteManagerByManagerIdNotFoundTest() {
		//GIVEN
		UUID managerId = generator.nextObject(UUID.class);
		//MOCK
		when(managerRepository.findById(managerId)).thenReturn(Optional.empty());
		//WHEN
		ManagerException thrown = assertThrows(
				ManagerException.class,
				() -> managerService.deleteManagerById(managerId));
		//THEN
		assertTrue(thrown.getMessage().contains("Менеджер не найден"));
	}


	@Test
	void deleteManagerByManagerIdWithoutSubManagerTest() {
		//GIVEN
		UUID managerId = generator.nextObject(UUID.class);
		ManagerEntity managerEntity = generator.nextObject(ManagerEntity.class);
		managerEntity.setId(managerId);
		managerEntity.setSubManagerId(null);
		//MOCK
		when(managerRepository.findById(managerId)).thenReturn(Optional.of(managerEntity));
		//WHEN
		ManagerException thrown = assertThrows(
				ManagerException.class,
				() -> managerService.deleteManagerById(managerId));
		//THEN
		assertTrue(thrown.getMessage().contains("Удаление менеджера запрещено, так как не назначен его заместитель"));
	}


	@Test
	void deleteManagerByManagerIdTest() {
		//GIVEN
		UUID managerId = generator.nextObject(UUID.class);
		ManagerEntity managerEntity = generator.nextObject(ManagerEntity.class);
		managerEntity.setId(managerId);
		//MOCK
		when(managerRepository.findById(managerId)).thenReturn(Optional.of(managerEntity));
		//WHEN
		ManagerDto resultManagerDto = managerService.deleteManagerById(managerId);
		//THEN
		verify(managerRepository, times(1)).save(any());
		verify(clientRepository, times(1)).updateClientsByManagerId(any(), any(), any());
		verify(managerRepository, times(1)).updateManagersBySubManagerId(any(), any());
		assertTrue(resultManagerDto.isDeleted());
		assertEquals(managerEntity.getId(), resultManagerDto.getId());
		assertEquals(managerEntity.getFullName(), resultManagerDto.getFullName());
		assertEquals(managerEntity.getFullName(), resultManagerDto.getFullName());
		assertEquals(managerEntity.getPhone(), resultManagerDto.getPhone());
		assertEquals(managerEntity.getSubManagerId(), resultManagerDto.getSubManagerId());
		}


	//GIVEN

	//MOCK

	//WHEN

	//THEN


}