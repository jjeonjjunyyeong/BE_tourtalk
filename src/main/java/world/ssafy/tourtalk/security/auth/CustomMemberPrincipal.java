package world.ssafy.tourtalk.security.auth;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.AllArgsConstructor;
import lombok.Getter;
import world.ssafy.tourtalk.model.dto.enums.Role;

@Getter
@AllArgsConstructor
public class CustomMemberPrincipal implements UserDetails {
	private final int mno;
	private final String id;
	private final String nickname;
	private final Role role;
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
	}
	
	@Override public String getPassword() { return null; }
	@Override public String getUsername() { return id; }
	@Override public boolean isAccountNonExpired() { return true; }
	@Override public boolean isAccountNonLocked() { return true; }
	@Override public boolean isCredentialsNonExpired() { return true; }
	@Override public boolean isEnabled() { return true; }
}
