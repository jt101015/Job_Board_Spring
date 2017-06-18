package main.java.com.jobBoard;


 
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.search.annotations.ContainedIn;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

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

@JsonRootName(value = "company")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "Company")
public class Company {

    @Id
    @Column(name = "email", length = 45, nullable = false)
    private String email = null;
    
    @Column(name = "Password", length = 45, nullable = false)
    private String password = null;
    
    @Column(name = "Companyname", length = 45)
    @Field
    private String Companyname = null;
    @Column(name = "website", length = 45)
    private String website =null;
    @Column(name = "logo", length = 150)
    private String logo =null;// image URL
    @Column(name = "address", length = 100)
    private String address =null;
    @Column(name = "description", length = 200)
    private String description =null;
    
    @ContainedIn
    @OneToMany( targetEntity=Job.class, mappedBy = "company", fetch = FetchType.LAZY)
    private Set<Job> jobs = new HashSet<Job>();
    


    public Company(){}
    
    
    
    
    public void setEmail(String email) { this.email = email; }
    public String getEmail() { return this.email; }
    
    public void setPassword(String password) { this.password = password; }
    public String getPassword() { return this.password; }
    
    public void setJobs(Set<Job> jobs) { this.jobs = jobs; }
    public Set<Job> getJobs() { return this.jobs; }
    
    public void setCompanyname(String name) { this.Companyname = name; }
    public String getCompanyname() { return this.Companyname; }
    
    /*public void SetName(String name) { this.Name = name; }
    public String getName() { return this.Name; }*/
    public void setWebsite(String website) { this.website = website; }
    public String getWebsite() { return this.website; }
    
    public void setLogo(String logo) { this.logo = logo; }
    public String getLogo() { return this.logo; }
    
    public void setAddress(String address) { this.address = address; }
    public String getAddress() { return this.address; }
    
    public void setDescription(String description) { this.description = description; }
    public String getDescription() { return this.description; }
    
    public boolean equals(Object other) {
        if (other instanceof Company) {
            return this.getEmail().equals(((Company)other).getEmail());
        } else {
            return false;
        }
    }
    
    public void fetchLazy() {
    	this.setJobs(null);
        return;
    }
    
    
    
    
 public void update(Company anotherCompany) {
        
        if (null != anotherCompany.getPassword())          { this.password = anotherCompany.getPassword();}
        if (null != anotherCompany.getCompanyname())         { this.Companyname = anotherCompany.getCompanyname();}
        if (null != anotherCompany.getWebsite())             { this.website = anotherCompany.getWebsite();}
        if (null != anotherCompany.getLogo())    			 { this.logo = anotherCompany.getLogo(); }
        if (null != anotherCompany.getDescription())         { this.description = anotherCompany.getDescription(); }
        if (null != anotherCompany.getAddress())         { this.address = anotherCompany.getAddress();}
        
    }
    
    
}