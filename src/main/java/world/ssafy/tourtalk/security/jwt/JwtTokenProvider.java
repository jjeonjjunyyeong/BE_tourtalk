package world.ssafy.tourtalk.security.jwt;

import java.util.Date;
import java.security.Key;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import world.ssafy.tourtalk.model.dto.Member.Role;

@Component
public class JwtTokenProvider {

	private final Key key;
    private final long validityInMs = 3600000; // 1시간
	
    public JwtTokenProvider(@Value("${jwt.secret}") String rawSecretKey) {
    	byte[] keyBytes = Base64.getEncoder().encode(rawSecretKey.getBytes());
        this.key = new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
    }
    
    public String createToken(int mno, String userId, String nickname, Role role) {
        Claims claims = Jwts.claims().setSubject(String.valueOf(mno));
        claims.put("id", userId);
        claims.put("nickname", nickname);
        claims.put("role", role.name());
        
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMs);

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(validity)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
    }
    
    // 토큰에서 사용자 ID 꺼내기
    public int getUserMno(String token) {
        return Integer.parseInt(
        		Jwts.parserBuilder()
		            .setSigningKey(key)
		            .build()
		            .parseClaimsJws(token)
		            .getBody()
		            .getSubject());
    }
    
    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    // 로그인 ID 
    public String getUserId(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("id", String.class);
    }

    // 닉네임
    public String getNickname(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("nickname", String.class);
    }

    // 권한
    public Role getRole(String token) {
        String roleName = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role", String.class);
        return Role.valueOf(roleName);
    }

    public Authentication getAuthentication(String token) {
    	int mno = getUserMno(token); 
        Role role = getRole(token);

        GrantedAuthority authority = new SimpleGrantedAuthority(role.name());
        return new UsernamePasswordAuthenticationToken(mno, null, List.of(authority));
    }
    
    public String resolveToken(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
