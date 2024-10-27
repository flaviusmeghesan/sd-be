package ro.tuc.ds2020.dtos;

import java.util.Objects;
import java.util.UUID;

public class PersonDTO {
    private UUID id;
    private String name;
    private String role;
    private String username;
    private String password;
    private UUID assigned_device_id;

    public PersonDTO() {
    }

    public PersonDTO(UUID id, String name, String role, String username, String password, UUID assigned_device_id) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.username = username;
        this.password = password;
        this.assigned_device_id = assigned_device_id;
    }


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UUID getAssigned_device_id() {
        return assigned_device_id;
    }

    public void setAssigned_device_id(UUID assigned_device_id) {
        this.assigned_device_id = assigned_device_id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PersonDTO personDTO)) return false;
        return Objects.equals(id, personDTO.id) &&
                Objects.equals(name, personDTO.name) &&
                Objects.equals(role, personDTO.role) &&
                Objects.equals(username, personDTO.username) &&
                Objects.equals(password, personDTO.password) &&
                Objects.equals(assigned_device_id, personDTO.assigned_device_id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, role, username, password, assigned_device_id);
    }

}
