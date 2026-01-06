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
    private Integer c_id;

    @OneToOne
    private Organization c_organization;

    @OneToMany(mappedBy = "of_contact")
    private List<Offer> c_offers;

    public Contact() {
    }

    public Contact(Integer c_id ,String c_login, String c_password, String c_firstName, String c_lastName, Organization c_organization) {
        this.c_id = c_id;
        this.c_login = c_login;
        this.c_password = c_password;
        this.c_firstName = c_firstName;
        this.c_lastName = c_lastName;
        this.c_organization = c_organization;
    }

    public String getC_login() {
        return c_login;
    }

    public void setC_login(String c_login) {
        this.c_login = c_login;
    }

    public String getC_password() {
        return c_password;
    }

    public void setC_password(String c_password) {
        this.c_password = c_password;
    }

    public String getC_firstName() {
        return c_firstName;
    }

    public void setC_firstName(String c_firstName) {
        this.c_firstName = c_firstName;
    }

    public String getC_lastName() {
        return c_lastName;
    }

    public void setC_lastName(String c_lastName) {
        this.c_lastName = c_lastName;
    }

    public Integer getC_id() {
        return c_id;
    }

    public Organization getC_organization() {
        return c_organization;
    }

    public void setC_organization(Organization c_organization) {
        this.c_organization = c_organization;
    }

    public List<Offer> getC_offers() {
        return c_offers;
    }

    public void setC_offers(List<Offer> c_offers) {
        this.c_offers = c_offers;
    }
}


