package org.example.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "clients")
public class ClientEntity {
	@Id
	@Column(name = "id")
	private UUID id;

	@Column(name = "name")
	private String name;

	@Column(name = "address")
	private String address;

	@Column(name = "manager_id")
	private UUID managerId;

	@Column(name = "deleted")
	private boolean deleted;

	@Column(name = "created_at")
	private ZonedDateTime createdAt;

	@Column(name = "updated_at")
	private ZonedDateTime updatedAt;

}
