package esteticaapp.co.kaxan.UA;

public class UM extends UsuarioGeneral{

    private String bateria;
    private String senial;
    private String latitud;
    private String longitud;
    private String lugar;

    public UM(String nombre, String apellidos, String fecha_nacimiento, String edad, String telefono, String correo, String contrasena, int foto, String bateria, String senial, String latitud, String longitud, String lugar) {
        super(nombre, apellidos, fecha_nacimiento, edad, telefono, correo, contrasena, foto);
        this.bateria = bateria;
        this.senial = senial;
        this.latitud = latitud;
        this.longitud = longitud;
        this.lugar = lugar;
    }

    public UM(String nombre, int foto, String bateria, String senial, String latitud, String longitud) {
        super(nombre, foto);
        this.bateria = bateria;
        this.senial = senial;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public UM(String nombre, int foto, String bateria, String senial, String latitud, String longitud, String lugar) {
        super(nombre, foto);
        this.bateria = bateria;
        this.senial = senial;
        this.latitud = latitud;
        this.longitud = longitud;
        this.lugar = lugar;
    }

    public UM(String nombre, String bateria, String senial, String latitud, String longitud, String lugar) {
        super(nombre);
        this.bateria = bateria;
        this.senial = senial;
        this.latitud = latitud;
        this.longitud = longitud;
        this.lugar = lugar;
    }

    public UM(String nombre, String bateria, String senial, String latitud, String longitud) {
        super(nombre);
        this.bateria = bateria;
        this.senial = senial;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public UM(String nombre, String bateria, String latitud, String longitud) {
        super(nombre);
        this.bateria = bateria;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public UM() {

    }

    public String getBateria() {
        return bateria;
    }

    public void setBateria(String bateria) {
        this.bateria = bateria;
    }

    public String getSenial() {
        return senial;
    }

    public void setSenial(String senial) {
        this.senial = senial;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public String getLugar() {
        return lugar;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }
}
