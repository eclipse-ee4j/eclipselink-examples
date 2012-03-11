/*******************************************************************************
 * Copyright (c) 2010-2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *  dclarke - EclipseLink 2.3 - MySports Demo Bug 344608
 ******************************************************************************/
package example.mysports.ejb;

import static example.mysports.MySportsConfig.LEAGUE_CONTEXT;
import static example.mysports.MySportsConfig.PU_NAME;
import static org.eclipse.persistence.config.PersistenceUnitProperties.SESSION_NAME;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PreDestroy;
import javax.ejb.PostActivate;
import javax.ejb.PrePassivate;
import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.Metamodel;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.eclipse.persistence.annotations.Multitenant;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.internal.jpa.metamodel.AttributeImpl;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.mappings.AttributeAccessor;
import org.eclipse.persistence.sessions.server.Server;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.tools.schemaframework.SchemaManager;

import example.mysports.MySportsConfig;
import example.mysports.admin.League;
import example.mysports.model.Division;
import example.mysports.model.Division_;
import example.mysports.model.Divisions;
import example.mysports.model.Player;
import example.mysports.model.Player_;
import example.mysports.model.Team;
import example.mysports.model.Team_;

/**
 * MySports Domain Model Repository providing simplified persistence facade for
 * managing the application bootstrapped {@link EntityManagerFactory} that are
 * tenant aware.
 * 
 * @author dclarke
 * @since EclipseLink 2.3.0
 */
@Stateful
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class LeagueRepository implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Current league being viewed. This value MUST be configured using
     * {@link #setLeagueId(String)} before any persistence operations can be
     * performed.
     */
    private String leagueId;

    /**
     * {@link EntityManagerFactory} wrapping a tenant specific shared
     * EclipseLink session identified by its session name ('mysports-{tenant}')
     * with its tenant context property configured.
     * 
     * @see MySportsConfig#LEAGUE_CONTEXT
     */
    private transient EntityManagerFactory emf;

    /**
     * TODO
     */
    protected transient MySportsConfig config;

    /**
     * Cache the current division to simplify operations within the division.
     */
    private transient Division currentDivision;

    public String getLeagueId() {
        return this.leagueId;
    }

    public LeagueRepository() {
        this.config = new MySportsConfig();
    }

    public MySportsConfig getConfig() {
        return config;
    }

    /**
     * Initialize this repository for a specific tenant. This method MUSt be
     * called before accessing any of the other methods.
     * 
     * @param leagueId
     * @param properties
     */
    public void setLeagueId(String leagueId, Map<String, Object> properties) {
        Map<String, Object> props = properties;
        if (leagueId == null || leagueId.isEmpty()) {
            throw new IllegalArgumentException("Invalid league identifier: " + leagueId);
        }

        // Only construct an EMF if there is not one or a different leagueId
        League league = getConfig().getAdminConnector().getLeague(leagueId);

        if (this.emf == null || !leagueId.equals(getLeagueId())) {
            if (props == null) {
                props = new HashMap<String, Object>();
            }

            // Override with the league specific data source if the properties
            // does not include one.
            if (!props.containsKey(PersistenceUnitProperties.NON_JTA_DATASOURCE)) {
               props.put(PersistenceUnitProperties.NON_JTA_DATASOURCE, league.getDatasource());
            }
            createEMF(leagueId, props);
        } else {
            Long preValue = (Long) JpaHelper.getServerSession(getEMF()).getProperty("league-version");

            // Check to see if the league version has changed
            if (preValue != null && preValue.longValue() < league.getVersion()) {
                // Create a new EMF
                createEMF(leagueId, properties);
                // Refresh the new EMF's metadata
                JpaHelper.getEntityManagerFactory(getEMF()).refreshMetadata(properties);
            }
        }
        JpaHelper.getServerSession(getEMF()).setProperty("league-version", league.getVersion());
    }

    public boolean hasLeague() {
        return getLeagueId() != null;
    }

    /**
     * Retrieve all of the divisions in this league and optionally force the
     * lazily loaded teams to be returned within the {@link Division}s.
     */
    public List<Division> getDivisions() {
        EntityManager em = getEMF().createEntityManager();

        try {
            TypedQuery<Division> q = em.createNamedQuery("Division.findAll", Division.class);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Division getCurrentDivision() {
        return currentDivision;
    }

    public void setCurrentDivision(Division currentDivision) {
        this.currentDivision = currentDivision;
    }

    public <T> T find(Class<T> entityClass, int id) {
        EntityManager em = getEMF().createEntityManager();

        try {
            return em.find(entityClass, id);
        } finally {
            em.close();
        }
    }

    public Division addDivision(String name) {
        EntityManager em = getEMF().createEntityManager();

        try {
            em.getTransaction().begin();
            Division div = new Division(name);
            em.persist(div);
            em.getTransaction().commit();
            return div;
        } finally {
            em.close();
        }
    }

    public Team addTeam(String name, List<Player> players, Division division) {
        EntityManager em = getEMF().createEntityManager();
        Division div = division == null ? getCurrentDivision() : division;

        try {
            em.getTransaction().begin();

            Division managedDiv = em.merge(div);
            Team team = new Team(name);
            if (players != null) {
                for (Player player : players) {
                    team.addPlayer(player);
                }
            }
            em.persist(team);
            managedDiv.addTeam(team);

            em.getTransaction().commit();

            this.currentDivision = managedDiv;
            return team;
        } finally {
            em.close();
        }
    }

    public Team mergeTeam(Team team) {
        EntityManager em = getEMF().createEntityManager();

        try {
            em.getTransaction().begin();

            Team managedTeam = em.merge(team);

            em.getTransaction().commit();

            this.currentDivision = managedTeam.getDivision();
            return managedTeam;
        } finally {
            em.close();
        }
    }

    public Division getDivision(String name) {
        EntityManager em = getEMF().createEntityManager();

        try {
            TypedQuery<Division> q = em.createNamedQuery("Division.findByName", Division.class);
            q.setParameter("NAME", name);
            return q.getSingleResult();
        } finally {
            em.close();
        }
    }

    public Team getTeam(String division, String name) {
        EntityManager em = getEMF().createEntityManager();

        try {
            TypedQuery<Team> q = em.createNamedQuery("Team.findByDivisionAndName", Team.class);
            q.setParameter("DIV", division);
            q.setParameter("NAME", name);
            return q.getSingleResult();
        } finally {
            em.close();
        }
    }

    /**
     * Retrieve a player using its division, team name, and jersey number using
     * JPA 2.0 criteria.
     */
    public Player getPlayerByNumber(String division, String teamId, int number) {
        EntityManager em = getEMF().createEntityManager();

        try {
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery<Player> query = qb.createQuery(Player.class);
            Root<Player> player = query.from(Player.class);
            Predicate numEqual = qb.equal(player.get(Player_.number), number);
            Predicate teamEqual = qb.equal(player.get(Player_.team).get(Team_.name), teamId);
            Predicate divEqual = qb.equal(player.get(Player_.team).get(Team_.division).get(Division_.name), division);
            query.where(qb.and(numEqual, teamEqual, divEqual));
            return em.createQuery(query).getSingleResult();
        } finally {
            em.close();
        }
    }

    public void remove(Object entity) {
        EntityManager em = getEMF().createEntityManager();

        try {
            Object managedEntity = em.merge(entity);

            em.getTransaction().begin();
            em.remove(managedEntity);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    /*
     * JPA API
     */

    /**
     * @throws IllegalStateException
     *             if the
     */
    private EntityManagerFactory getEMF() {
        if (this.emf == null) {
            throw new IllegalStateException("LeagueRepository not initialized");
        }
        return this.emf;
    }

    /**
     * Determine which mapped attributes are virtual/extended attributes. Used
     * to provide dynamic access to these attributes in presentation layer.
     * 
     * @return list of extended (virtual) JPA meta-model attributes
     * @throws IllegalArgumentException
     *             if entityType is not a managed type
     */
    public List<Attribute<?, ?>> getAdditionalAttributes(Class<?> entityType) {
        Metamodel metamodel = getEMF().getMetamodel();
        ManagedType<?> type = metamodel.managedType(entityType);
        List<Attribute<?, ?>> addnAttrs = new ArrayList<Attribute<?, ?>>();

        for (Attribute<?, ?> attr : type.getAttributes()) {
            AttributeImpl<?, ?> attrImpl = (AttributeImpl<?, ?>) attr;
            AttributeAccessor accessor = attrImpl.getMapping().getAttributeAccessor();
            if (accessor.isVirtualAttributeAccessor()) {
                addnAttrs.add(attr);
            }
        }

        return addnAttrs;
    }

    /**
     * Provide access to internals of repository similar to
     * {@link EntityManager#unwrap(Class)}. Provided for access to advanced
     * capabilities of the underlying EclipseLink persistence unit as well as
     * for testing access to internals.
     */
    @SuppressWarnings("unchecked")
    public <T> T unwrap(Class<T> cls) {
        if (EntityManagerFactory.class.equals(cls)) {
            return (T) getEMF();
        } else if (cls.equals(Server.class) || cls.equals(ServerSession.class)) {
            return (T) JpaHelper.getServerSession(getEMF());
        } else if (cls.equals(SchemaManager.class)) {
            return (T) new SchemaManager(JpaHelper.getServerSession(getEMF()));
        }
        throw new RuntimeException("LeagueRepository cannot be unwrapped to: " + cls);
    }

    /**
     * Close this league repository forcing the underlying
     * {@link EntityManagerFactory} to be closed as well.
     */
    @PreDestroy
    @PrePassivate
    @Remove
    public void close() {
        if (this.emf != null && this.emf.isOpen()) {
            this.emf.close();
        }
        this.emf = null;
        this.currentDivision = null;
        this.leagueId = null;
    }

    /**
     * After the stateful session bean is activated after passivation the
     * {@link #createEMF(String, Map)}is called with the stored
     * {@link #leagueId} to populate the persistence state necessary for use.
     */
    @PostActivate
    public void initialize() {
        createEMF(getLeagueId(), null);
    }

    /**
     * Create the league (tenant) specific {@link EntityManagerFactory}. In
     * order to use the same persistence unit definition (persistence.xml) as a
     * template the {@link PersistenceUnitProperties#SESSION_NAME} property is
     * supplied to indicate that the backing shared EclipseLink session should
     * be cached based on the league identifier.
     * 
     * The {@value MySportsConfig#LEAGUE_CONTEXT} property is supplied to
     * indicate the required context property as required by the use of
     * {@link Multitenant} on the persistent entities.
     * 
     * @param leagueId
     *            league (tenant) identifier
     * @param properties
     *            additional {@link EntityManagerFactory} properties.
     */
    private void createEMF(String leagueId, Map<String, Object> properties) {
        Map<String, Object> emfProps = new HashMap<String, Object>();

        if (properties != null) {
            emfProps.putAll(properties);
        }

        emfProps.put(SESSION_NAME, getConfig().getSessionName(leagueId));
        emfProps.put(LEAGUE_CONTEXT, leagueId);
        emfProps.put(MySportsConfig.class.getName(), getConfig());

        this.emf = Persistence.createEntityManagerFactory(PU_NAME, emfProps);
        this.leagueId = leagueId;

        // Clear cached state
        setCurrentDivision(null);

        Server session = JpaHelper.getServerSession(getEMF());
        session.getSessionLog().log(SessionLog.CONFIG, "LeagueRepository[" + hashCode() + "] initialized with session named: " + session.getName());
    }

    /*
     * MOXy (JAXB) API
     */

    /**
     * Property name for caching the {@link JAXBContext} for the league within
     * its {@link EntityManagerFactory}
     */
    private static final String JAXB_CONTEXT = "jaxb-context";

    private static final String MAPPING_FILE = "META-INF/eclipselink-oxm.xml";

    /**
     * Create an EclipseLink {@link JAXBContext} which is built from the
     * {@value #MAPPING_FILE} combined with the virtual attribute extended
     * mappings returned from the call to the Admin server.
     */
    public JAXBContext getJAXBContext() {
        JAXBContext context = (JAXBContext) JpaHelper.getServerSession(getEMF()).getProperty(JAXB_CONTEXT);

        if (context == null) {
            List<String> xmlBindings = new ArrayList<String>();
            xmlBindings.add(MAPPING_FILE);
            xmlBindings.add(getConfig().getAdminConnector().getOxmURL(getLeagueId()));

            Map<String, Object> props = new HashMap<String, Object>();
            props.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, xmlBindings);

            try {
                context = JAXBContextFactory.createContext(new Class[] { Divisions.class }, props);
            } catch (JAXBException e) {
                throw new RuntimeException("JAXB Failure to create context. League: " + getLeagueId(), e);
            }

            // Cache the JAXB context in the shared session's properties
            JpaHelper.getServerSession(getEMF()).setProperty(JAXB_CONTEXT, context);
        }
        return context;
    }

}
