package net.orfdev;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class UrlShortDatabase {
	@Autowired
	private JdbcTemplate jdbctemplate;
	
	@PostConstruct
	public void constructDB(){
		jdbctemplate.execute("CREATE TABLE If Not Exists urls (short_url text Primary Key, long_url text);");
	}
	
	public void insert(String base62Number, String longUrl) {
		
		jdbctemplate.update("Insert Into urls (short_url , long_url) Values (?,?)", base62Number, longUrl);
	}

	public String lookup(String shortUrl) {
		List<String> results = jdbctemplate.queryForList("Select long_url From urls Where short_url = ?", String.class, shortUrl);
		if(results.isEmpty()){
			return null;
		}
		return results.get(0);
	}
	
	
	
}
