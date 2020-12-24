package com.ferbook.models;

public class User {

    private String id;
    private String email;
    private String nombre;
    private String telefono;
    private Long timestamp;

    public User () {

    }

    public User(String id, String email, String nombre, String telefono, Long timestamp) {
        this.id = id;
        this.email = email;
        this.nombre = nombre;
        this.telefono = telefono;
        this.timestamp = timestamp;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
