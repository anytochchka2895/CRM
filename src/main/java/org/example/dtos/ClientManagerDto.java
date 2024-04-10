package org.example.dtos;

import lombok.*;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientManagerDto {
	private UUID id;
	private String name;
	private String address;
	private ManagerDto manager;
	private ManagerDto subManager;
	private ZonedDateTime createdAt;
	private ZonedDateTime updatedAt;
}
