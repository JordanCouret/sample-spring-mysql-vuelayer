package fr.jco.api.controller;

import java.util.List;

import fr.jco.api.model.Town;
import fr.jco.api.repository.TownRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/town")
public class TownController {

	private final TownRepository repository;

	@CrossOrigin(origins = "*")
	@GetMapping
	public List<Town> findAll() {
		return this.repository.findAll();
	}

}
