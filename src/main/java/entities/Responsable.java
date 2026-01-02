package entities;

import jakarta.persistence.*;

import entities.Organisation;

import java.util.List;

@Entity
public class Responsable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long r_id;

    @Column(unique = true)
    private String r_login;
    private String r_password;

    private String r_fname;
    private String r_lname;

    @OneToOne
    private Organisation r_organisation;

    @OneToMany(mappedBy = "of_responsable")
    private List<Offer> r_offers;

    public Responsable() {
    }

    public Responsable(String r_login, String r_password, String r_fname, String r_lname, Organisation r_organisation) {
        this.r_login = r_login;
        this.r_password = r_password;
        this.r_fname = r_fname;
        this.r_lname = r_lname;
        this.r_organisation = r_organisation;
    }

    public Long getR_id() {
        return r_id;
    }

    public String getR_login() {
        return r_login;
    }

    public void setR_login(String r_login) {
        this.r_login = r_login;
    }

    public String getR_password() {
        return r_password;
    }

    public void setR_password(String r_password) {
        this.r_password = r_password;
    }

    public String getR_fname() {
        return r_fname;
    }

    public void setR_fname(String r_fname) {
        this.r_fname = r_fname;
    }

    public String getR_lname() {
        return r_lname;
    }

    public void setR_lname(String r_lname) {
        this.r_lname = r_lname;
    }

    public Organisation getR_organisation() {
        return r_organisation;
    }

    public void setR_organisation(Organisation r_organisation) {
        this.r_organisation = r_organisation;
    }

    public List<Offer> getR_offers() {
        return r_offers;
    }

    public void setR_offers(List<Offer> r_offers) {
        this.r_offers = r_offers;
    }
}
