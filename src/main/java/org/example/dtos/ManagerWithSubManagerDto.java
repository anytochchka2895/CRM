package org.example.dtos;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ManagerWithSubManagerDto {
	private ManagerDto manager;
	private ManagerDto subManager;
}
