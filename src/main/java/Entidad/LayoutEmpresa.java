package Entidad;

import jakarta.persistence.*;

@Entity
@Table(name = "layout_empresa", schema = "monitoreo tablet")
public class LayoutEmpresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @Column(name = "imagen_url")
    private String imagenUrl;

    @Column(name = "lat_north")
    private Double latNorth;

    @Column(name = "lat_south")
    private Double latSouth;

    @Column(name = "lng_east")
    private Double lngEast;

    @Column(name = "lng_west")
    private Double lngWest;

    @Column(name = "archivo", columnDefinition = "bytea")
    private byte[] archivo;

    @Column(name = "tipo_archivo")
    private String tipoArchivo;

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public Double getLatNorth() {
        return latNorth;
    }

    public Double getLatSouth() {
        return latSouth;
    }

    public Double getLngEast() {
        return lngEast;
    }

    public Double getLngWest() {
        return lngWest;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public void setLatNorth(Double latNorth) {
        this.latNorth = latNorth;
    }

    public void setLatSouth(Double latSouth) {
        this.latSouth = latSouth;
    }

    public void setLngEast(Double lngEast) {
        this.lngEast = lngEast;
    }

    public void setLngWest(Double lngWest) {
        this.lngWest = lngWest;
    }

    public byte[] getArchivo() {
        return archivo;
    }

    public void setArchivo(byte[] archivo) {
        this.archivo = archivo;
    }

    public String getTipoArchivo() {
        return tipoArchivo;
    }

    public void setTipoArchivo(String tipoArchivo) {
        this.tipoArchivo = tipoArchivo;
    }
}