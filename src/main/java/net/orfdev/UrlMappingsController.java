package net.orfdev;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class UrlMappingsController {
	
	@Autowired
	private UrlShortDatabase shortUrlDb;

    
    
    
 	
    //Take URL as param shorten url
    @RequestMapping("/shorten") // defines the URL to access this method
    @ResponseBody
    public String shortenUrl(
    		@RequestParam(value="url", required=true) String longUrl, // the request parameter you can pass to the URL
    		Model model) { // the Model object is provided by the Spring framework to allow you to pass variables to the template
    		
    		Random randomNumber = new Random();
    		long random = randomNumber.nextLong();
    		if(random < 0){
    			random = random * -1;
    		}
    		String base62Number = Base62.encode(random);
    		shortUrlDb.insert(base62Number, longUrl);
    		
    	
        return "http://localhost:8080/" + base62Number; // resolves to the HTML template at src/main/resources/templates/greeting.html
    }
    
    @RequestMapping("/{shortUrl}")
    public String mapUrl(@PathVariable(value="shortUrl") String shortUrl, Model model) {
    	
        String longUrl = shortUrlDb.lookup(shortUrl);
        
        if(longUrl == null){
        	System.out.println("Url not Found");
        	longUrl = "";
        }
    	
        return "redirect:" + longUrl;
    }
    
    // You can add more mappings and methods here!
    
    
    
    
    
    
}