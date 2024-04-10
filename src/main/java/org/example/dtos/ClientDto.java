package org.example.dtos;

import lombok.*;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientDto {
	private UUID id;
	private String name;
	private String address;
	private UUID managerId;
	private boolean deleted;
	private ZonedDateTime createdAt;
	private ZonedDateTime updatedAt;
}
