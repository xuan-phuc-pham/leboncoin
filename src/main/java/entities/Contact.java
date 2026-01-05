package entities;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Contact {


    @Column(unique = true)
    private String c_login;
    private String c_password;

    private String c_fname;
    private String c_lname;

    @Id
    @OneToOne
    private Organization c_organization;

    @OneToMany(mappedBy = "of_contact")
    private List<Offer> c_offers;

    public Contact() {
    }

    public Contact(String c_login, String c_password, String c_fname, String c_lname, Organization c_organization) {
        this.c_login = c_login;
        this.c_password = c_password;
        this.c_fname = c_fname;
        this.c_lname = c_lname;
        this.c_organization = c_organization;
    }

    public String getR_login() {
        return c_login;
    }

    public void setR_login(String c_login) {
        this.c_login = c_login;
    }

    public String getR_password() {
        return c_password;
    }

    public void setR_password(String c_password) {
        this.c_password = c_password;
    }

    public String getR_fname() {
        return c_fname;
    }

    public void setR_fname(String c_fname) {
        this.c_fname = c_fname;
    }

    public String getR_lname() {
        return c_lname;
    }

    public void setR_lname(String c_lname) {
        this.c_lname = c_lname;
    }

    public Organization getR_organisation() {
        return c_organization;
    }


    public List<Offer> getR_offers() {
        return c_offers;
    }

    public void setR_offers(List<Offer> c_offers) {
        this.c_offers = c_offers;
    }
}
