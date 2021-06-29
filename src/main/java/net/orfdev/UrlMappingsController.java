package net.orfdev;

import java.io.IOException;
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
	
	// Shorten a URL and then show an HTML page
	@RequestMapping("/html/shorten")
	public String shortenUrlAndReturnHtml(@RequestParam(value="url", required=true) String longUrl, Model model) {

		String shortUrl = shortenUrl(longUrl);
		
		model.addAttribute("originalUrl", longUrl);
		model.addAttribute("shortUrl", "http://localhost:8080/" + shortUrl);
		
		return "shortened";
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

	
	private String shortenUrl(String longUrl) {
		
		Random randomNumber = new Random();
		long random = randomNumber.nextLong();
		if(random < 0) {
			random = random * -1;
		}
		String shortUrl = Base62.encode(random);
		shortUrlDb.insert(shortUrl, longUrl);
		
		return shortUrl;
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
	
	
	// You can add more methods here if you need to!
	
}