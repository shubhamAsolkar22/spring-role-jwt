package com.techcross.security.jwt.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/v1/ping")
public class PingController {

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping(path = "/adminping", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getAdminPing() {
		return ResponseEntity.ok("[\"adminHello\"]");
	}

	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	@GetMapping(path = "/userping", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getUserPing() {
		return ResponseEntity.ok("[\"userHello\"]");
	}
}
