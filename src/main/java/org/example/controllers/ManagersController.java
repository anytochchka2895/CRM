package org.example.controllers;


import lombok.RequiredArgsConstructor;
import org.example.dtos.*;
import org.example.services.ManagerService;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RequiredArgsConstructor

@RestController
public class ManagersController {
	private final ManagerService managerService;

	@PostMapping(value = "/managers")
	public ManagerDto saveManager(@RequestBody ManagerDto managerDto) {
		return managerService.saveManager(managerDto);
	}


	@GetMapping(value = "/managers")
	public List<ManagerWithSubManagerDto> getAllManagers() {
		return managerService.getAllManagers();
	}


	@GetMapping(value = "/managers/{managerId}")
	public List<ClientDto> getByManagerId(@PathVariable UUID managerId) {
		return managerService.getByManagerId(managerId);
	}


	@DeleteMapping(value = "/managers/{managerId}")
	public ManagerDto deleteManagerById(@PathVariable UUID managerId) {
		return managerService.deleteManagerById(managerId);
	}




}
