package za.ac.nwu.model;

import za.ac.nwu.jobs.Utility;

public abstract class RosterUser {

    private String userName;

    private String firstName;

    private String surname;

    private String email;

    private String password = "password";

    private boolean foundInLDAP;

    public RosterUser(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isFoundInLDAP() {
        return foundInLDAP;
    }

    public void setFoundInLDAP(boolean foundInLDAP) {
        this.foundInLDAP = foundInLDAP;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (this == obj) return true;
        if (obj instanceof RosterUser) {
            RosterUser other = (RosterUser) obj;
            return Utility.equals(userName, other.userName);
        }
        else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Utility.hashCode(userName);
    }

    @Override
    public String toString() {
        return this.userName;
    }
}