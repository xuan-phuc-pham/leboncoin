package entities;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Contact {


    @Column(unique = true)
    private String c_login;
    private String c_password;

    private String c_firstName;
    private String c_lastName;

    @Id
    @OneToOne
    private Organization c_organization;

    @OneToMany(mappedBy = "of_contact")
    private List<Offer> c_offers;

    public Contact() {
    }

    public Contact(String c_login, String c_password, String c_firstName, String c_lastName, Organization c_organization) {
        this.c_login = c_login;
        this.c_password = c_password;
        this.c_firstName = c_firstName;
        this.c_lastName = c_lastName;
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

    public String getR_firstName() {
        return c_firstName;
    }

    public void setR_firstName(String c_firstName) {
        this.c_firstName = c_firstName;
    }

    public String getR_lastName() {
        return c_lastName;
    }

    public void setR_lastName(String c_lastName) {
        this.c_lastName = c_lastName;
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
