package example.asm;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class EmpSummary {

    private Integer id;

    private String firstName;

    private String lastName;

    private String city;

    public EmpSummary() {
        super();
    }
    
    public EmpSummary(Integer id, String firstName, String lastName, String city) {
        super();
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.city = city;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

}
