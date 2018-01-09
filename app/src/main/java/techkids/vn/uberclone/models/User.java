package techkids.vn.uberclone.models;

/**
 * Created by ADMIN on 1/8/2018.
 */

public class User {
    private String email;
    private String password;
    private String name;
    private String phone;

    public User(){
        //non body constructor
    }

    public User(String email, String password, String name, String phone){
        this.email = email;
        this.password = password;
        this.name = name;
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }
}
