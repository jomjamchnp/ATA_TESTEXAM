package utils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Represents a GoRest user payload for POST / PUT requests
 * and also maps GET response bodies.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserPayload {

    private int    id;
    private String name;
    private String email;
    private String gender;
    private String status;

    // ----------------------------------------------------------------
    // Constructors
    // ----------------------------------------------------------------
    public UserPayload() {}

    public UserPayload(String name, String email, String gender, String status) {
        this.name   = name;
        this.email  = email;
        this.gender = gender;
        this.status = status;
    }

    // ----------------------------------------------------------------
    // Getters & Setters
    // ----------------------------------------------------------------
    public int getId()               { return id; }
    public void setId(int id)        { this.id = id; }

    public String getName()              { return name; }
    public void setName(String name)     { this.name = name; }

    public String getEmail()             { return email; }
    public void setEmail(String email)   { this.email = email; }

    public String getGender()            { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getStatus()            { return status; }
    public void setStatus(String status) { this.status = status; }
}
