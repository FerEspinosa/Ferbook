package com.ferbook.models;

public class Post {

    private String idUser;
    private String titulo;
    private String descripcion;
    private String image1;
    private String image2;
    private String category;
    private Long   timestamp;

    public Post () {

    }

    public Post(String idUser, String titulo, String descripcion, String image1, String image2, String category, Long timestamp) {
        this.idUser      = idUser;
        this.titulo      = titulo;
        this.descripcion = descripcion;
        this.image1      = image1;
        this.image2      = image2;
        this.category    = category;
        this.timestamp   = timestamp;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getImage1() {
        return image1;
    }

    public void setImage1(String image1) {
        this.image1 = image1;
    }

    public String getImage2() {
        return image2;
    }

    public void setImage2(String image2) {
        this.image2 = image2;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
