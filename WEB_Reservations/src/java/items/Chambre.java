/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package items;

import caddie.Calculable;

/**
 *
 * @author jona1993
 */
public class Chambre implements Calculable {
    private int numero;
    private String equipement;
    private int nboccupants;
    private int prixHTVA;
    private String categorie;
    private String type;

    public Chambre() {
    }

    public Chambre(int numero, String equipement, int nboccupants, int prixHTVA, String categorie, String type) {
        this.numero = numero;
        this.equipement = equipement;
        this.nboccupants = nboccupants;
        this.prixHTVA = prixHTVA;
        this.categorie = categorie;
        this.type = type;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public void setEquipement(String equipement) {
        this.equipement = equipement;
    }

    public void setNboccupants(int nboccupants) {
        this.nboccupants = nboccupants;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public void setPrixHTVA(int prixHTVA) {
        this.prixHTVA = prixHTVA;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategorie() {
        return categorie;
    }

    public String getEquipement() {
        return equipement;
    }

    public int getNboccupants() {
        return nboccupants;
    }

    public int getNumero() {
        return numero;
    }

    
    public int getPrixHTVA() {
        return prixHTVA;
    }

    public String getType() {
        return type;
    }
    
    @Override
    public int getPrice() {
        return prixHTVA;
    }

   
}
