/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package caddie;

import java.util.ArrayList;

/**
 *
 * @author jona1993
 */
public class Panier {
    private ArrayList<Calculable> items;
    private int prix;

    public Panier() {
        items = new ArrayList<Calculable>();
        prix = 0;
    }

    public Panier(ArrayList<Calculable> items, int prix) {
        this.items = items;
        this.prix = prix;
    }

    public void setItems(ArrayList<Calculable> items) {
        this.items = items;
    }

    public void setPrix(int prix) {
        this.prix = prix;
    }

    public ArrayList<Calculable> getItems() {
        return items;
    }

    public int getPrix() {
        return prix;
    }

    public void addItem(Calculable arg){
        items.add(arg);
        
        prix += arg.getPrice();
    }
    
    public void removeItem(Calculable arg){
        items.remove(arg);
        
        prix -= arg.getPrice();
    }
    
    public void PurgePanier(){
        items.clear();
        prix = 0;
    }
    
    public void ApplyRemise(int tauxremise){
        prix = prix*(tauxremise/100);
    }
    
    public void ApplyTVA(int tauxTVA){
        prix = prix*((tauxTVA/100)+1);
    }
    
    public void ResetPrix() {
        prix = 0;
        
        for(Calculable cal : items) {
            prix += cal.getPrice();
        }
    }
    
    public boolean IsEmpty() {
        return items.isEmpty();
    }
    
}
