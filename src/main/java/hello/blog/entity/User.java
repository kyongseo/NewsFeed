package hello.blog.entity;


import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter

public class User {

    private Integer id;
    private String name;
    private String email;
    private String password;
    private Gender gender;
    private String phone;
    private String address;
    private String nicName;
    private int profileImage;


}
