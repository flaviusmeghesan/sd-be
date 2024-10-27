package ro.tuc.ds2020.dtos.builders;

import ro.tuc.ds2020.dtos.PersonDTO;
import ro.tuc.ds2020.dtos.PersonDetailsDTO;
import ro.tuc.ds2020.entities.Person;

public class PersonBuilder {

    private PersonBuilder() {
    }

    public static PersonDTO toPersonDTO(Person person) {
        return new PersonDTO(person.getId(), person.getName(), person.getRole(), person.getUsername(), person.getPassword(), person.getAssigned_device_id());
    }

    public static Person toEntity(PersonDetailsDTO personDetailsDTO) {
        return new Person(
                personDetailsDTO.getId(),
                personDetailsDTO.getName(),
                personDetailsDTO.getRole(),
                personDetailsDTO.getUsername(),
                personDetailsDTO.getPassword(),
                personDetailsDTO.getAssigned_device_id());
    }
}
