package world.ssafy.tourtalk.security.jwt;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import world.ssafy.tourtalk.model.dto.enums.Role;
import world.ssafy.tourtalk.security.auth.CustomMemberPrincipal;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtTokenProvider jwtProvider;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String token = jwtProvider.resolveToken(request);
		
		if (token != null && jwtProvider.validateToken(token)) {
	        int mno = jwtProvider.getUserMno(token);
	        String id = jwtProvider.getUserId(token);
	        String nickname = jwtProvider.getNickname(token);
	        Role role = jwtProvider.getRole(token);

	        CustomMemberPrincipal principal = new CustomMemberPrincipal(mno, id, nickname, role);

	        List<GrantedAuthority> authorities =
	                List.of(new SimpleGrantedAuthority("ROLE_" + role));

	        Authentication auth =
	                new UsernamePasswordAuthenticationToken(principal, null, authorities);

	        log.info("인증 principal 타입: {}", auth.getPrincipal().getClass().getName());
	        
	        SecurityContextHolder.getContext().setAuthentication(auth);
	        
	        log.info("JWT 필터 작동됨: token = {}", token);
	    }

	    filterChain.doFilter(request, response);
	}
}
