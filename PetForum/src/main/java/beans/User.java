package beans;

public class User {
	private int id;
	private String username;
	private String password;
    private String salt;
	private int role;
	
    public User(int id, String username, String password, String salt, int role) {
    	this.id = id;
    	this.username = username;
    	this.password = password;
        this.salt = salt;
    	this.role = role;
    }
    
    // Getter 和 Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSalt(){
        return salt;
    }

    public void setSalt(String salt){
        this.salt = salt;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    public int getRole() {
    	return this.role;
    }
    
}
