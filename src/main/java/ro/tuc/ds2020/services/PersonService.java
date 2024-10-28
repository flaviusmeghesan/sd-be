package ro.tuc.ds2020.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ro.tuc.ds2020.controllers.handlers.exceptions.model.ResourceNotFoundException;
import ro.tuc.ds2020.dtos.PersonDTO;
import ro.tuc.ds2020.dtos.PersonDetailsDTO;
import ro.tuc.ds2020.dtos.builders.PersonBuilder;
import ro.tuc.ds2020.entities.Person;
import ro.tuc.ds2020.repositories.PersonRepository;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PersonService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PersonService.class);
    private final PersonRepository personRepository;
    private final RestTemplate restTemplate; // For HTTP calls to the device microservice

    @Value("${device.microservice.url}")
    private String deviceMicroserviceUrl;
    @Autowired
    public PersonService(PersonRepository personRepository, RestTemplate restTemplate) {
        this.personRepository = personRepository;
        this.restTemplate = restTemplate;
    }

    public List<PersonDTO> findPersons() {
        List<Person> personList = personRepository.findAll();
        return personList.stream()
                .map(PersonBuilder::toPersonDTO)
                .collect(Collectors.toList());
    }

    public PersonDTO findPersonById(UUID id) {
        Optional<Person> prosumerOptional = personRepository.findById(id);
        if (!prosumerOptional.isPresent()) {
            LOGGER.error("Person with id {} was not found in db", id);
            throw new ResourceNotFoundException(Person.class.getSimpleName() + " with id: " + id);
        }
        return PersonBuilder.toPersonDTO(prosumerOptional.get());
    }

    public void delete(UUID id) {
        Optional<Person> personOptional = personRepository.findById(id);
        if (!personOptional.isPresent()) {
            LOGGER.error("Person with id {} was not found in db", id);
            throw new ResourceNotFoundException(Person.class.getSimpleName() + " with id: " + id);
        }

        // Call device microservice to check for mappings
        if (hasAssignedDevices(id)) {
            LOGGER.error("Person with id {} has assigned devices and cannot be deleted", id);
            throw new IllegalStateException("Cannot delete person with id " + id + " because they are assigned to one or more devices.");
        }

        personRepository.deleteById(id);
        LOGGER.info("Person with id {} was deleted successfully", id);
    }

    // Helper method to check if user has assigned devices
    private boolean hasAssignedDevices(UUID userId) {
        String url = deviceMicroserviceUrl + "/mappings/user/" + userId;
        Boolean hasMappings = restTemplate.getForObject(url, Boolean.class);
        return Boolean.TRUE.equals(hasMappings); // Return true if mappings exist
    }
    public UUID insert(PersonDetailsDTO personDTO) {
        Person person = PersonBuilder.toEntity(personDTO);
        person = personRepository.save(person);
        LOGGER.debug("Person with id {} was inserted in db", person.getId());
        return person.getId();
    }

    public UUID update(UUID id, @Valid PersonDetailsDTO personDTO) {
        Optional<Person> personOptional = personRepository.findById(id);
        if (!personOptional.isPresent()) {
            LOGGER.error("Person with id {} was not found in db", id);
            throw new ResourceNotFoundException(Person.class.getSimpleName() + " with id: " + id);
        }
        Person person = PersonBuilder.toEntity(personDTO);
        person.setId(id);
        person = personRepository.save(person);
        LOGGER.debug("Person with id {} was updated in db", id);
        return person.getId();
    }

    public Optional<PersonDTO> authenticate(String username, String password) {
        Optional<Person> personOptional = personRepository.findByUsername(username);
        return personOptional
                .filter(person -> person.getPassword().equals(password))
                .map(PersonBuilder::toPersonDTO);
    }


}
