package dtos;

public record RegOrgContact(
        String org_name,
        String org_description,
        String contact_fname,
        String contact_lname,
        String contact_login ,
        String contact_pword) { }
