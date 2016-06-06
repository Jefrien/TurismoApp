package org.jefrienalvizures.turismo.bean;

/**
 * Created by Jefrien Alvizures on 26/05/2016.
 */
public class Usuario {
    private Integer id;
    private String usuario;
    private String nombre;
    private String correo;
    private String token;
    private String exp;

    public Usuario() {
    }

    public Usuario(Integer id, String usuario, String nombre, String correo, String token, String exp) {
        this.id = id;
        this.usuario = usuario;
        this.nombre = nombre;
        this.correo = correo;
        this.token = token;
        this.exp = exp;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getExp() {
        return exp;
    }

    public void setExp(String exp) {
        this.exp = exp;
    }
}
