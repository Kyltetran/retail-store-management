package databasesystems.project2;

public class Supplier {
    private int id;
    private String name;
    private String contactPerson;
    private String phoneNumber;
    private String address;
    private String email;

    public Supplier(int id, String name, String contactPerson, String phoneNumber, String address, String email) {
        this.id = id;
        this.name = name;
        this.contactPerson = contactPerson;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.email = email;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public String getEmail() {
        return email;
    }
}
