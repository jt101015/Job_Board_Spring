package main.java.com.jobBoard;

import java.util.Iterator;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class CompanyDao {
	
	 protected EntityManagerFactory entityManagerFactory;
	    
	    public CompanyDao() {
	        entityManagerFactory = Persistence.createEntityManagerFactory("jobBoard");
	    }
	
    public Object create(Company company) {
        EntityManager manager = entityManagerFactory.createEntityManager();
        EntityTransaction tx = manager.getTransaction();
        Company newCompany = null;
        Company existCompany = null;

        try {
            existCompany = manager.find(Company.class, company.getEmail());
            if (null != existCompany) { return new Response("404", "The email has been registered."); }

            tx.begin();
            newCompany = manager.merge(company);
            tx.commit();
            newCompany.fetchLazy();
            return newCompany;
        } catch (RuntimeException e) {
            tx.rollback();

            return new Response("400", "Runtime Error in store");
        } finally {
            manager.close();
        }	        

    }


    public Object update(Company company) {
        EntityManager manager = entityManagerFactory.createEntityManager();
        EntityTransaction tx = manager.getTransaction();
        Company newCompany = null;
        Company existCompany = null;


        try {

            existCompany = manager.find(Company.class, company.getEmail());
            if (null == existCompany) { return new Response("404", "The job seeker does not exist."); }

            existCompany.update(company);

            tx.begin();
            newCompany = manager.merge(existCompany);
            tx.commit();
            newCompany.fetchLazy();
            return newCompany;
         } catch (RuntimeException e) {
            tx.rollback();
            return new Response("400", "Runtime Error in store");
         } finally {
            manager.close();
         }

    }
    
    public Object getApplication(String appId) {
        
        EntityManager manager = entityManagerFactory.createEntityManager();
        EntityTransaction tx = manager.getTransaction();
        Application application = null;


        try {

            application = manager.find(Application.class, appId);
            if (null == application) { return new Response("404", "The application does not exist."); }

            application.fetchLazy();

            return application;
         } catch (RuntimeException e) {
            tx.rollback();
            return new Response("400", "Runtime Error in store");
         } finally {
            manager.close();
         }
        
    }
    
    public Object updateApplicationStatus(String appId, String status) {
        
        EntityManager manager = entityManagerFactory.createEntityManager();
        EntityTransaction tx = manager.getTransaction();
        Application application = null;
        Application newApplication = null;


        try {

            application = manager.find(Application.class, appId);
            if (null == application) { return new Response("404", "The application does not exist."); }

            
            application.setStatus(status);
            
            tx.begin();
            newApplication = manager.merge(application);
            tx.commit();

            return new Response("200", "Update successfully.");
         } catch (RuntimeException e) {
            tx.rollback();
            return new Response("400", "Runtime Error in store");
         } finally {
            manager.close();
         }
    }
    



    public Object findById(String email) {
        EntityManager manager = entityManagerFactory.createEntityManager();
        Company company = null;
        try {
            company = manager.find(Company.class, email);
            if (null == company) { return new Response("404", "The Company cannot be found"); }
            company.fetchLazy();
            return company;

        } finally {
            manager.close();
        }
    }

    

}
