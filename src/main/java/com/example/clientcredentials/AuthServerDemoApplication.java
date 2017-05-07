package com.example.clientcredentials;

import java.util.Optional;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class AuthServerDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthServerDemoApplication.class, args);
	}
}

@Configuration
@EnableAuthorizationServer
class AuthServiceConfiguration extends AuthorizationServerConfigurerAdapter {

	/*
	 * @Override public void configure(AuthorizationServerSecurityConfigurer
	 * arg0) throws Exception { }
	 */

	@Override
	public void configure(ClientDetailsServiceConfigurer clients)
			throws Exception {
		clients.inMemory().withClient("client1").secret("pass");
		//		.authorizedGrantTypes("client_credentials");
		//.authorizedGrantTypes("client_credentials").scopes("openid");
		//clients.jdbc(dataSource);
	}

/*	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints)
			throws Exception {
		//endpoints.authenticationManager(this.authenticationManager);
	}
*/
}

@RestController
class AcctRestController {

	@RequestMapping(value = "/healthstatus", method = RequestMethod.GET)
	public String healthStatus() {
		return "up and running " ;
	}
}
