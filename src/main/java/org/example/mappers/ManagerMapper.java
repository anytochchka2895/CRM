package org.example.mappers;

import org.example.dtos.ManagerDto;
import org.example.entities.ManagerEntity;

import java.util.Objects;

public class ManagerMapper {
	public static ManagerEntity managerDtoToEntity(ManagerDto managerDto) {
		return new ManagerEntity(managerDto.getId(),
		                         managerDto.getFullName(),
		                         managerDto.getPhone(),
		                         managerDto.getSubManagerId(),
		                         managerDto.isDeleted(),
		                         managerDto.getCreatedAt(),
		                         managerDto.getUpdatedAt());
	}


	public static ManagerDto managerEntityToDto(ManagerEntity managerEntity) {
		if (Objects.isNull(managerEntity)) {
			return null;
		}
		return new ManagerDto(managerEntity.getId(),
		                      managerEntity.getFullName(),
		                      managerEntity.getPhone(),
		                      managerEntity.getSubManagerId(),
		                      managerEntity.isDeleted(),
		                      managerEntity.getCreatedAt(),
		                      managerEntity.getUpdatedAt());
	}
}
