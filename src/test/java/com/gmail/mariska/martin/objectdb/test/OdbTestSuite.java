package com.gmail.mariska.martin.objectdb.test;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.StoredProcedureQuery;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.metamodel.Metamodel;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.gmail.mariska.martin.mtginventory.db.model.User;


public abstract class OdbTestSuite {
    private static final String ALL_MODEL_PACKAGES = User.class.getPackage().getName() + ".*";
    private static EntityManagerFactory emf;

    @BeforeClass
    public static void setUp() {
        com.objectdb.Enhancer.enhance(ALL_MODEL_PACKAGES);
        emf = Persistence.createEntityManagerFactory("objectdb:testOdb.tmp;drop"); // ;drop
        System.out.println("setting up odb TestSuite");
    }

    @AfterClass
    public static void tearDown() {
        emf.close();
        System.out.println("tearing down odb TestSuite");
    }

    public static EntityManager getEM() {
        return emf.createEntityManager();
    }

    public static ClosableEM getClosableEM() {
        return new ClosableEM(emf.createEntityManager());
    }

    /**
     * Auto closable EM
     * @author MAR
     *
     */
    public static class ClosableEM implements EntityManager, AutoCloseable{

        private EntityManager decorated;

        @Override
        public void clear() {
            decorated.clear();
        }

        @Override
        public void close() {
            decorated.close();
        }

        @Override
        public boolean contains(Object arg0) {
            return decorated.contains(arg0);
        }

        @Override
        public <T> EntityGraph<T> createEntityGraph(Class<T> arg0) {
            return decorated.createEntityGraph(arg0);
        }

        @Override
        public EntityGraph<?> createEntityGraph(String arg0) {
            return decorated.createEntityGraph(arg0);
        }

        @Override
        public <T> TypedQuery<T> createNamedQuery(String arg0, Class<T> arg1) {
            return decorated.createNamedQuery(arg0, arg1);
        }

        @Override
        public Query createNamedQuery(String arg0) {
            return decorated.createNamedQuery(arg0);
        }

        @Override
        public StoredProcedureQuery createNamedStoredProcedureQuery(String arg0) {
            return decorated.createNamedStoredProcedureQuery(arg0);
        }

        @Override
        public Query createNativeQuery(String arg0, Class arg1) {
            return decorated.createNativeQuery(arg0, arg1);
        }

        @Override
        public Query createNativeQuery(String arg0, String arg1) {
            return decorated.createNativeQuery(arg0, arg1);
        }

        @Override
        public Query createNativeQuery(String arg0) {
            return decorated.createNativeQuery(arg0);
        }

        @Override
        public Query createQuery(CriteriaDelete arg0) {
            return decorated.createQuery(arg0);
        }

        @Override
        public <T> TypedQuery<T> createQuery(CriteriaQuery<T> arg0) {
            return decorated.createQuery(arg0);
        }

        @Override
        public Query createQuery(CriteriaUpdate arg0) {
            return decorated.createQuery(arg0);
        }

        @Override
        public <T> TypedQuery<T> createQuery(String arg0, Class<T> arg1) {
            return decorated.createQuery(arg0, arg1);
        }

        @Override
        public Query createQuery(String arg0) {
            return decorated.createQuery(arg0);
        }

        @Override
        public StoredProcedureQuery createStoredProcedureQuery(String arg0, Class... arg1) {
            return decorated.createStoredProcedureQuery(arg0, arg1);
        }

        @Override
        public StoredProcedureQuery createStoredProcedureQuery(String arg0, String... arg1) {
            return decorated.createStoredProcedureQuery(arg0, arg1);
        }

        @Override
        public StoredProcedureQuery createStoredProcedureQuery(String arg0) {
            return decorated.createStoredProcedureQuery(arg0);
        }

        @Override
        public void detach(Object arg0) {
            decorated.detach(arg0);
        }

        @Override
        public <T> T find(Class<T> arg0, Object arg1, LockModeType arg2, Map<String, Object> arg3) {
            return decorated.find(arg0, arg1, arg2, arg3);
        }

        @Override
        public <T> T find(Class<T> arg0, Object arg1, LockModeType arg2) {
            return decorated.find(arg0, arg1, arg2);
        }

        @Override
        public <T> T find(Class<T> arg0, Object arg1, Map<String, Object> arg2) {
            return decorated.find(arg0, arg1, arg2);
        }

        @Override
        public <T> T find(Class<T> arg0, Object arg1) {
            return decorated.find(arg0, arg1);
        }

        @Override
        public void flush() {
            decorated.flush();
        }

        @Override
        public CriteriaBuilder getCriteriaBuilder() {
            return decorated.getCriteriaBuilder();
        }

        @Override
        public Object getDelegate() {
            return decorated.getDelegate();
        }

        @Override
        public EntityGraph<?> getEntityGraph(String arg0) {
            return decorated.getEntityGraph(arg0);
        }

        @Override
        public <T> List<EntityGraph<? super T>> getEntityGraphs(Class<T> arg0) {
            return decorated.getEntityGraphs(arg0);
        }

        @Override
        public EntityManagerFactory getEntityManagerFactory() {
            return decorated.getEntityManagerFactory();
        }

        @Override
        public FlushModeType getFlushMode() {
            return decorated.getFlushMode();
        }

        @Override
        public LockModeType getLockMode(Object arg0) {
            return decorated.getLockMode(arg0);
        }

        @Override
        public Metamodel getMetamodel() {
            return decorated.getMetamodel();
        }

        @Override
        public Map<String, Object> getProperties() {
            return decorated.getProperties();
        }

        @Override
        public <T> T getReference(Class<T> arg0, Object arg1) {
            return decorated.getReference(arg0, arg1);
        }

        @Override
        public EntityTransaction getTransaction() {
            return decorated.getTransaction();
        }

        @Override
        public boolean isJoinedToTransaction() {
            return decorated.isJoinedToTransaction();
        }

        @Override
        public boolean isOpen() {
            return decorated.isOpen();
        }

        @Override
        public void joinTransaction() {
            decorated.joinTransaction();
        }

        @Override
        public void lock(Object arg0, LockModeType arg1, Map<String, Object> arg2) {
            decorated.lock(arg0, arg1, arg2);
        }

        @Override
        public void lock(Object arg0, LockModeType arg1) {
            decorated.lock(arg0, arg1);
        }

        @Override
        public <T> T merge(T arg0) {
            return decorated.merge(arg0);
        }

        @Override
        public void persist(Object arg0) {
            decorated.persist(arg0);
        }

        @Override
        public void refresh(Object arg0, LockModeType arg1, Map<String, Object> arg2) {
            decorated.refresh(arg0, arg1, arg2);
        }

        @Override
        public void refresh(Object arg0, LockModeType arg1) {
            decorated.refresh(arg0, arg1);
        }

        @Override
        public void refresh(Object arg0, Map<String, Object> arg1) {
            decorated.refresh(arg0, arg1);
        }

        @Override
        public void refresh(Object arg0) {
            decorated.refresh(arg0);
        }

        @Override
        public void remove(Object arg0) {
            decorated.remove(arg0);
        }

        @Override
        public void setFlushMode(FlushModeType arg0) {
            decorated.setFlushMode(arg0);
        }

        @Override
        public void setProperty(String arg0, Object arg1) {
            decorated.setProperty(arg0, arg1);
        }

        @Override
        public <T> T unwrap(Class<T> arg0) {
            return decorated.unwrap(arg0);
        }

        public ClosableEM(EntityManager decorated) {
            this.decorated = decorated;
        }

    }

}
