package com.user_service.jwt;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;


@Component
public class JwtHelper {
	
	private static final Logger logger = LoggerFactory.getLogger(JwtHelper.class);


	@Value("${jwt.expiration}")
	private long expiration;
	
	@Value("${jwt.secret}")
	private String secret;
	
	
	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

	
	
    //for retrieveing any information from token we will need the secret key
	   // For retrieving any information from the token, we need the secret key
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
    
  


	 //validate token
   
    public Boolean validateToken(String token) {

    	try {
    		Claims claims=Jwts.parser().setSigningKey(secret).build()
                    .parseClaimsJws(token)
                    .getBody();
        	if(isTokenExpired(token)) {
        			return false;
        	}
        	return true;
		} catch (Exception e) {
			return false;
		}
    
    }


	public String getUsernameFromToken(String token) {
		return getClaimFromToken(token, Claims::getSubject);
	}


	


	private boolean isTokenExpired(String token) {
		   final Date expiration = getExpirationDateFromToken(token);
	        return expiration.before(new Date(System.currentTimeMillis()));
	}


	private Date getExpirationDateFromToken(String token) {
		return getClaimFromToken(token, Claims::getExpiration);
	}


}
