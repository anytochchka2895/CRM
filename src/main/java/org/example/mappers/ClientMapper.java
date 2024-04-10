package org.example.mappers;

import org.example.dtos.ClientDto;
import org.example.entities.ClientEntity;

public class ClientMapper {
	public static ClientEntity clientDtoToEntity(ClientDto clientDto) {
		return new ClientEntity(clientDto.getId(),
		                        clientDto.getName(),
		                        clientDto.getAddress(),
		                        clientDto.getManagerId(),
		                        clientDto.isDeleted(),
		                        clientDto.getCreatedAt(),
		                        clientDto.getUpdatedAt());
	}


	public static ClientDto clientEntityToDto(ClientEntity clientEntity) {
		return new ClientDto(clientEntity.getId(),
		                        clientEntity.getName(),
		                        clientEntity.getAddress(),
		                        clientEntity.getManagerId(),
		                        clientEntity.isDeleted(),
		                        clientEntity.getCreatedAt(),
		                        clientEntity.getUpdatedAt());
	}
}
