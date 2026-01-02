package entities;

import jakarta.persistence.*;

import entities.Responsable;

import java.util.Queue;
import java.util.Set;

@Entity
public class Offer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long of_id;

    @ManyToOne
    private Responsable of_responsable;

    @ManyToMany
    private Set<Category> of_categories;

    @OneToMany(mappedBy = "de_offer")
    private Queue<Demande> of_demandes;

    private String of_name;
    private String of_description;

    public Offer() {
    }

    public Offer(Responsable of_responsable, String of_name, String of_description) {
        this.of_responsable = of_responsable;
        this.of_name = of_name;
        this.of_description = of_description;
    }

    public Long getOf_id() {
        return of_id;
    }

    public Responsable getOf_responsable() {
        return of_responsable;
    }

    public void setOf_responsable(Responsable of_responsable) {
        this.of_responsable = of_responsable;
    }

    public Set<Category> getOf_categories() {
        return of_categories;
    }

    public void setOf_categories(Set<Category> of_categories) {
        this.of_categories = of_categories;
    }

    public Queue<Demande> getOf_demandes() {
        return of_demandes;
    }

    public void setOf_demandes(Queue<Demande> of_demandes) {
        this.of_demandes = of_demandes;
    }

    public String getOf_name() {
        return of_name;
    }

    public void setOf_name(String of_name) {
        this.of_name = of_name;
    }

    public String getOf_description() {
        return of_description;
    }

    public void setOf_description(String of_description) {
        this.of_description = of_description;
    }

}
