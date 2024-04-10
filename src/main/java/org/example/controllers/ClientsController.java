package org.example.controllers;


import lombok.RequiredArgsConstructor;
import org.example.dtos.*;
import org.example.services.ClientService;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RequiredArgsConstructor

@RestController
public class ClientsController {
	private final ClientService clientService;

	@PostMapping(value = "/clients")
	public ClientDto saveClient(@RequestBody ClientDto clientDto) {
		return clientService.saveClient(clientDto);
	}


	@GetMapping(value = "/clients")
	public List<ClientManagerDto> getAllClients() {
		return clientService.getAllClients();
	}


	@GetMapping(value = "/clients/{clientId}")
	public ClientManagerDto findByClientId(@PathVariable UUID clientId) {
		return clientService.findByClientId(clientId);
	}


	@DeleteMapping(value = "/clients/{clientId}")
	public ClientDto deleteClientById(@PathVariable UUID clientId) {
		return clientService.deleteClientById(clientId);
	}
}
