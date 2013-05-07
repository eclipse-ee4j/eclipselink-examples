package eclipselink.example.jpamoxy.simple.model;

import java.io.Serializable;
import javax.persistence.*;

import org.eclipse.persistence.oxm.annotations.XmlInverseReference;


/**
 * The persistent class for the PHONE_NUMBER database table.
 * 
 */
@Entity
@Table(name="PHONE_NUMBER")
public class PhoneNumber implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private long id;

	private String num;

	private String type;

	//bi-directional many-to-one association to Customer
    @ManyToOne
	@JoinColumn(name="ID_CUSTOMER")
	@XmlInverseReference(mappedBy="phoneNumbers")
	private Customer customer;

    public PhoneNumber() {
    }

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getNum() {
		return this.num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Customer getCustomer() {
		return this.customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	
}