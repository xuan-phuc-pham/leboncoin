package entities;

import jakarta.persistence.*;

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
    private Organization m_organization;

    @OneToMany(mappedBy = "w_member", fetch = FetchType.EAGER)
    private List<Wish> m_wishes;

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

    public List<Wish> getM_wishes() {
        return m_wishes;
    }

    public void setM_wishes(List<Wish> m_wishes) {
        this.m_wishes = m_wishes;
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



    public Organization getM_organisation() {
        return m_organization;
    }

    public void setM_organisation(Organization m_organization) {
        this.m_organization = m_organization;
    }


}
