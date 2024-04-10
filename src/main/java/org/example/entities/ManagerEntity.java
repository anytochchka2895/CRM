package org.example.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "managers")
public class ManagerEntity {
	@Id
	@Column(name = "id")
	private UUID id;

	@Column(name = "full_name")
	private String fullName;

	@Column(name = "phone")
	private Long phone;

	@Column(name = "sub_manager_id")
	private UUID subManagerId;

	@Column(name = "deleted")
	private boolean deleted;

	@Column(name = "created_at")
	private ZonedDateTime createdAt;

	@Column(name = "updated_at")
	private ZonedDateTime updatedAt;

}
