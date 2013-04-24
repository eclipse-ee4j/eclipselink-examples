package eclipselink.example.mysports.admin.services;

import java.util.List;

import javax.ejb.Local;

import eclipselink.example.mysports.admin.model.HostEnvironment;

@Local
public interface HostEnvironmentRepository {

    void create(HostEnvironment env);

    void delete(HostEnvironment env);

    HostEnvironment find(String name);
    
    List<HostEnvironment> getEnvironments();
}