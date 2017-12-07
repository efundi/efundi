package edu.nwu.sakai.studentlink.client;

import com.google.gwt.user.client.rpc.IsSerializable;

public class User implements IsSerializable {

    private String userName;

    private String password;

    private String firstName;

    private String surname;

    private String number;

    private boolean valid;

    private String message;

    private String email;
    
    private boolean isAdminUser;    

    public User() {
    }

    public User(String userName, String password, String firstName, String surname, String number,
            String message, boolean valid, boolean isAdminUser) {
        super();
        this.setUserName(userName);
        this.setPassword(password);
        this.setFirstName(firstName);
        this.setSurname(surname);
        this.setNumber(number);
        this.setMessage(number);
        this.setValid(valid);
        this.setAdminUser(isAdminUser);
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getSurname() {
        return surname;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getNumber() {
        return number;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public boolean isValid() {
        return valid;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

	public boolean isAdminUser() {
		return isAdminUser;
	}

	public void setAdminUser(boolean isAdminUser) {
		this.isAdminUser = isAdminUser;
	}

    public String toString() {
        return firstName + ", " + surname + ", " + userName + ", " + email;
    }
}
