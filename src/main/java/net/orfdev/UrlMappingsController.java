package net.orfdev;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.*;
import java.sql.SQLException;

@Controller
public class UrlMappingsController {
	private static final Logger log = LogManager.getLogger();
	
	@Autowired
	private UrlShortDatabase shortUrlDb;

	// --------------------------------------------------------------------------------------------------------------------- //
	// The first set of methods are just here to illustrate how a Spring MVC application can work. The @RequestMapping 
	// annotation is what tells Spring that when a certain URL is requested in a browser it should be mapped onto a specific
	// method here. Spring has several convenient mechanisms for (eg) mapping querystring paramters to method variables, or
	// passing in a Model object, or even HttpServletRequest and HttpServletResponse objects, as variables.
	// You should NOT need to change these methods.
	// --------------------------------------------------------------------------------------------------------------------- //
	
	@RequestMapping("/") // defines the URL to access this method - so this will be http://localhost:8080/
	public String hello(
			@RequestParam(defaultValue="World") String who,  // querystring or form parameter with name "who"
			Model model // the Model object is provided by the Spring framework to allow you to pass variables to the template
			) {
		model.addAttribute("who", who);
		return "hello"; // resolves to the HTML template at src/main/resources/templates/hello.html
	}

	
	@RequestMapping("/sam") // defines the URL to access this method - so this will be http://localhost:8080/sam
	public String helloSam() {
		return "redirect:/?who=Sam" ; // using the "redirect:" prefix tells Spring to return a 301 redirect instead of rendering content
	}

	
	@RequestMapping("/sam/{surname}") // defines the URL to access this method - so this will be http://localhost:8080/sam/someValue
	public String helloSamWithSurname(
			@PathVariable(value="surname") String surname // this variable's value is obtained from the RequestMapping, for
				// example if the URL "/sam/foo" was requested the variable's value would be "foo" 
		) {
		return "redirect:/?who=Sam+" + surname; // so eg. http://localhost:8080/sam/I+am would redirect to http://localhost:8080/?who=Sam+I+am
	}
	
	
	@RequestMapping("favicon.ico") // browsers insist on requesting the favicon so just return a 404
	public void favicon(HttpServletResponse response) throws IOException {
		response.sendError(404);
	}

	
	// --------------------------------------------------------------------------------------------------------------------- //
	// The next set of methods are the actual useful thing that do the work of the application.
	// Here is where you may need to add/remove/change code in order to complete the coding task. 
	// --------------------------------------------------------------------------------------------------------------------- //
	
	@RequestMapping("/errors") 
	public String Error( @RequestParam(defaultValue="err") String err,  Model model ) { // To display the Error
		model.addAttribute("err", err);
		return "Error"; 
	}


		// Shorten a URL and then show an HTML page
	@RequestMapping("/html/shorten")
	public String shortenUrlAndReturnHtml(@RequestParam(value="url", required=true) String longUrl, Model model) throws MalformedURLException,IOException{

		if(isValidUrl(longUrl)){ 	// TO check URL is Valid or Invalid
			
			if(longUrl.length() <= 256){ 	// TO chck the maximum character
				String shortUrl = handleDuplicateURL(longUrl); 	// TO check the short uel is already in DB
				model.addAttribute("originalUrl", longUrl);
				model.addAttribute("shortUrl", "http://localhost:8080/" + shortUrl);
				return "shortened";
			}
			else{
				return	"redirect:/errors?err=Maximum of 256 characters allowed in URL.";
			}

		}else{
			return "redirect:/errors?err=Invalid Url";
		}

	}

	// Shorten a URL and then return a JSON payload
	@RequestMapping("/json/shorten")
	public @ResponseBody Map<String, String> shortenUrlAndReturnJson(@RequestParam(value="url", required=true) String longUrl, Model model) {
		
		String shortUrl = shortenUrl(longUrl);
		
		Map<String, String> payload = new HashMap<>();
		payload.put("originalUrl", longUrl);
		payload.put("shortUrl", "http://localhost:8080/" + shortUrl);
		
		return payload;
	}

	@RequestMapping("/{shortUrl}")
	public String mapUrl(@PathVariable(value="shortUrl") String shortUrl, Model model) {
		
		String longUrl = shortUrlDb.lookupByShortUrl(shortUrl);

		if(longUrl == null){
			log.debug("Short url code [{}] not found in DB so returning no content", shortUrl);
			longUrl = "";
		}
		
		return "redirect:" + longUrl;
	}

	@RequestMapping("/health")
	public @ResponseBody Map<String, Integer> healthCheck(){
		Map<String, Integer> data = new HashMap<>();
		
		int count = shortUrlDb.numberOfRecords();
		data.put("recordCount", count);

		return data;
	}


	
	private String shortenUrl(String longUrl) {
		int retry = 15;  //	 Limit the number of retries 15
		String shortUrl = null;
		while(retry != 0) {  // Handle duplicate random numbers
			Random randomNumber = new Random();
			long random = randomNumber.nextLong();
			if(random < 0) {
				random = random * -1;
			}
			shortUrl = Base62.encode(random);
			try {
				shortUrlDb.insert(shortUrl, longUrl);
				break;
			}catch (Exception e) {
				retry-=1; // retry = retry-1
				if(retry == 1){
					log.fatal("Limit Exceeds. Try after sometime or another URL");
					throw e;
					
				}
			}
		}
		return shortUrl;
	}
	
	
	// Function to check the URL is Valid or Not.
	public boolean isValidUrl(String url) throws MalformedURLException, IOException{
		URL uri = new URL(url);
		try{
			HttpURLConnection huc = (HttpURLConnection) uri.openConnection();
			int responseCode = huc.getResponseCode();
			System.out.println(responseCode);
			if(responseCode == 200){
				return true;
			}
			else{
				return false;
			}
		}catch(Exception e){
			return false;
		}
		
	}
	
	// Function to handle the Duplicate URL
	public String handleDuplicateURL(String longUrl){
		String shortUrl;
		String result = shortUrlDb.handleDuplicate(longUrl);
		
		if(result == null){
			shortUrl = shortenUrl(longUrl);
		}else{
			shortUrl = result;
		}
		return shortUrl;
	}
}