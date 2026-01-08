

package entities;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int o_id;

    @Column(unique = true)
    private String o_name;

    private String o_description;

    @OneToMany(mappedBy = "m_organization",fetch = FetchType.EAGER, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Member> o_members;

    @OneToOne
    private Contact o_contact;

    public Organization() {
        this.o_members = new ArrayList<>();
    }

    public void addMember(Member member){
        this.o_members.add(member);
    }

    public Organization(String o_name, String o_description, Contact o_contact) {
        this.o_name = o_name;
        this.o_description = o_description;
        this.o_contact = o_contact;
        this.o_members = new ArrayList<Member>();
    }

    public String getO_name() {
        return o_name;
    }

    public void setO_name(String o_name) {
        this.o_name = o_name;
    }

    public String getO_description() {
        return o_description;
    }

    public void setO_description(String o_description) {
        this.o_description = o_description;
    }

    public int getO_id() {
        return o_id;
    }
}


