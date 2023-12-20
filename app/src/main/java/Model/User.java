package Model;

public class User {
    private String id;
    private String login;
    private String password;
    private String email;
    private String imageurl;
    private String status;

    public User(){
    }

    public User(String id, String login, String password, String email, String imageurl, String status) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.email = email;
        this.imageurl = imageurl;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
