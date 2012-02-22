/*******************************************************************************
 * Copyright (c) 2011-2012 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      dclarke - Bug 361016: Future Versions Examples
 ******************************************************************************/
package temporal.persistence;

import static temporal.TemporalHelper.EDITION;
import static temporal.TemporalHelper.EDITION_VIEW;
import static temporal.TemporalHelper.INTERFACE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.persistence.config.CacheIsolationType;
import org.eclipse.persistence.config.SessionCustomizer;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.DescriptorEventAdapter;
import org.eclipse.persistence.descriptors.DescriptorEventListener;
import org.eclipse.persistence.descriptors.changetracking.AttributeChangeTrackingPolicy;
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.jpa.CMP3Policy;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.mappings.ManyToOneMapping;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.mappings.VariableOneToOneMapping;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.QueryRedirector;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.queries.SQLCall;
import org.eclipse.persistence.sessions.Record;
import org.eclipse.persistence.sessions.Session;

import temporal.EditionSetEntry;
import temporal.Effectivity;
import temporal.TemporalEdition;
import temporal.TemporalEntity;
import temporal.TemporalHelper;

/**
 * Customize the persistence unit by adding edition {@link ClassDescriptor}
 * using dynamic subclasses. This additional descriptor is added to enable
 * separate edition based queries using an effective time.
 * 
 * @author dclarke
 * @since EclipseLink 2.3.1
 */
public class ConfigureTemporalDescriptors implements SessionCustomizer {

    @Override
    public void customize(Session session) throws Exception {
        DynamicClassLoader dcl = new DynamicClassLoader(session.getPlatform().getConversionManager().getLoader());
        session.getPlatform().getConversionManager().setLoader(dcl);

        // Create edition descriptor for all subclasses of TemporalEntity
        List<ClassDescriptor> editionDescriptors = new ArrayList<ClassDescriptor>();
        List<ClassDescriptor> editionViewDescriptors = new ArrayList<ClassDescriptor>();
        Map<Class<?>, ClassDescriptor> interfaceDescriptors = new HashMap<Class<?>, ClassDescriptor>();

        for (ClassDescriptor current : session.getProject().getDescriptors().values()) {
            if (!current.isDescriptorForInterface() && TemporalEntity.class.isAssignableFrom(current.getJavaClass())) {
                ClassDescriptor editionDesc = createEditionType(session, dcl, current, EDITION);
                editionDescriptors.add(editionDesc);

                ClassDescriptor editionViewDesc = createEditionType(session, dcl, current, EDITION_VIEW);
                editionViewDescriptors.add(editionViewDesc);

                configureQueries(current, editionDesc, session);
                setupInterfaceDescriptor(current, session, interfaceDescriptors);

                // Since the redirector can cause queries to run against
                // different types it is important that no expression to query
                // caching be used.
                current.getQueryManager().setExpressionQueryCacheMaxSize(0);
            }
        }

        // Fix all relationship FKs to edition
        for (ClassDescriptor desc : editionDescriptors) {
            fixEditionRelationships(desc, dcl, EDITION);
            desc.setCacheIsolation(CacheIsolationType.ISOLATED);
        }

        // Fix all relationship FKs to edition view
        for (ClassDescriptor desc : editionViewDescriptors) {
            fixEditionRelationships(desc, dcl, EDITION_VIEW);
            desc.setCacheIsolation(CacheIsolationType.ISOLATED);
        }

        session.getProject().addDescriptors(editionDescriptors, (DatabaseSessionImpl) session);
        session.getProject().addDescriptors(editionViewDescriptors, (DatabaseSessionImpl) session);
        session.getProject().getDescriptors().putAll(interfaceDescriptors);

        configureEditionSetEntryVariableMapping(session, editionDescriptors);
    }

    /**
     * Create new dynamic edition subclass and clone the original descriptor to
     * have all of the mappings of its parent.
     * 
     * @return edition {@link ClassDescriptor}
     */
    private ClassDescriptor createEditionType(Session session, DynamicClassLoader dcl, ClassDescriptor source, String suffix) {
        String className = source.getJavaClassName() + suffix;
        Class<?> cls = dcl.createDynamicClass(className, new EditionClassWriter(source.getJavaClass()));

        ClassDescriptor desc = (ClassDescriptor) source.clone();
        desc.setJavaClassName(className);
        desc.setJavaClass(cls);
        desc.setAlias(source.getAlias() + suffix);
        desc.setCMPPolicy(new CMP3Policy());

        // Configure cache invalidation for edition & current sharing same row
        desc.getEventManager().addEntityListenerEventListener(new CurrentCacheInvalidator());

        // Configure attribute change tracking as initialization requires
        // weaving interfaces directly on each class
        desc.setObjectChangePolicy(new AttributeChangeTrackingPolicy());

        return desc;
    }

    /**
     * Adjust the relationship mappings on the edition descriptors so that they
     * reference other edition descriptors. This can only be done after all
     * edition dynamic classes have been created and are available in the
     * {@link DynamicClassLoader}.
     * <p>
     * All edition relationships must also be modified so that their FK
     * references are to the CID combined with the start-end date range applied
     * by the additional criteria. This method assumes that only simple FK
     * structures are in use.
     */
    private void fixEditionRelationships(ClassDescriptor descriptor, DynamicClassLoader dcl, String suffix) throws ClassNotFoundException {

        // Point all reference mappings to TemporalEntity to edition classes
        for (DatabaseMapping mapping : descriptor.getMappings()) {
            if (mapping.isForeignReferenceMapping() && TemporalHelper.isTemporalEntity(((ForeignReferenceMapping) mapping).getReferenceClass())) {
                ForeignReferenceMapping frMapping = (ForeignReferenceMapping) mapping;
                frMapping.setReferenceClassName(frMapping.getReferenceClassName() + suffix);
                frMapping.setReferenceClass(dcl.loadClass(frMapping.getReferenceClassName()));

                // Relationship or edition descriptor must not be cached so that
                // the EntityManager/ClientSession/UOW properties are available
                // to the relationship query
                frMapping.setIsCacheable(false);

                if (mapping.getAttributeName().equals("continuity")) {
                    ManyToOneMapping contMapping = (ManyToOneMapping) mapping;
                    // Use a native query to avoid additional criteria
                    // Causes additional SQL calls
                    contMapping.setSelectionSQLString("SELECT * FROM " + descriptor.getTableName() + " WHERE OID = #CID");
                    contMapping.getSelectionQuery().setRedirector(new ContinuityMappingQueryRedirector());
                    ((ReadObjectQuery) contMapping.getSelectionQuery()).setReferenceClass(frMapping.getReferenceClass());
                } else if (frMapping.isOneToOneMapping()) {
                    fixFKNames(((OneToOneMapping) frMapping).getSourceToTargetKeyFields());
                } else if (frMapping.isOneToManyMapping()) {
                    OneToManyMapping otMMapping = (OneToManyMapping) frMapping;
                    fixFKNames(otMMapping.getTargetForeignKeysToSourceKeys());
                    List<DatabaseField> sourceFields = (List<DatabaseField>) otMMapping.getSourceKeyFields().clone();
                    otMMapping.getSourceKeyFields().clear();
                    List<DatabaseField> targetFields = (List<DatabaseField>) otMMapping.getTargetForeignKeyFields().clone();
                    otMMapping.getTargetForeignKeyFields().clear();
                    
                    for (int i  = 0; i < sourceFields.size(); i++) {
                        DatabaseField sourceField = sourceFields.get(0).clone();
                        DatabaseField targetField = targetFields.get(0).clone();
                        if (sourceField.getName().equals("OID")) {
                            sourceField.setName("CID");
                        }
                        otMMapping.addTargetForeignKeyFieldName(sourceField.getQualifiedName(), targetField.getQualifiedName());
                    }
                } else {
                    throw new RuntimeException("Unsupported mapping: " + frMapping);
                }
            }
        }
    }

    /**
     * Replace the FK field references to OID to use the continuity id (CID) for
     * edition relationships. This works with the temporal range to get the
     * effective instances.
     */
    private void fixFKNames(Map<DatabaseField, DatabaseField> keys) {
        for (Map.Entry<DatabaseField, DatabaseField> entry : keys.entrySet()) {
            if (entry.getValue().getName().equals("OID")) {
                entry.getValue().setName("CID");
            }
        }

    }

    /**
     * Configure queries for current and edition descriptors.
     */
    private void configureQueries(ClassDescriptor currentDesc, ClassDescriptor editionDesc, Session session) {
        // Add query redirector to handle edition query redirection to edition
        // class when effectivity time is provided.
        TemporalQueryRedirector redirector = new TemporalQueryRedirector(currentDesc, editionDesc);
        currentDesc.setDefaultReadAllQueryRedirector(redirector);
        currentDesc.setDefaultReadObjectQueryRedirector(redirector);
        currentDesc.setDefaultReportQueryRedirector(redirector);

        // EDITION: Add additional criteria and query keys
        editionDesc.getDescriptorQueryManager().setAdditionalCriteria(":EFF_TS >= this.effectivity.start AND :EFF_TS < this.effectivity.end");
        editionDesc.addDirectQueryKey("id", "CID");
        editionDesc.addDirectQueryKey("cid", "CID");

        // CURRENT: Add additional criteria to current descriptor
        currentDesc.getQueryManager().setAdditionalCriteria("this.effectivity.start = " + Effectivity.BOT);
        currentDesc.addDirectQueryKey("cid", "CID");
        currentDesc.addDirectQueryKey("id", "OID");

        // Add Named Queries for editions
        ReadAllQuery raq = new ReadAllQuery(editionDesc.getJavaClass());
        raq.setName(editionDesc.getAlias() + ".find");
        ExpressionBuilder eb = raq.getExpressionBuilder();
        raq.setSelectionCriteria(eb.get("id").equal(eb.getParameter("ID")));
        raq.addArgument("ID", int.class);
        session.addQuery(raq.getName(), raq);

        raq = new ReadAllQuery(editionDesc.getJavaClass());
        raq.setName(editionDesc.getAlias() + ".all");
        SQLCall call = new SQLCall("SELECT * From TPERSON WHERE CID = #CID ORDER BY START_TS");
        call.setHasCustomSQLArguments(true);
        call.setCustomSQLArgumentType("CID", int.class);
        raq.setCall(call);
        raq.addArgument("CID", int.class);
        session.addQuery(raq.getName(), raq);

    }

    /**
     * TODO
     * 
     * @param interfaceDescriptors
     */
    private void setupInterfaceDescriptor(ClassDescriptor currentDesc, Session session, Map<Class<?>, ClassDescriptor> interfaceDescriptors) {
        Class<?>[] interfaces = currentDesc.getJavaClass().getInterfaces();
        if (interfaces.length == 0) {
            throw new IllegalStateException("TemporalEntity types must implement an interface");
        }

        Class<?> currentInterface = interfaces[0];
        currentDesc.setProperty(INTERFACE, currentInterface);
        interfaceDescriptors.put(currentInterface, currentDesc);
    }

    /**
     * Configure the {@link VariableOneToOneMapping} from
     * {@link EditionSetEntry} to the {@link TemporalEdition} populating all of
     * the edition descriptor types.
     */
    @SuppressWarnings("unchecked")
    private void configureEditionSetEntryVariableMapping(Session session, List<ClassDescriptor> editionDescriptors) {
        ClassDescriptor editionSetEntryDesc = session.getClassDescriptor(EditionSetEntry.class);
        VariableOneToOneMapping mapping = (VariableOneToOneMapping) editionSetEntryDesc.getMappingForAttributeName("edition");

        mapping.setIsCacheable(false);

        for (ClassDescriptor editionDesc : editionDescriptors) {
            String shortAlias = editionDesc.getAlias().substring(0, editionDesc.getAlias().indexOf(TemporalHelper.EDITION));
            mapping.addClassIndicator(editionDesc.getJavaClass(), shortAlias);
        }

        for (Entry<?, String> entry : ((Map<?, String>) mapping.getSourceToTargetQueryKeyNames()).entrySet()) {
            entry.setValue("oid");
        }
    }

    /**
     * This redirector is used on the edition descriptor's M:1 continuity
     * mapping to check for cache hits. The query on the mapping has been
     * altered to use native SQL to avoid the descriptor's additional criteria
     * so without this there will never be a cache hit.
     */
    @SuppressWarnings("serial")
    class ContinuityMappingQueryRedirector implements QueryRedirector {

        @Override
        public Object invokeQuery(DatabaseQuery query, Record arguments, Session session) {
            TemporalEntity<?> cachedEntity = (TemporalEntity<?>) session.getIdentityMapAccessor().getFromIdentityMap(arguments, query.getReferenceClass());
            if (cachedEntity != null && cachedEntity.getEffectivity().isCurrent()) {
                return cachedEntity;
            }

            query.setDoNotRedirect(true);
            return ((AbstractSession) session).executeQuery(query, (AbstractRecord) arguments);
        }

    }

    /**
     * Invalidate the cache for any current entities when a change is written
     * for an edition entity with a start effectivity time of
     * {@value Effectivity#BOT}.
     * <p>
     * A {@link DescriptorEventListener} approach is used which means the
     * invalidation happens after the write but before the transaction commits.
     * This could result in cache invalidations for transactions that do not
     * commit.
     */
    class CurrentCacheInvalidator extends DescriptorEventAdapter {

        @Override
        public void postWrite(DescriptorEvent event) {
            TemporalEntity<?> entity = (TemporalEntity<?>) event.getSource();
            if (entity.getEffectivity().getStart() == Effectivity.BOT) {
                AbstractSession session = event.getSession().getRootSession(event.getQuery());
                Object pk = event.getDescriptor().getObjectBuilder().extractPrimaryKeyFromObject(entity.getContinuity(), session);
                ClassDescriptor currentDesc = TemporalHelper.getCurrentDescriptor(session, entity.getClass());
                session.getIdentityMapAccessor().invalidateObject(pk, currentDesc.getJavaClass());
            }
        }

    }

    // Added to avoid Eclipse WTP-Dali Bug 361196
    public ConfigureTemporalDescriptors() {
    }

}
