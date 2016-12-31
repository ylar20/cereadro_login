package com.cereadro;

import com.cereadro.user.User;
import com.cereadro.user.UserDao;
import com.cereadro.user.UserDetailsService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.CharacterEncodingFilter;

import javax.servlet.Filter;

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

            @Autowired
            private UserDetailsService userDetailsService;

			@Override
			public void afterPropertiesSet() {
                User adminUser = userDao.findByUsername("admin");
                if(adminUser == null)  {
                	adminUser = userDetailsService.createAdminUser();
                    userDetailsService.addUser(adminUser);
                }
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
