package eclipselink.example.jpamoxy.simple.model;

import java.io.Serializable;
import javax.persistence.*;

import org.eclipse.persistence.oxm.annotations.XmlInverseReference;


/**
 * The persistent class for the ADDRESS database table.
 * 
 */
@Entity
public class Address implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private long id;

	private String city;

	private String street;

	//bi-directional one-to-one association to Customer
	@OneToOne
	@JoinColumn(name="ID")
	@MapsId
	@XmlInverseReference(mappedBy="address")
	private Customer customer;

    public Address() {
    }

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCity() {
		return this.city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getStreet() {
		return this.street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public Customer getCustomer() {
		return this.customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	
}