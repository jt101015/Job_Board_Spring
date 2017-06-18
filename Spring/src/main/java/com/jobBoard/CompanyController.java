package main.java.com.jobBoard;


import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.RandomStringUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.DeserializationConfig.Feature;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
//import org.springframework.ui.ModelMap;


@RestController
@RequestMapping("company")
public class CompanyController {
  
    private CompanyDao companyDao = new CompanyDao();
    
    private static final int CODE_LEN = 6;

    @RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json")
    public Object create(@RequestBody Company company){ 

        return companyDao.create(company); 
    }
    
    @RequestMapping(value = "/{email:.+}", method = RequestMethod.GET, produces = "application/json")
    public Object get(@PathVariable String email) { return companyDao.findById(email); }
    
    
    @RequestMapping(value = "/application/{id}", method = RequestMethod.GET, produces = "application/json")
    public Object getApplication(@PathVariable String id) { return companyDao.getApplication(id); }
    
    
    @RequestMapping(value = "/application/{id}/{status}", method = RequestMethod.PUT, produces = "application/json")
    public Object getApplication(@PathVariable String id, @PathVariable String status) { return companyDao.updateApplicationStatus(id, status); }
    
    
    /*
    @RequestMapping(value = "/{email:.+}/applications", method = RequestMethod.GET, produces = "application/json")
    public Object get(@PathVariable String email) { return companyDao.findAllApplications(email); }*/
    
    
    @RequestMapping(value = "/{email:.+}", method = RequestMethod.PUT, produces = "application/json")
    public Object update(@PathVariable String email, @RequestBody Company company) {  
        company.setEmail(email);
        System.out.println("update a company");
        System.out.println(company.getCompanyname());
        return companyDao.update(company); 
    }
    
    	
    
}
