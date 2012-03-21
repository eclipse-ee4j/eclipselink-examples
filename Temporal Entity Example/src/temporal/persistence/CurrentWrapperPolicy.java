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

public class CurrentWrapperPolicy implements WrapperPolicy {
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
        InvocationHandler handler = Proxy.getInvocationHandler(proxy);

        if (handler instanceof CurrentWrapperHandler<?>) {
            return ((CurrentWrapperHandler<?>) handler).getOriginal();
        }

        if (handler instanceof EditionWrapperPolicy.Handler<?>) {
            return ((EditionWrapperPolicy.Handler<?>) handler).getEntity();
        }
        throw new IllegalArgumentException("Unknown proxy: " + proxy);
    }

    @Override
    public Object wrapObject(Object original, AbstractSession session) {
        Class<?> wrapperInterface = (Class<?>) getDescriptor().getProperty(TemporalHelper.INTERFACE);
        CurrentWrapperHandler<?> handler = new CurrentWrapperHandler<TemporalEntity<?>>((TemporalEntity<?>) original);
        return Proxy.newProxyInstance(getSession().getLoader(), new Class[] { wrapperInterface, TemporalEdition.class, PersistenceObject.class, FetchGroupTracker.class }, handler);
    }

    /**
     * {@link Proxy} wrapper for {@link TemporalEntity} to handle the update
     * scenarios where changes made to an edition should result in new editions
     * being created.
     * 
     * @author dclarke
     * @since EclipseLink 2.3.1
     */
    public class CurrentWrapperHandler<T extends TemporalEntity<?>> implements InvocationHandler, TemporalEdition<T> {

        /**
         * The original edition being used by the client. This edition will not
         * be changed with the exception of the end effectivity time if a new
         * edition is created.
         */
        private T original;

        /**
         * The new edition created if a set method is invoked
         */
        private T newEdition;

        private TemporalEntityManager entityManager;

        /**
         * 
         */
        private Map<String, Object[]> changes;

        public CurrentWrapperHandler(T original) {
            this.original = original;
        }

        public T getOriginal() {
            return original;
        }

        public T getNewEdition() {
            return newEdition;
        }

        public T getEntity() {
            if (getNewEdition() != null) {
                return getNewEdition();
            }
            return getOriginal();
        }

        public TemporalEntityManager getEntityManager() {
            if (this.entityManager == null && getOriginal() instanceof FetchGroupTracker) {
                Session session = ((FetchGroupTracker) getOriginal())._persistence_getSession();
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

            if ("toString".equals(method.getName())) {
                return "CurrentEntityProxy[" + getEntity() + "]";
            }

            if (getEntityManager() != null && args != null && args.length > 0) {
                if (this.newEdition == null) {
                    this.newEdition = getEntityManager().newEdition(getOriginal());
                    this.changes = new HashMap<String, Object[]>();
                }
                getChanges().put(method.getName(), args);
            }

            return method.invoke(getEntity(), args);
        }

        @Override
        public boolean hasChanges() {
            if (getNewEdition() == null) {
                return false;
            }

            return !getChanges().isEmpty();
        }

        @Override
        public EditionSetEntry getEditionSetEntry() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String toString() {
            return "CurrentEntityProxy[" + getEntity() + "]";
        }

    }

}
