package entities;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Contact {


    @Column(unique = true)
    private String r_login;
    private String r_password;

    private String r_fname;
    private String r_lname;

    @Id
    @OneToOne
    private Organization r_organization;

    @OneToMany(mappedBy = "of_contact")
    private List<Offer> r_offers;

    public Contact() {
    }

    public Contact(String r_login, String r_password, String r_fname, String r_lname, Organization r_organization) {
        this.r_login = r_login;
        this.r_password = r_password;
        this.r_fname = r_fname;
        this.r_lname = r_lname;
        this.r_organization = r_organization;
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

    public Organization getR_organisation() {
        return r_organization;
    }


    public List<Offer> getR_offers() {
        return r_offers;
    }

    public void setR_offers(List<Offer> r_offers) {
        this.r_offers = r_offers;
    }
}
