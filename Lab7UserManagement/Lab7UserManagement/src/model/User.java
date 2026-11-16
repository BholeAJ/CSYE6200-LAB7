package model;

import java.time.LocalDate;

/**
 * One user record (one row of the users table).
 */
public class User {

    private int id;
    private String firstName;
    private String lastName;
    private String gender;
    private LocalDate dob;
    private int age;
    private String phone;
    private String email;
    private String continent;
    private String experience;
    private String hobbies;
    private String photoPath;

    public User() {
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getContinent() { return continent; }
    public void setContinent(String continent) { this.continent = continent; }

    public String getExperience() { return experience; }
    public void setExperience(String experience) { this.experience = experience; }

    public String getHobbies() { return hobbies; }
    public void setHobbies(String hobbies) { this.hobbies = hobbies; }

    public String getPhotoPath() { return photoPath; }
    public void setPhotoPath(String photoPath) { this.photoPath = photoPath; }

    /** All of the user's details, one per line (shown in the Success / User Details pop-ups). */
    @Override
    public String toString() {
        return "First Name: " + firstName
                + "\nLast Name: " + lastName
                + "\nGender: " + gender
                + "\nDate of Birth: " + dob
                + "\nAge: " + age
                + "\nPhone: " + phone
                + "\nEmail: " + email
                + "\nContinent: " + continent
                + "\nExperience: " + (experience == null || experience.isEmpty() ? "-" : experience)
                + "\nHobbies: " + (hobbies == null || hobbies.isEmpty() ? "-" : hobbies)
                + "\nPhoto: " + (photoPath == null || photoPath.isEmpty() ? "(none)" : photoPath);
    }
}
