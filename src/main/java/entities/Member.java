package entities;

import jakarta.persistence.*;

import entities.Organisation;

import java.util.List;

@Entity

public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int m_id;

    @Column(unique = true)
    private String m_login;

    private String m_password;

    private String m_fname;

    private String m_lname;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Organisation m_organisation;

    @OneToMany(mappedBy = "de_member", fetch = FetchType.EAGER)
    private List<Demande> m_demandes;

    public Member() {
    }

    public Member(String m_login, String m_password, String m_fname, String m_lname) {
        this.m_login = m_login;
        this.m_password = m_password;
        this.m_fname = m_fname;
        this.m_lname = m_lname;
    }

    public int getM_id() {
        return m_id;
    }

    public List<Demande> getM_demandes() {
        return m_demandes;
    }

    public void setM_demandes(List<Demande> m_demandes) {
        this.m_demandes = m_demandes;
    }

    public String getM_login() {
        return m_login;
    }

    public void setM_login(String m_login) {
        this.m_login = m_login;
    }

    public String getM_password() {
        return m_password;
    }

    public void setM_password(String m_password) {
        this.m_password = m_password;
    }

    public String getM_fname() {
        return m_fname;
    }

    public void setM_fname(String m_fname) {
        this.m_fname = m_fname;
    }

    public String getM_lname() {
        return m_lname;
    }

    public void setM_lname(String m_lname) {
        this.m_lname = m_lname;
    }



    public Organisation getM_organisation() {
        return m_organisation;
    }

    public void setM_organisation(Organisation m_organisation) {
        this.m_organisation = m_organisation;
    }


}
