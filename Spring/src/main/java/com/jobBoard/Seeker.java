package main.java.com.jobBoard;


 
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.FetchType;

@JsonRootName(value = "Seeker")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "Seeker")
public class Seeker {
    
    private static final String defaultPictureUrl = "/default.png";
    private static final int maxPendingApp = 5;

    @Id
    @Column(name = "email", length = 45)
    private String email = null;
    
    @Column(name = "password", length = 45)
    private String password = null;
    
    @Column(name = "firstname", length = 45)
    private String firstname = null;
    
    @Column(name = "lastname", length = 45)
    private String lastname = null;
    
    @Column(name = "picture", length = 45, nullable = false)
    private String picture = defaultPictureUrl;
    
    @Column(name = "SelfIntroduction", length = 1000)
    private String selfIntroduction = null;
    
    @Column(name = "workExperience", length = 1000)
    private String workExperience = null; 
    
    @Column(name = "education", length = 1000)
    private String education = null; 
    
    @Column(name = "skills", length = 45)
    private String skills = null; 
    
    @JsonIgnore
    @OneToMany( targetEntity=Application.class, mappedBy = "seeker", fetch = FetchType.LAZY)
    private Set<Application> applications = new HashSet<Application>();
    
    @JsonIgnore
    @ManyToMany( targetEntity=Job.class, fetch = FetchType.LAZY )
    private Set<Job> interestJobs = new HashSet<Job>();
    
    public Seeker() { }
    
    public void setEmail(String email) { this.email = email; }
    public String getEmail() { return this.email; }
    
    public void setPassword(String password) { this.password = password; }
    public String getPassword() { return this.password; }
    
    public void setFirstname(String firstname) { this.firstname = firstname; }
    public String getFirstname() { return this.firstname; }
    
    public void setLastname(String lastname) { this.lastname = lastname; }
    public String getLastname() { return this.lastname; }
    
    public void setPicture(String picture) { this.picture = picture; }
    public String getPicture() { return this.picture; }
    
    public void setSelfIntroduction(String selfIntroduction) { this.selfIntroduction = selfIntroduction; }
    public String getSelfIntroduction() { return this.selfIntroduction; }
    
    public void setWorkExperience(String workExperience) { this.workExperience = workExperience; }
    public String getWorkExperience() { return this.workExperience; }
    
    public void setEducation(String education) { this.education = education; }
    public String getEducation() { return this.education; }
    
    public void setSkills(String skills) { this.skills = skills; }
    public String getSkills() { return this.skills; }
    
    public void setApplications (Set<Application> applications) { this.applications = applications; }
    public Set<Application> getApplications() { return this.applications; }
    
    public void setInterestJobs (Set<Job> interestJobs) { this.interestJobs = interestJobs; }
    public Set<Job> getInterestJobs() { return this.interestJobs; }
        
    public boolean equals(Object other) {
        if (other instanceof Seeker) {
            return this.getEmail().equals(((Seeker)other).getEmail());
        } else {
            return false;
        }
    }
    
    public void update(Seeker anotherSeeker) {
        
        if (null != anotherSeeker.getPassword())          { this.password = anotherSeeker.getPassword();}
        if (null != anotherSeeker.getFirstname())         { this.firstname = anotherSeeker.getFirstname();}
        if (null != anotherSeeker.getLastname())          { this.lastname = anotherSeeker.getLastname();}
        if (null != anotherSeeker.getPicture())           { this.picture = anotherSeeker.getPicture();}
        if (null != anotherSeeker.getWorkExperience())    { this.workExperience = anotherSeeker.getWorkExperience(); }
        if (null != anotherSeeker.getSelfIntroduction())  { this.selfIntroduction = anotherSeeker.getSelfIntroduction(); }
        if (null != anotherSeeker.getEducation())         { this.education = anotherSeeker.getEducation();}
        if (null != anotherSeeker.getSkills())            { this.skills = anotherSeeker.getSkills();}
        
    }
    
    public boolean isAppExd() {
        
        Iterator<Application> applicationIt = null;
        int num = 0;
        
        applicationIt = this.getApplications().iterator(); 
        while(applicationIt.hasNext()) {
            
            if (applicationIt.next().isPending()) { num++; }
            
            if (maxPendingApp == num) { return true; }
        }
        
        return false;
        
    }
    
    public boolean canApply(String jobId) {
        
        Iterator<Application> applicationIt = null;
        Application application = null;
        Job job = null;
        
        applicationIt = this.getApplications().iterator(); 
        while(applicationIt.hasNext()){ 
            
            application = applicationIt.next();
            job = application.getJob();
            if ((job.getId().equals(jobId)) && (application.isPending())) { return false; }
        }
        
        return true;
        
    }
    
    public boolean checkInfo() {
        
        if (null == this.firstname) { return false; }
        if (null == this.lastname) {return false; }
        if (null == this.selfIntroduction) { return false; }
        if (null == this.workExperience) { return false; }
        if (null == this.education) {return false; }
        if (null == this.skills) { return false; }
        
        return true;
    }
    
    public boolean canCancel(String[] appList) {
        
        Iterator<Application> applicationIt = null;
        Application application = null;
        int i = 0;
        
        applicationIt = this.getApplications().iterator(); 
        while(applicationIt.hasNext()){ 
            
            application = applicationIt.next();
            for (i = 0; i < appList.length; i++) {
                if ((appList[i].equals(application.getId())) && (!application.getStatus().equals("Pending"))) {return false; }
            }
            
        }
        
        return true;
        
    }
    
    public boolean canReject(String[] appList) {
        
        Iterator<Application> applicationIt = null;
        Application application = null;
        Job job = null;
        int i = 0;
        
        applicationIt = this.getApplications().iterator(); 
        while(applicationIt.hasNext()){ 
            
            application = applicationIt.next();
            for (i = 0; i < appList.length; i++) {
                if ((appList[i].equals(application.getId())) && (!application.getStatus().equals("Offered"))) {return false; }
            }
            
        }
        
        return true;
        
    }
    
public boolean canAccept(String[] appList) {
        
        Iterator<Application> applicationIt = null;
        Application application = null;
        Job job = null;
        int i = 0;
        
        applicationIt = this.getApplications().iterator(); 
        while(applicationIt.hasNext()){ 
            
            application = applicationIt.next();
            for (i = 0; i < appList.length; i++) {
                if ((appList[i].equals(application.getId())) && (!application.getStatus().equals("Offered"))) {return false; }
            }
            
        }
        
        return true;
        
    }
    
    public void fetchLazy() {
        
        
        this.applications = new HashSet<Application>();
        this.interestJobs = new HashSet<Job>();
        
        return;
        
    }
    
}
