package entities;

import jakarta.persistence.*;

@Entity
public class Organisation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int o_id;

    @Column(unique = true)
    private String o_name;

    private String o_description;

    public Organisation() {
    }

    public Organisation(String o_name, String o_description) {
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
