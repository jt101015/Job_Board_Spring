package main.java.com.jobBoard;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.NamedQuery;
import javax.persistence.Persistence;

import javax.persistence.Query;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.query.dsl.BooleanJunction;
import org.hibernate.search.query.dsl.QueryBuilder;




public class JobDao {
    
    protected EntityManagerFactory entityManagerFactory;
    
    public JobDao() {
        entityManagerFactory = Persistence.createEntityManagerFactory("jobBoard");
    }
    
    @SuppressWarnings("unchecked")
	public Object search(String[] text, String[] companyName, String[] location, Integer minSalary, Integer maxSalary) {
        EntityManager manager = entityManagerFactory.createEntityManager();
        EntityTransaction tx = manager.getTransaction();
        FullTextEntityManager ftem = org.hibernate.search.jpa.Search.getFullTextEntityManager(manager);
        String searchTxt = "";
        if(text!=null && text.length>=0){
        	for(String t:text){
        		if(searchTxt=="") searchTxt = searchTxt + t;
            	else searchTxt = searchTxt + " " + t;
        	}
        }
        String searchCom = "";
        if(companyName!=null && companyName.length>0){
        	for(String c:companyName){
        		if(searchCom=="") searchCom = searchCom + c;
        		else searchCom = searchCom + " " + c;
        	}
        }
        String searchLoc = "";
        if(location!=null && location.length>0){
            for(String l:location){
            	if(searchLoc=="") searchLoc = searchLoc + l;
            	else searchLoc = searchLoc + " " + l;
            }
        }
        try {
        	tx.begin();
        	ftem.createIndexer().startAndWait();
        	QueryBuilder qb = ftem.getSearchFactory().buildQueryBuilder().forEntity(Job.class).get();
        	@SuppressWarnings("rawtypes")
			BooleanJunction mj = qb.bool().should(qb.all().createQuery());
        	if(searchTxt!="") mj = mj.must(qb.keyword().onFields("responsibilities", "description", "title").matching(searchTxt).createQuery());
        	if(searchLoc!="") mj = mj.must(qb.keyword().onField("location").matching(searchLoc).createQuery());
        	if(minSalary!=null) mj = mj.must(qb.range().onField("salary").above(minSalary).createQuery());
        	if(maxSalary!=null) mj = mj.must(qb.range().onField("salary").below(maxSalary).createQuery());
        	if(searchCom!="")mj = mj.must(qb.keyword().onField("company.Companyname").matching(searchCom).createQuery());
        	org.apache.lucene.search.Query luceneQuery = mj.createQuery();
        	Query fullTextQuery = ftem.createFullTextQuery(luceneQuery, Job.class);
        	List<Job> queryJob = (List<Job>)fullTextQuery.getResultList();
        	tx.commit();
        	if (queryJob.size() == 0) { return new Response("404", "Sorry, the search result does not exist"); }
        	for (Job job:queryJob) job.fetchLazy();
        	return new HashSet<Job>(queryJob);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return new Response("404", "error at search");
        }finally {
            manager.close();
        }
        
    }
    
    
    public Object getJobsByCompany(String email) {
        
        EntityManager manager = entityManagerFactory.createEntityManager();
        Company company = null;
        String[] companyName = {"1"};
  
        try {
            company = manager.find(Company.class, email);
            if (null == company) { return new Response("404", "The Company cannot be found"); }
            companyName[0] = company.getCompanyname();
            System.out.println(companyName[0]);
            
            return search(null, companyName, null, null, null);
            
        } finally {
            manager.close();
        }
        
    };
    
    public Object findById(String id) {
        EntityManager manager = entityManagerFactory.createEntityManager();
        Job job = null;
        System.out.println(id);
        try {
            job = manager.find(Job.class, id);
            if (null == job) { return new Response("404", "The Job cannot be found"); }
            job.fetchLazy();
            return job;
            
        } finally {
            manager.close();
        }
    }

   	public Object add(String company_email, Job job) {
           EntityManager manager = entityManagerFactory.createEntityManager();
           EntityTransaction tx = manager.getTransaction();
           
           Company tmpcmp = manager.find(Company.class, company_email);
           if (null == tmpcmp) { return new Response("404", "The company has not been registered."); }
           job.setCompany(tmpcmp);
           try {
        	   tx.begin();
        	   Job tmpjob = manager.merge(job);
        	   tx.commit();
        	   tmpjob.fetchLazy();
               return tmpjob;
           } catch (RuntimeException e) {
               tx.rollback();
               return new Response("400", "Runtime Error in store");
           } finally {
               manager.close();
           }
       }
	
       public Object update(Job job) {
	        EntityManager manager = entityManagerFactory.createEntityManager();
	        EntityTransaction tx = manager.getTransaction();
	        Job newJob = null;
	        Job existJob = null;
	        
	        try {
	            
	            existJob = manager.find(Job.class, job.getId());
	            if (null == existJob) { return new Response("404", "The job seeker does not exist."); }
	            
	            existJob.update(job);
	            
	            tx.begin();
	            newJob = manager.merge(existJob);
	            tx.commit();
	            newJob.fetchLazy();
	            return newJob;
	         } catch (RuntimeException e) {
	            tx.rollback();
	            return new Response("400", "Runtime Error in store");
	         } finally {
	            manager.close();
	         }
	        
	    }
    
    public Object filled(String jobId) {
        EntityManager manager = entityManagerFactory.createEntityManager();
	    EntityTransaction tx = manager.getTransaction();
        Job job = null;
        Job newJob = null;
        
        
        try {
	            
            job = manager.find(Job.class, jobId);
            if (null == job) { return new Response("404", "The job does not exist."); }

            if (!job.canFilled()) { return new Response("404", "The job status cannot be filled, because there is not any offerAccepted status application."); }
            
            job.cancelApplications();
            job.setStatus("Filled");
            tx.begin();
	        newJob = manager.merge(job);
	        tx.commit();
            return new Response("200", "Update successfully.");
            
         } catch (RuntimeException e) {
            tx.rollback();
            return new Response("400", "Runtime Error in store");
         } finally {
            manager.close();
         }
    }
    
    public Object cancel(String jobId) {
        EntityManager manager = entityManagerFactory.createEntityManager();
	    EntityTransaction tx = manager.getTransaction();
        Job job = null;
        Job newJob = null;
        
        try {
	            
            job = manager.find(Job.class, jobId);
            if (null == job) { return new Response("404", "The job does not exist."); }

            if (!job.canCancel()) { return new Response("404", "The job status cannot be cancelled, because there is offerAccepted status application."); }
            
            job.cancelApplications();
            job.setStatus("Cancelled");
            
            tx.begin();
	        newJob = manager.merge(job);
	        tx.commit();
            return new Response("200", "Update successfully.");
            
         } catch (RuntimeException e) {
            tx.rollback();
            return new Response("400", "Runtime Error in store");
         } finally {
            manager.close();
         }
    }
    
      public Object getEmails(String jobId) {
          
            EntityManager manager = entityManagerFactory.createEntityManager();
	        EntityTransaction tx = manager.getTransaction();
	        Job job = null;
            Set<Application> applications = null;
            Iterator<Application> applicationIt = null;
            Application application = null;
            Set<String> emails = new HashSet<String>();
	        
	        try {
	            
	            job = manager.find(Job.class, jobId);
	            if (null == job) { return new Response("404", "The job does not exist."); }
	            
	            applications = job.getApplications();
                applicationIt = applications.iterator();
                
                while(applicationIt.hasNext()){ 
            
                    application = applicationIt.next();
                    emails.add(application.getSeeker().getEmail());

                }
	            return emails;
	         } catch (RuntimeException e) {
	            tx.rollback();
	            return new Response("400", "Runtime Error in store");
	         } finally {
	            manager.close();
	         }
          
      }
    
}
