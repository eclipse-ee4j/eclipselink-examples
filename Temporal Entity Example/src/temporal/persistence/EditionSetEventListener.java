package temporal.persistence;

import org.eclipse.persistence.config.DescriptorCustomizer;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.DescriptorEventAdapter;
import org.eclipse.persistence.internal.sessions.ObjectChangeSet;
import org.eclipse.persistence.internal.sessions.RepeatableWriteUnitOfWork;
import org.eclipse.persistence.internal.sessions.UnitOfWorkChangeSet;

import temporal.EditionSet;
import temporal.EditionSetEntry;

public class EditionSetEventListener extends DescriptorEventAdapter implements DescriptorCustomizer {

    @Override
    public void customize(ClassDescriptor descriptor) throws Exception {
        descriptor.getEventManager().addListener(this);
    }

    @Override
    public void preWrite(DescriptorEvent event) {
        EditionSet es = (EditionSet) event.getSource();
        RepeatableWriteUnitOfWork uow = (RepeatableWriteUnitOfWork) event.getSession();
        UnitOfWorkChangeSet uowCS = (UnitOfWorkChangeSet) uow.getUnitOfWorkChangeSet();
        
        if (es.hasEntries() && uowCS.hasChanges()) {
           for (EditionSetEntry entry: es.getEntries()) {
               ObjectChangeSet objCS = uowCS.getCloneToObjectChangeSet().get(entry.getEdition());
               System.out.println(objCS);
           }
        }
    }

}
