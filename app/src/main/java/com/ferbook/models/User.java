package com.ferbook.models;

public class User {

    private String id;
    private String email;
    private String nombre;
    private String telefono;
    private Long timestamp;
    private String profile_image;
    private String cover_image;
    private Long lastConnection;
    private boolean online;

    public User () {

    }

    public User(String id, String email, String nombre, String telefono, Long timestamp, String profile_image, String cover_image, Long lastConnection, boolean online) {
        this.id = id;
        this.email = email;
        this.nombre = nombre;
        this.telefono = telefono;
        this.timestamp = timestamp;
        this.profile_image = profile_image;
        this.cover_image = cover_image;
        this.lastConnection = lastConnection;
        this.online = online;
    }

    public Long getLastConnection() {
        return lastConnection;
    }

    public void setLastConnection(Long lastConnection) {
        this.lastConnection = lastConnection;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
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

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public String getCover_image() {
        return cover_image;
    }

    public void setCover_image(String cover_image) {
        this.cover_image = cover_image;
    }
}
