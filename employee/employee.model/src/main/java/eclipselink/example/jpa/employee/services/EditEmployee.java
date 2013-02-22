package eclipselink.example.jpa.employee.services;

import javax.ejb.Local;

import eclipselink.example.jpa.employee.model.Employee;
import eclipselink.example.jpa.employee.model.PhoneNumber;

/**
 * 
 * @author dclarke
 * @since EclipseLink 2.4.2
 */
@Local
public interface EditEmployee {

    public abstract Employee getEmployee();

    public abstract void setEmployee(Employee employee);

    public abstract boolean isNew();

    /**
     * 
     * @return
     */
    public abstract void save();

    public abstract void delete();

    public abstract void refresh();

    /**
     * Force the optimistic version field to be updated so that the save
     * operations will fail.
     */
    public abstract int updateVersion();

    public abstract String removeAddress();

    public abstract String addAddress();

    public abstract PhoneNumber addPhone(String type);

    public abstract void remove(PhoneNumber phone);

    public abstract void close();

}