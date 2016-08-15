package com.cereadro;

import com.cereadro.user.User;
import com.cereadro.user.UserDao;
import com.cereadro.user.UserRole;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.filter.CharacterEncodingFilter;

import javax.servlet.Filter;
import java.time.LocalDateTime;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public InitializingBean insertDefaultUsers() {
		return new InitializingBean() {
			@Autowired
			private UserDao userDao;

			@Override
			public void afterPropertiesSet() {
                User adminUser = userDao.findByUsername("admin");
                if(adminUser == null)  {
                    addUser("admin", "admin");
                }
			}

			private void addUser(String username, String password) {
				User user = new User();
				user.setUsername(username);
				user.setPassword(new BCryptPasswordEncoder().encode(password));
				user.grantRole(username.equals("admin") ? UserRole.ADMIN : UserRole.USER);
                user.setCreatedDtime(LocalDateTime.now());
				userDao.save(user);
			}
		};
	}

	@Bean
	public Filter characterEncodingFilter() {
		CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
		characterEncodingFilter.setEncoding("UTF-8");
		characterEncodingFilter.setForceEncoding(true);
		return characterEncodingFilter;
	}
}
