package main.java.com.jobBoard;

import java.io.Serializable;
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
//import javax.persistence.Query;
import javax.persistence.Query;


public class SeekerDao {
    
    protected EntityManagerFactory entityManagerFactory;
    
    public SeekerDao() {
        entityManagerFactory = Persistence.createEntityManagerFactory("jobBoard");
    }
    
    public Object create(Seeker seeker) {
        EntityManager manager = entityManagerFactory.createEntityManager();
        EntityTransaction tx = manager.getTransaction();
        Seeker newSeeker = null;
        Seeker existSeeker = null;
        
        
        try {
            existSeeker = manager.find(Seeker.class, seeker.getEmail());
            if (null != existSeeker) { return new Response("404", "The email has been registered."); }

            
            tx.begin();
            newSeeker = manager.merge(seeker);
            tx.commit();
            newSeeker.fetchLazy();
            return newSeeker;
        } catch (RuntimeException e) {
            tx.rollback();
            
            return new Response("400", "Runtime Error in store");
        } finally {
            manager.close();
        }
        
        
        
    }
    
    public Object update(Seeker seeker) {
        EntityManager manager = entityManagerFactory.createEntityManager();
        EntityTransaction tx = manager.getTransaction();
        Seeker newSeeker = null;
        Seeker existSeeker = null;
        
        
        try {
            
            existSeeker = manager.find(Seeker.class, seeker.getEmail());
            if (null == existSeeker) { return new Response("404", "The job seeker does not exist."); }
            
            existSeeker.update(seeker);
            
            tx.begin();
            newSeeker = manager.merge(existSeeker);
            tx.commit();
            newSeeker.fetchLazy();
            return newSeeker;
         } catch (RuntimeException e) {
            tx.rollback();
            return new Response("400", "Runtime Error in store");
         } finally {
            manager.close();
         }
        
    }
    
    public Object markOrUnmarkInterest(String email, String jobId, boolean isInterested) {
        EntityManager manager = entityManagerFactory.createEntityManager();
        EntityTransaction tx = manager.getTransaction();
        Seeker seeker = null;
        Seeker newSeeker = null;
        Job  job = null;
        Set<Job> interestJobs = null;
        
        try {
            
            seeker = manager.find(Seeker.class, email);
            if (null == seeker) { return new Response("404", "The job seeker does not exist."); }
            
            job = manager.find(Job.class, jobId);
            if (null == job) { return new Response("404", "The job does not exist."); }
            
            interestJobs = seeker.getInterestJobs();
            if (isInterested) 
            {
                interestJobs.add(job);
            } else {
                interestJobs.remove(job);
            }
            
            
            tx.begin();
            newSeeker = manager.merge(seeker);
            tx.commit();
            newSeeker.fetchLazy();
            return newSeeker;
         } catch (RuntimeException e) {
            tx.rollback();
            return new Response("400", "Runtime Error in store");
         } finally {
            manager.close();
         }
        
    }
    
    public Object getInterestJobs(String email) {
        EntityManager manager = entityManagerFactory.createEntityManager();
        Seeker existSeeker = null;
        
        try {
            
            existSeeker = manager.find(Seeker.class, email);
            if (null == existSeeker) { return new Response("404", "The job seeker does not exist."); }
            
            Set<Job> jobs = existSeeker.getInterestJobs();
            for(Job j:jobs) j.fetchLazy();
            return jobs;
         } finally {
            manager.close();
         }
        
        
    }
    
    public Object getApplications(String email) {
        EntityManager manager = entityManagerFactory.createEntityManager();
        Seeker existSeeker = null;
        
        try {
            
            existSeeker = manager.find(Seeker.class, email);
            if (null == existSeeker) { return new Response("404", "The job seeker does not exist."); }
            
            Set<Application> apps = existSeeker.getApplications();
            for(Application app:apps) {
            	Job tmp = app.getJob();
            	tmp.setApplications(null);
            	tmp.setInterestSeekers(null);
            	tmp.getCompany().fetchLazy();
            	app.fetchLazy();
            	app.setJob(tmp);
            }
            System.out.println(apps);
            return apps;
         } finally {
            manager.close();
         }
        
        
    }
    
    public Object apply(String email, String jobId, Application application) {
        EntityManager manager = entityManagerFactory.createEntityManager();
        EntityTransaction tx = manager.getTransaction();
        Seeker seeker = null;
        Job  job = null;
        Set<Job> interestJobs = null;
        Set<Application> applications = null;
        System.out.println("@@@@@");
        
        try {
            
            seeker = manager.find(Seeker.class, email);
            if (null == seeker) { return new Response("404", "The job seeker does not exist."); }
            
            if (!seeker.checkInfo()) { return new Response("404", "Please update your information at first"); }
            
            job = manager.find(Job.class, jobId);
            if (null == job) { return new Response("404", "The job does not exist."); }
            
            if (!seeker.canApply(jobId)) { return new Response("404", "Cannot apply for the same application again"); }
            
            if (seeker.isAppExd()) { return new Response("404", "A job seeker cannot have more than 5 pending applications."); }
            
            application.setSeeker(seeker);
            application.setJob(job);
            application.setStatus("Pending");
            
            System.out.println("22223232");
            
            tx.begin();
            manager.merge(application);
            tx.commit();
            return new Response("200", "Apply successfully.");
            
         } catch (RuntimeException e) {
            tx.rollback();
            return new Response("400", "Runtime Error in store.");
         } finally {
            manager.close();
         }
    }
    
    public Object cancel(String email, String[] appList) {
        EntityManager manager = entityManagerFactory.createEntityManager();
        EntityTransaction tx = manager.getTransaction();
        Seeker seeker = null;
        Job  job = null;
        Set<Job> interestJobs = null;
        Set<Application> applications = null;
        Iterator<Application> applicationIt = null;
        Application application = null;
        int i = 0;
        
        try {
            
            seeker = manager.find(Seeker.class, email);
            if (null == seeker) { return new Response("404", "The job seeker does not exist."); }
            
            if (!seeker.canCancel(appList)) { return new Response("404", "Only pending application can be canceled."); }

            tx.begin();
            
            for (i = 0; i < appList.length; i++) {
                application = manager.find(Application.class, appList[i]);
                application.setStatus("Cancelled");
                manager.merge(application);
            }
            tx.commit();
            return new Response("200", "Cancel successfully.");
         } catch (RuntimeException e) {
            tx.rollback();
            return new Response("400", "Runtime Error in store");
         } finally {
            manager.close();
         }
    }
    
    public Object reject(String email, String[] appList) {
        EntityManager manager = entityManagerFactory.createEntityManager();
        EntityTransaction tx = manager.getTransaction();
        Seeker seeker = null;
        Job  job = null;
        Set<Job> interestJobs = null;
        Set<Application> applications = null;
        Iterator<Application> applicationIt = null;
        Application application = null;
        int i = 0;
        
        try {
            
            seeker = manager.find(Seeker.class, email);
            if (null == seeker) { return new Response("404", "The job seeker does not exist."); }
            
            if (!seeker.canReject(appList)) { return new Response("404", "Only pending application can be canceled."); }

            tx.begin();
            
            for (i = 0; i < appList.length; i++) {
                application = manager.find(Application.class, appList[i]);
                application.setStatus("OfferRejcted");
                manager.merge(application);
            }
            tx.commit();
            return new Response("200", "Reject successfully.");
         } catch (RuntimeException e) {
            tx.rollback();
            return new Response("400", "Runtime Error in store");
         } finally {
            manager.close();
         }
    }
    
    public Object accept(String email, String[] appList) {
        EntityManager manager = entityManagerFactory.createEntityManager();
        EntityTransaction tx = manager.getTransaction();
        Seeker seeker = null;
        Job  job = null;
        Set<Job> interestJobs = null;
        Set<Application> applications = null;
        Iterator<Application> applicationIt = null;
        Application application = null;
        int i = 0;
        
        try {
            
            seeker = manager.find(Seeker.class, email);
            if (null == seeker) { return new Response("404", "The job seeker does not exist."); }
            
            if (!seeker.canAccept(appList)) { return new Response("404", "Only pending application can be canceled."); }

            tx.begin();
            
            for (i = 0; i < appList.length; i++) {
                application = manager.find(Application.class, appList[i]);
                application.setStatus("OfferAccepted");
                manager.merge(application);
            }
            tx.commit();
            return new Response("200", "Accept successfully.");
         } catch (RuntimeException e) {
            tx.rollback();
            return new Response("400", "Runtime Error in store");
         } finally {
            manager.close();
         }
    }
    
    public Object findById(String email) {
        EntityManager manager = entityManagerFactory.createEntityManager();
        Seeker seeker = null;
        System.out.println(email);
        try {
            seeker = manager.find(Seeker.class, email);
            if (null == seeker) { return new Response("404", "The Seeker cannot be found"); }
            seeker.fetchLazy();
            return seeker;
            
        } finally {
            manager.close();
        }
    }
    
    
    
    /*

    public Object update(Passenger passenger) {
        EntityManager manager = entityManagerFactory.createEntityManager();
        EntityTransaction tx = manager.getTransaction();
        Passenger newPassenger = null;
        Query queryPassengerByPhone = manager.createNamedQuery("queryPassengerByPhone");
        Passenger existPassenger = null;
        
        try {
            
            queryPassengerByPhone.setParameter("phone", passenger.getPhone());
            List<Passenger> passengers =  queryPassengerByPhone.getResultList();
            existPassenger = manager.find(Passenger.class, passenger.getId());
            if (null == existPassenger) { return new BadResponse("404", "The passenger id " + passenger.getId() + " does not exist."); }
            
            if ((passengers.size() > 0) && (!passenger.getId().equals(passengers.get(0).getId()))){
                return new BadResponse("400", "Phone number " + passenger.getPhone() + " has already existed.");
            }
                
            existPassenger.update(passenger);
            
            
            tx.begin();
            newPassenger = manager.merge(existPassenger);
            tx.commit();
            newPassenger.fetchLazy();
            return newPassenger;
        } catch (RuntimeException e) {
            tx.rollback();
            System.out.println(e);
            System.out.println(e.toString());
            return new BadResponse("400", "Runtime Error in store");
        } finally {
            manager.close();
        }
    }
    
    public Response delete(String id) {
        EntityManager manager = entityManagerFactory.createEntityManager();
        EntityTransaction tx = manager.getTransaction();
        Passenger passenger = null;
        Iterator<Reservation> reservationIt = null;
        Iterator<Flight> flightIt = null;
        Reservation reservation = null;
        Flight flight = null;
        Set<Flight> flights = null;
        
        try 
        {
            passenger = manager.find(Passenger.class, id);
            if (null == passenger) { return new BadResponse("404", "The passenger id " + id + " does not exist."); }   
            tx.begin();
            reservationIt = passenger.getReservations().iterator();
            while (reservationIt.hasNext()) {
                reservation = reservationIt.next();
                flightIt = reservation.getFlights().iterator();
                
                while (flightIt.hasNext()){
                    flight = flightIt.next();
                    flight.setSeatsLeft(flight.getSeatsLeft() + 1);
                    flight.removePassenger(passenger);
                    manager.merge(flight);
                }
                flights = new HashSet<Flight>();
                reservation.setFlights(flights);
                manager.merge(reservation);
                manager.remove(reservation);   
            }
            manager.remove(passenger);
            tx.commit();
            return new SuccessResponse("200", "Passenger with id " + id + " is deleted successfully");
            
        } catch (RuntimeException e) {
            tx.rollback();
            System.out.println(e.toString());
            return new BadResponse("400", "Runtime Error in store");
            
        } finally {
            manager.close();
        }
        
    }

    public Object findById(String id) {
        EntityManager manager = entityManagerFactory.createEntityManager();
        Passenger passenger = null;
        
        try {
            passenger = manager.find(Passenger.class, id);
            if (null == passenger) { return new BadResponse("404", "Sorry, the requested passenger with id " + id + " does not exist"); }
            passenger.fetchLazy();
            return passenger;
            
        } finally {
            manager.close();
        }
    } */

}
