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
public class JobController {
  
    private JobDao JobDao = new JobDao();
    

    

    @RequestMapping(value = "/jobs", method = RequestMethod.GET, produces = "application/json")
    public Object search(@RequestParam(value = "text", required = false) String text,
                         @RequestParam(value = "companyName", required = false) String[] companyName,
                         @RequestParam(value = "location", required = false) String[] location,
                         @RequestParam(value = "minSalary", required = false) Integer minSalary,
                         @RequestParam(value = "maxSalary", required = false) Integer maxSalary){ 
        
    	String[] textList = null;
    	if (text!= null) textList = text.split(" ");
        
        return JobDao.search(textList, companyName, location, minSalary, maxSalary); 
    }
    
    @RequestMapping(value = "/job/{email:.+}", method = RequestMethod.POST, produces = "application/json")
    public Object create(@PathVariable String email, @RequestBody Job job){ 
        
        return JobDao.add(email, job); 
    }
    
    
    @RequestMapping(value = "/job/{id}", method = RequestMethod.GET, produces = "application/json")
    public Object add(@PathVariable String id){ 
        
        return JobDao.findById(id); 
    }
    
    
    @RequestMapping(value = "/jobs/{email:.+}", method = RequestMethod.GET, produces = "application/json")
    public Object getJobsByCompany(@PathVariable String email){ 
        
        return JobDao.getJobsByCompany(email); 
    }
    
    
    
    @RequestMapping(value = "/job/{id}", method = RequestMethod.PUT, produces = "application/json")
    public Object update(@PathVariable String id,@RequestBody Job job) {  
    	job.setId(id);
    	return JobDao.update(job); 
    }
    
    
    @RequestMapping(value = "/job/{id}/filled", method = RequestMethod.PUT, produces = "application/json")
    public Object filled(@PathVariable String id) {  return JobDao.filled(id); }
    
    
    @RequestMapping(value = "/job/{id}/cancel", method = RequestMethod.PUT, produces = "application/json")
    public Object cancel(@PathVariable String id) {  return JobDao.cancel(id); }
    
    
    @RequestMapping(value = "/emails/{jobId}", method = RequestMethod.GET, produces = "application/json")
    public Object update(@PathVariable String jobId) {  
    	return JobDao.getEmails(jobId); }
    	
    	
    
}
