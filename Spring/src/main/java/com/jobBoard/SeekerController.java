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
@RequestMapping("seeker")
public class SeekerController {
  
    private SeekerDao seekerDao = new SeekerDao();
    private static final int CODE_LEN = 6;
    

    

    @RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json")
    public Object create(@RequestBody Seeker seeker){ 

        return seekerDao.create(seeker); 
    }
    
    
    @RequestMapping(value = "/{email:.+}", method = RequestMethod.PUT, produces = "application/json")
    public Object update(@PathVariable String email, @RequestBody Seeker seeker) {
    	
    	seeker.setEmail(email);
    	return seekerDao.update(seeker); 
    }
    
    @RequestMapping(value = "/{email:.+}/markInterest/{jobId}", method = RequestMethod.PUT, produces = "application/json")
    public Object markInterest(@PathVariable String email, @PathVariable String jobId) {
        return seekerDao.markOrUnmarkInterest(email, jobId, true);
    }
    
    @RequestMapping(value = "/{email:.+}/interestJobs", method = RequestMethod.GET, produces = "application/json")
    public Object getInterestJobs(@PathVariable String email) { return seekerDao.getInterestJobs(email); }
    
    @RequestMapping(value = "/{email:.+}/unmarkInterest/{jobId}", method = RequestMethod.PUT, produces = "application/json")
    public Object unmarkInterest(@PathVariable String email, @PathVariable String jobId) { return seekerDao.markOrUnmarkInterest(email, jobId, false); }
    
    @RequestMapping(value = "/{email:.+}/apply/{jobId}", method = RequestMethod.POST, produces = "application/json")
    public Object apply(@PathVariable String email, 
                        @PathVariable String jobId,
                        @RequestBody Application application) 
    { 
        System.out.println("in Apply");
        return seekerDao.apply(email, jobId, application); 
    }
    
    @RequestMapping(value = "/{email:.+}/cancel", method = RequestMethod.PUT, produces = "application/json")
    public Object cancel(@PathVariable String email, 
                         @RequestParam("appList") String[] appList)
    {
    	System.out.println("got cancel request!!! "+ appList[0]);
        return seekerDao.cancel(email, appList);
    }
    
    @RequestMapping(value = "/{email:.+}/reject", method = RequestMethod.PUT, produces = "application/json")
    public Object reject(@PathVariable String email, 
                         @RequestParam("appList") String[] appList)
    {
        return seekerDao.reject(email, appList);
    }
    
    @RequestMapping(value = "/{email:.+}/accept", method = RequestMethod.PUT, produces = "application/json")
    public Object accept(@PathVariable String email, 
                         @RequestParam("appList") String[] appList)
    {
        return seekerDao.accept(email, appList);
    }
    
    @RequestMapping(value = "/{email:.+}", method = RequestMethod.GET, produces = "application/json")
    public Object get(@PathVariable String email) { return seekerDao.findById(email); }
    
    
    @RequestMapping(value = "/{email:.+}/applications", method = RequestMethod.GET, produces = "application/json")
    public Object getApps(@PathVariable String email) 
    {
        return seekerDao.getApplications(email);
    }
    
    
   
    	
    
}
