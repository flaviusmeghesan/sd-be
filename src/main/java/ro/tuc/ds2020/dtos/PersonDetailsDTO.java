package ro.tuc.ds2020.dtos;


import javax.validation.constraints.NotNull;
import java.util.UUID;

public class PersonDetailsDTO {

    private UUID id;
    @NotNull
    private String name;
    @NotNull
    private String role;
    @NotNull
    private String username;
    @NotNull
    private String password;

    public PersonDetailsDTO() {
    }

    public PersonDetailsDTO(UUID id, String name, String role, String username, String password) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.username = username;
        this.password = password;
    }

    public PersonDetailsDTO(String name, String role, String username, String password) {
        this.name = name;
        this.role = role;
        this.username = username;
        this.password = password;
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

    public void setRole( String role) {
        this.role = role;
    }

    public  String getUsername() {
        return username;
    }

    public void setUsername( String username) {
        this.username = username;
    }

    public  String getPassword() {
        return password;
    }

    public void setPassword( String password) {
        this.password = password;
    }
}
