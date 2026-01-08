package entities;

import jakarta.persistence.*;

@Entity
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int c_id;

    @Column(unique = true)
    private String c_name;

    public Category(){}

    public Category(String c_name){
        this.c_name = c_name;
    }
}
