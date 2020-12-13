package com.ferbook.models;

public class Post {

    private String id;
    private String titulo;
    private String descripcion;
    private String image1;
    private String image2;
    private String category;

    public Post () {

    }

    public Post(String id, String titulo, String descripcion, String image1, String image2, String category, String usuario) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.image1 = image1;
        this.image2 = image2;
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

}
