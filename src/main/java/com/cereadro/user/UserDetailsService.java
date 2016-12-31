package com.cereadro.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;

@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

	@Autowired
	private UserDao userDao;

	private final AccountStatusUserDetailsChecker detailsChecker = new AccountStatusUserDetailsChecker();

	@Override
	public final User loadUserByUsername(String username) throws UsernameNotFoundException {
		final User user = userDao.findByUsername(username);
		if (user == null) {
			throw new UsernameNotFoundException("user not found");
		}
		detailsChecker.check(user);
		return user;
	}

    public void addUser(User user) {
		user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
		user.grantRole(UserRole.USER);
		user.setCreatedDtime(LocalDateTime.now());
        userDao.save(user);
    }

    public User createAdminUser() {
		User adminUser = new User();
		adminUser.setFirstName("BrainAdmin");
		adminUser.setLastName("BrainAdmin");
		adminUser.setUsername("admin");
		adminUser.setPassword(new BCryptPasswordEncoder().encode("brainroot"));
		adminUser.setEmail("admin@braintext.net");
		adminUser.setAccountEnabled(true);
		adminUser.setCreatedDtime(LocalDateTime.now());
		adminUser.grantRole(UserRole.ADMIN);
		return adminUser;
	}
}
