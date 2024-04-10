package org.example.dtos;

import lombok.*;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ManagerDto {
	private UUID id;
	private String fullName;
	private Long phone;
	private UUID subManagerId;
	private boolean deleted;
	private ZonedDateTime createdAt;
	private ZonedDateTime updatedAt;
}
