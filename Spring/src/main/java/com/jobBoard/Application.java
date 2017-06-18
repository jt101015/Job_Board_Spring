package main.java.com.jobBoard;


 
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.annotations.GenericGenerator;

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
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.FetchType;


@JsonRootName(value = "Application")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "Application")
public class Application {
    
    private static final String[] pendingState = {"Pending", "Offered"};
    private static final String offerAcceptState = "OfferAccepted";

    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    @Column(name = "id", length = 45)
    private String id = null;
    
    @ManyToOne( targetEntity=Seeker.class, fetch = FetchType.EAGER, cascade=CascadeType.REFRESH)
    private Seeker seeker;
    
    @ManyToOne( targetEntity=Job.class, fetch = FetchType.EAGER, cascade=CascadeType.REFRESH)
    private Job job;
    
    @Column(name = "fileName", length = 45)
    private String fileName = null;
    
    @Column(name = "fileURL", length = 45)
    private String fileURL = null;
    
    public Application() { }
    
    @Column(name = "status", length = 45, nullable = false)
    private String status;
    
    public void setId(String id) { this.id = id; }
    public String getId() { return this.id; }
    
    public void setSeeker(Seeker seeker) { this.seeker = seeker; }
    public Seeker getSeeker() { return this.seeker; }
    
    public void setJob(Job job) { this.job = job; }
    public Job getJob() { return this.job; }
    
    public void setStatus (String status) { this.status = status; }
    public String getStatus() { return this.status; }
    
    public void setFileName (String fileName) { this.fileName = fileName; }
    public String getFileName() { return this.fileName; }
    
    public void setFileURL (String fileURL) { this.fileURL = fileURL; }
    public String getFileURL() { return this.fileURL; }
    
    public boolean isPending() {
        
        int i = 0;
        
        for (i = 0; i < pendingState.length; i++) {
            if (pendingState[i].equals(this.status)) { return true; }
        }
        
        return false;
        
    }
    
    public boolean isOfferAccepted() { return this.status.equals(offerAcceptState); }
    
    public boolean equals(Object other) {
        if (other instanceof Application) {
            return this.getId().equals(((Application)other).getId());
        } else {
            return false;
        }
    }
    
    public void fetchLazy() {
        
        this.setJob(null);
        this.getSeeker().fetchLazy();
    }
    
}
