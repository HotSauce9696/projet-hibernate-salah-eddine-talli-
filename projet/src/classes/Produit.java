/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classes;

/**
 *
 * @author HP
 */
import javax.persistence.*;
@Entity
@Table(name = "produit")

public class Produit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "code")
    private String code;
    @Column(name = "libelle")
    private String libelle;
    @Column(name = "type")
    private String type;
    @Column(name = "quantite_en_stock")
    private int quantiteEnStock; 
    @Column(name = "disponibilite")
    private boolean disponibilite;
    
    public Produit() {}
    public Produit(String code, String libelle, String type, int quantiteEnStock, boolean disponibilite) {
        this.code = code;
        this.libelle = libelle;
        this.type = type;
        this.quantiteEnStock = quantiteEnStock;
        this.disponibilite = disponibilite;
    }
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public int getQuantiteEnStock() { return quantiteEnStock; }
    public void setQuantiteEnStock(int quantiteEnStock) { 
        this.quantiteEnStock = quantiteEnStock;
        this.disponibilite = quantiteEnStock > 0;
    }
    public boolean isDisponibilite() { return disponibilite; }
    public void setDisponibilite(boolean disponibilite) { this.disponibilite = disponibilite; }
    @Override
    public String toString() {
        return String.format("Produit{id=%d, code='%s', libelle='%s', type='%s', stock=%d, disponible=%s}",
                id, code, libelle, type, quantiteEnStock, disponibilite);
    }
} 

