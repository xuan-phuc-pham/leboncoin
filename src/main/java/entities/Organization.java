

package entities;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int o_id;

    @Column(unique = true)
    private String o_name;

    private String o_description;

    @OneToMany
    private List<Member> members;

    @OneToOne
    private Contact contact;

    public Organization() {
    }

    public Organization(String o_name, String o_description) {
        this.o_name = o_name;
        this.o_description = o_description;
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


