package main.java.com.jobBoard;


 
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.FieldBridge;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.Store;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.FetchType;

@XmlRootElement(name = "Job")
@JsonRootName(value = "Job")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "Job")
@Indexed
public class Job {

    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    @Column(name = "id", length = 45)
    private String id = null;
    
    @Column(name = "title", length = 45, nullable = false)
    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    private String title = null;
    
    @Column(name = "description", length = 1000, nullable = false)
    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    private String description = null;
    
    @Column(name = "responsibilities", length = 1000, nullable = false)
    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    private String responsibilities = null;
    
    @Column(name = "location", length = 45, nullable = false)
    @Field
    private String location = null;
    
    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    @Column(name = "salary", nullable = false)
    private int salary = 0;
    
    @Column(name = "status", nullable = false)
    private String status = "Open";
    
    @OneToMany( targetEntity=Application.class, mappedBy = "job", fetch = FetchType.LAZY)
    private Set<Application> applications = new HashSet<Application>();
    
    @JsonIgnore
    @ManyToMany( targetEntity=Seeker.class, mappedBy = "interestJobs", fetch = FetchType.LAZY )
    private Set<Seeker> interestSeekers = new HashSet<Seeker>();
    
    @ManyToOne( targetEntity=Company.class, fetch = FetchType.EAGER, cascade=CascadeType.REFRESH)
    //@Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    //@FieldBridge(impl = CompanyFieldBridge.class)
    @IndexedEmbedded
    private Company company = null;
    

    public Job() { }
    
    public void setId(String id) { this.id = id; }
    public String getId() { return this.id; }
    
    public void setTitle(String title) { this.title = title; }
    public String getTitle() { return this.title; }
    
    public void setDescription(String description) { this.description = description; }
    public String getDescription() { return this.description; }
    
    public void setResponsibilities(String responsibilities) { this.responsibilities = responsibilities; }
    public String getResponsibilities() { return this.responsibilities; }
    
    public void setLocation(String location) { this.location = location; }
    public String getLocation() { return this.location; }
    
    public void setSalary(int salary) { this.salary = salary; }
    public int getSalary() { return this.salary; }
    
    public void setApplications(Set<Application> applications) { this.applications = applications; }
    public Set<Application> getApplications() { return this.applications; }
    
    public void setInterestSeekers(Set<Seeker> interestSeekers) { this.interestSeekers = interestSeekers; }
    public Set<Seeker> getInterestSeekers() { return this.interestSeekers; }
    
    public void setCompany(Company company) { this.company = company; }
    public Company getCompany() { return company;  }
    
    public void setStatus(String status) { this.status = status; }
    public String getStatus() { return this.status; }
    
    
    public boolean equals(Object other) {
        if (other instanceof Job) {
            return this.getId().equals(((Job)other).getId());
        } else {
            return false;
        }
    }
    
    public void fetchLazy() {
    	Company company = this.getCompany();
        Application application = null;
        Iterator<Application> applicationIt = this.getApplications().iterator();;

        company.setJobs(null);
        
        while(applicationIt.hasNext()){ 
            application = applicationIt.next(); 
            application.fetchLazy();
        }
        
        System.out.println("job fetchlazy");
        return;
    }
    
    public boolean canFilled() {
        
        Application application = null;
        Iterator<Application> applicationIt = this.getApplications().iterator();;

        while(applicationIt.hasNext()){ 

            application = applicationIt.next();
            if (application.isOfferAccepted()) { return true; }
        }
        
        return false;
    }
    
    public boolean canCancel() { return !canFilled(); }
    
    public void cancelApplications() { 
        
        Application application = null;
        Iterator<Application> applicationIt = this.getApplications().iterator();;

        while(applicationIt.hasNext()){ 

            application = applicationIt.next();
            if (application.isPending()) { application.setStatus("Cancelled"); }
        }
        
        return;
    
    }
 
    public void update(Job anotherJob) {
           
           System.out.println("in update");
   	 	   if (null != anotherJob.getTitle())          { this.title = anotherJob.getTitle();}
           if (null != anotherJob.getDescription())         { this.description = anotherJob.getDescription();}
           if (null != anotherJob.getResponsibilities())             { this.responsibilities = anotherJob.getResponsibilities();}
           if (null != anotherJob.getLocation())    			 { this.location = anotherJob.getLocation(); }
           if (null != anotherJob.getDescription())         { this.description = anotherJob.getDescription(); }
           if (0 != anotherJob.getSalary())         { this.salary = anotherJob.getSalary();}
           if (null != anotherJob.getCompany())            { this.company = anotherJob.getCompany();}
           //if (null != anotherJob.getIsVerified())            { this.isVerified = anotherCompany.getIsVerified();}
           
       }
    
}
