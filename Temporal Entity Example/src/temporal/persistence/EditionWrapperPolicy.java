package temporal.persistence;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.WrapperPolicy;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.internal.descriptors.PersistenceObject;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.FetchGroupTracker;
import org.eclipse.persistence.sessions.Session;

import temporal.EditionSetEntry;
import temporal.TemporalEdition;
import temporal.TemporalEntity;
import temporal.TemporalEntityManager;
import temporal.TemporalHelper;

public class EditionWrapperPolicy implements WrapperPolicy {
    private static final long serialVersionUID = 1L;

    private AbstractSession session;

    private ClassDescriptor descriptor;

    @Override
    public void initialize(AbstractSession session) throws DescriptorException {
        this.session = session;
    }

    public AbstractSession getSession() {
        return session;
    }

    @Override
    public boolean isTraversable() {
        return true;
    }

    @Override
    public boolean isWrapped(Object object) {
        return Proxy.isProxyClass(object.getClass());
    }

    @Override
    public void setDescriptor(ClassDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    public ClassDescriptor getDescriptor() {
        return descriptor;
    }

    @Override
    public Object unwrapObject(Object proxy, AbstractSession session) {
        Handler<?> handler = (Handler<?>) Proxy.getInvocationHandler(proxy);
        return handler.getEntity();
    }

    @Override
    public Object wrapObject(Object original, AbstractSession session) {
        Class<?> wrapperInterface = (Class<?>) getDescriptor().getProperty(TemporalHelper.INTERFACE);
        Handler<?> handler = new Handler<TemporalEntity<?>>((TemporalEntity<?>) original);
        return Proxy.newProxyInstance(getSession().getLoader(), new Class[] { wrapperInterface, TemporalEdition.class, PersistenceObject.class, FetchGroupTracker.class }, handler);
    }

    public class Handler<T extends TemporalEntity<?>> implements InvocationHandler, TemporalEdition<T> {

        /**
         * The original edition being used by the client. This edition will not
         * be changed with the exception of the end effectivity time if a new
         * edition is created.
         */
        private T entity;

        /**
         * 
         */
        private TemporalEntityManager entityManager;

        private Map<String, Object[]> changes;

        public Handler(T entity) {
            this.entity = entity;
            this.changes = new HashMap<String, Object[]>();
        }

        public T getEntity() {
            return this.entity;
        }

        public TemporalEntityManager getEntityManager() {
            if (this.entityManager == null && getEntity() instanceof FetchGroupTracker) {
                Session session = ((FetchGroupTracker) getEntity())._persistence_getSession();
                if (session != null) {
                    this.entityManager = TemporalEntityManager.getInstance(session);
                }
            }
            return entityManager;
        }

        public void setEntityManager(TemporalEntityManager entityManager) {
            this.entityManager = entityManager;
        }

        public Map<String, Object[]> getChanges() {
            return changes;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getDeclaringClass() == TemporalEdition.class) {
                return method.invoke(this, args);
            }

            return method.invoke(getEntity(), args);
        }

        public boolean hasChanges() {
            return !getChanges().isEmpty();
        }

        @Override
        public EditionSetEntry getEditionSetEntry() {
            // TODO Auto-generated method stub
            return null;
        }

    }

}
