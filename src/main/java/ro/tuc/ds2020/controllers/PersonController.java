package ro.tuc.ds2020.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.tuc.ds2020.dtos.PersonDTO;
import ro.tuc.ds2020.dtos.PersonDetailsDTO;
import ro.tuc.ds2020.services.PersonService;
import ro.tuc.ds2020.config.JwtUtil;

import io.jsonwebtoken.Claims;

import javax.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@CrossOrigin(origins = {"http://localhost:8081", "http://localhost:3000"}, allowCredentials = "true")
@RequestMapping(value = "/person")
public class PersonController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonController.class);

    private final PersonService personService;

    @Autowired
    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    // Helper method to check if the logged-in user is an admin based on JWT
    private boolean isAdmin(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return false;
        }

        String token = authHeader.substring(7); // Remove "Bearer " prefix
        try {
            Claims claims = JwtUtil.validateToken(token);
            String role = claims.get("role", String.class);
            return "admin".equalsIgnoreCase(role);
        } catch (Exception e) {
            return false; // Invalid token
        }
    }

    // Helper method to validate JWT and return user ID from token
    private UUID getUserIdFromToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }

        String token = authHeader.substring(7);
        try {
            Claims claims = JwtUtil.validateToken(token);
            return UUID.fromString(claims.get("id", String.class));
        } catch (Exception e) {
            return null; // Invalid token
        }
    }

    // Login Endpoint - generates a JWT for authenticated users
    @PostMapping("/auth/login")
    public ResponseEntity<Map<String, Object>> login(@RequestParam String username, @RequestParam String password) {
        Optional<PersonDTO> person = personService.authenticate(username, password);
        Map<String, Object> response = new HashMap<>();

        if (person.isPresent()) {
            // Generate JWT token
            String token = JwtUtil.generateToken(person.get());

            // Return token in the response
            response.put("message", "Login successful");
            response.put("token", token);
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "Invalid credentials");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    // Protected: Get all persons (admin only)
    @GetMapping()
    public ResponseEntity<List<PersonDTO>> getPersons(HttpServletRequest request) {
        if (!isAdmin(request)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        List<PersonDTO> dtos = personService.findPersons();
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    // Protected: Insert a new person (admin only)
    @PostMapping()
    public ResponseEntity<UUID> insertPerson(@Valid @RequestBody PersonDetailsDTO personDTO, HttpServletRequest request) {
        if (!isAdmin(request)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        UUID personID = personService.insert(personDTO);
        return new ResponseEntity<>(personID, HttpStatus.CREATED);
    }

    // Protected: Get a person by ID (user must be logged in)
    @GetMapping(value = "/{id}")
    public ResponseEntity<PersonDTO> getPerson(@PathVariable("id") UUID personId, HttpServletRequest request) {
        UUID userId = getUserIdFromToken(request);
        if (userId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        PersonDTO dto = personService.findPersonById(personId);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    // Protected: Delete a person (admin only)
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> deletePerson(@PathVariable("id") UUID personId, HttpServletRequest request) {
        if (!isAdmin(request)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        // Check if the user has an assigned device
        PersonDTO person = personService.findPersonById(personId);

        personService.delete(personId);
        return new ResponseEntity<>("Person with id " + personId + " was deleted!", HttpStatus.OK);
    }

    // Protected: Update a person (admin only)
    @PutMapping(value = "/{id}")
    public ResponseEntity<UUID> updatePerson(@PathVariable("id") UUID id, @Valid @RequestBody PersonDetailsDTO personDTO, HttpServletRequest request) {
        if (!isAdmin(request)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        UUID personId = personService.update(id, personDTO);
        return new ResponseEntity<>(personId, HttpStatus.OK);
    }

    // Get current logged-in person details (user must be logged in)
    @GetMapping("/auth/current")
    public ResponseEntity<PersonDTO> getCurrentPerson(HttpServletRequest request) {
        UUID userId = getUserIdFromToken(request);
        if (userId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        PersonDTO person = personService.findPersonById(userId);
        return new ResponseEntity<>(person, HttpStatus.OK);
    }


}
