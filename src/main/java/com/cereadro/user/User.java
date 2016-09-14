package com.cereadro.user;

import com.cereadro.security.UserAuthority;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Email;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "cer.user2", uniqueConstraints = @UniqueConstraint(columnNames = { "username" }))
public class User implements UserDetails {

	public User() {
	}

	public User(String username) {
		this.username = username;
	}

	public User(String username, Date expires) {
		this.username = username;
		this.expires = expires.getTime();
	}

	@Id
    @Setter
    @Getter
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotNull
	@Size(min = 4, max = 50)
    @Setter
    @Getter
	private String username;

	@NotNull
	@Size(min = 4, max = 100)
    @Setter
	private String password;

    @Getter
    @Setter
    @Email
    @Size(min = 5, max = 50)
    private String email;

    @Getter
    @Setter
    @NotNull
    @Size(min = 5, max = 50)
    private String lastName;

    @Getter
    @Setter
    @NotNull
    @Size(min = 5, max = 50)
    private String firstName;

    @NotNull
    @Setter
    @Getter
    @JsonIgnore
    private LocalDateTime createdDtime;

	@Transient
    @Setter
    @Getter
	private long expires;

	@NotNull
    @Setter
    @Getter
	private boolean accountExpired;

	@NotNull
    @Setter
    @Getter
	private boolean accountLocked;

	@NotNull
    @Setter
    @Getter
	private boolean credentialsExpired;

	@NotNull
    @Setter
    @Getter
	private boolean accountEnabled;

	@Transient
    @Setter
    @Getter
    @JsonIgnore
	private String newPassword;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "user", fetch = FetchType.EAGER, orphanRemoval = true)
	private Set<UserAuthority> authorities;

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	@JsonIgnore
	public String getPassword() {
		return password;
	}

	@JsonProperty
	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	@JsonIgnore
	public Set<UserAuthority> getAuthorities() {
		return authorities;
	}

	public Set<UserRole> getRoles() {
		Set<UserRole> roles = EnumSet.noneOf(UserRole.class);
		if (authorities != null) {
			for (UserAuthority authority : authorities) {
				roles.add(UserRole.valueOf(authority));
			}
		}
		return roles;
	}

	public void setRoles(Set<UserRole> roles) {
		for (UserRole role : roles) {
			grantRole(role);
		}
	}

	public void grantRole(UserRole role) {
		if (authorities == null) {
			authorities = new HashSet<UserAuthority>();
		}
		authorities.add(role.asAuthorityFor(this));
	}

	public void revokeRole(UserRole role) {
		if (authorities != null) {
			authorities.remove(role.asAuthorityFor(this));
		}
	}

	public boolean hasRole(UserRole role) {
		return authorities.contains(role.asAuthorityFor(this));
	}

	@Override
	@JsonIgnore
	public boolean isAccountNonExpired() {
		return !accountExpired;
	}

	@Override
	@JsonIgnore
	public boolean isAccountNonLocked() {
		return !accountLocked;
	}

	@Override
	@JsonIgnore
	public boolean isCredentialsNonExpired() {
		return !credentialsExpired;
	}

	@Override
	@JsonIgnore
	public boolean isEnabled() {
		return !accountEnabled;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + ": " + getUsername();
	}
}
