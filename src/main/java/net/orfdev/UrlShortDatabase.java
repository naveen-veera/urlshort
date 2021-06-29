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
	public void constructDB(){
		jdbctemplate.execute("CREATE TABLE If Not Exists urls (short_url varchar(32) Primary Key, long_url varchar(256));");
	}
	
	
	public void insert(String base62Number, String longUrl) {	
		jdbctemplate.update("INSERT INTO urls (short_url , long_url) Values (?,?)", base62Number, longUrl);
	}

	
	public String lookup(String shortUrl) {
		List<String> results = jdbctemplate.queryForList("SELECT long_url FROM urls WHERE short_url = ?", String.class, shortUrl);
		if(results.isEmpty()){
			return null;
		}
		return results.get(0);
	}
	
}