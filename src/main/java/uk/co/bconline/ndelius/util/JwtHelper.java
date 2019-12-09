package uk.co.bconline.ndelius.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.crypto.MacProvider;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import uk.co.bconline.ndelius.model.auth.UserInteraction;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Stream;

import static io.jsonwebtoken.SignatureAlgorithm.HS512;
import static java.time.LocalDateTime.now;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

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

	@Value("${jwt.secure:#{true}}")
	private boolean cookieIsSecure;

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

	public String generateToken(String username, List<UserInteraction> interactions)
	{
		return Jwts.builder()
				.setIssuer(appName)
				.setSubject(username)
				.claim("interactions", interactions.stream().map(UserInteraction::getAuthority).collect(toList()))
				.setIssuedAt(new Date())
				.setExpiration(expirationDate())
				.signWith(HS512, secret)
				.compact();
	}

	public Claims parseToken(String token)
	{
		return Jwts.parser()
				.setSigningKey(secret)
				.parseClaimsJws(token)
				.getBody();
	}

	public void addTokenToResponse(String token, HttpServletResponse response)
	{
		response.addCookie(createCookie(token));
	}

	public Cookie createCookie(String token)
	{
		val cookie = new Cookie(cookieName, token);
		cookie.setSecure(cookieIsSecure);
		cookie.setHttpOnly(true);
		cookie.setPath("/");
		return cookie;
	}

	public List<UserInteraction> getInteractions(Claims claims) {
		return ((List<String>) claims.get("interactions")).stream()
				.map(UserInteraction::new)
				.collect(toList());
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