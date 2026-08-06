package Entidad;

import jakarta.persistence.*;

@Entity
@Table(name = "adguard_politica_tablet", schema = "monitoreo tablet")
public class AdGuardPoliticaTablet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tablet_id")
    private Long tabletId;

    private String dominio;

    private String tipo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTabletId() {
        return tabletId;
    }

    public void setTabletId(Long tabletId) {
        this.tabletId = tabletId;
    }

    public String getDominio() {
        return dominio;
    }

    public void setDominio(String dominio) {
        this.dominio = dominio;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}