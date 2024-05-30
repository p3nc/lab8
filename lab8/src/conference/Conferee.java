package conference;

import java.io.Serializable;

public class Conferee implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String surname;
    private String affiliation;
    private String email;
    private String paperTitle;

    public Conferee() {
    }

    public Conferee(String name, String surname, String affiliation, String email, String paperTitle) {
        this.name = name;
        this.surname = surname;
        this.affiliation = affiliation;
        this.email = email;
        this.paperTitle = paperTitle;
    }

    // Геттери та сеттери
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getAffiliation() {
        return affiliation;
    }

    public void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPaperTitle() {
        return paperTitle;
    }

    public void setPaperTitle(String paperTitle) {
        this.paperTitle = paperTitle;
    }

    @Override
    public String toString() {
        return "Conferee{" +
                "name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", affiliation='" + affiliation + '\'' +
                ", email='" + email + '\'' +
                ", paperTitle='" + paperTitle + '\'' +
                '}';
    }
}