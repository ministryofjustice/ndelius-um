package uk.co.bconline.ndelius.util;

import static io.jsonwebtoken.SignatureAlgorithm.HS512;
import static java.time.LocalDateTime.now;
import static java.util.Optional.ofNullable;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.crypto.MacProvider;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtHelper
{
	@Value("${spring.application.name}")
	private String appName;

	@Value("${jwt.secret:}")
	private String secret;

	@Value("${jwt.expiry}")
	private int expiry;

	@Value("${jwt.cookie}")
	private String cookieName;

	@PostConstruct
	public void generateSecret()
	{
		if (StringUtils.isEmpty(secret))
		{
			log.warn("Auto-generating JWT secret key. Any issued tokens will be invalidated on restart.");
			byte[] keyBytes = MacProvider.generateKey(HS512).getEncoded();
			byte[] base64Bytes = Base64.getEncoder().encode(keyBytes);
			secret = new String(base64Bytes);
		}
	}

	/**
	 * Look for a JWT token, first in the auth cookie then in the Authorization header
	 */
	public String getToken(HttpServletRequest request)
	{
		return tokenFromCookie(request)
				.orElse(tokenFromHeader(request)
				.orElse(null));
	}

	public String getUsernameFromToken(String token)
	{
		return parseToken(token).getSubject();
	}

	public String generateToken(String username)
	{
		return Jwts.builder()
				.setIssuer(appName)
				.setSubject(username)
				.setIssuedAt(new Date())
				.setExpiration(expirationDate())
				.signWith(HS512, secret)
				.compact();
	}

	private Claims parseToken(String token)
	{
		return Jwts.parser()
				.setSigningKey(secret)
				.parseClaimsJws(token)
				.getBody();
	}

	private Optional<String> tokenFromHeader(HttpServletRequest request)
	{
		return ofNullable(request.getHeader(AUTHORIZATION))
				.filter(header -> header.startsWith("Bearer "))
				.map(header -> header.substring(7));
	}

	private Optional<String> tokenFromCookie(HttpServletRequest request)
	{
		return ofNullable(request.getCookies())
				.map(Arrays::stream)
				.orElseGet(Stream::empty)
				.filter(cookie -> cookieName.equals(cookie.getName()))
				.map(Cookie::getValue)
				.findFirst();
	}

	private Date expirationDate()
	{
		return Date.from(now()
				.plusMinutes(expiry)
				.atZone(ZoneOffset.systemDefault())
				.toInstant());
	}
}