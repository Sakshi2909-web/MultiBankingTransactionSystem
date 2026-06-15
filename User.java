public class User {

    private int userId;
    private String name;
    private String email;
    private String password;

    public User(int userId, String name,
                String email, String password) {

        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public int getUserId() {
        return userId;
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
} 
