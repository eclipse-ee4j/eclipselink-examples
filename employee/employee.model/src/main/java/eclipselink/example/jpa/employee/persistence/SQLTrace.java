package eclipselink.example.jpa.employee.persistence;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.logging.SessionLogEntry;

public class SQLTrace {

    private List<SessionLogEntry> entries = new ArrayList<SessionLogEntry>();
    
    protected void add(SessionLogEntry entry) {
        if (entry.getNameSpace().equals(SessionLog.SQL)) {
            this.entries.add(entry);
        }
    }

    public List<SessionLogEntry> getEntries() {
        return entries;
    }

}
