package net.orfdev;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * This class is just a crude way of doing very simple DDL and CRUD on a SQL database 
 */
@Component
public class UrlShortDatabase {
	
	@Autowired
	private JdbcTemplate jdbctemplate;
	
	
	@PostConstruct
	public void constructDB() {
		jdbctemplate.execute("CREATE TABLE If Not Exists urls (short_url varchar(32) Primary Key, long_url varchar(256));");
	}
	
	
	public void insert(String base62Number, String longUrl) {	
		jdbctemplate.update("INSERT INTO urls (short_url, long_url) Values (?,?)", base62Number, longUrl);
	}

	
	public String lookupByShortUrl(String shortUrl) {
		List<String> results = jdbctemplate.queryForList("SELECT long_url FROM urls WHERE short_url = ?", String.class, shortUrl);
		if(results.isEmpty()){
			return null;
		}
		return results.get(0);
	}

	// To Handle Handle duplicate URLs
	public String handleDuplicate(String longUrl) {
		List<String> result = jdbctemplate.queryForList("SELECT short_url FROM urls WHERE long_url = ?", String.class, longUrl);
		if(result.isEmpty()){
			return null;
		}
		return result.get(0);
	}

	//Health - return the number of records in urls table.
	public int numberOfRecords() {
		int count = jdbctemplate.queryForObject("SELECT COUNT(short_url) FROM urls", Integer.class);
		return count;
	}
}