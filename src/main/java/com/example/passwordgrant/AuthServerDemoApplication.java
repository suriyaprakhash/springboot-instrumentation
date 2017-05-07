package com.example.passwordgrant;

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
public class AuthServerDemoApplication implements CommandLineRunner {

	@Autowired
	AccountRepository acctRepository;

	/*
	 * 
	 * @Bean CommandLineRunner demo(AccountRepository accountRepository) {
	 * return args->{}; }
	 */

	public void run(String... arg0) throws Exception {
		acctRepository.save(new Account("suriya", "pass1", true));
		acctRepository.save(new Account("chandra", "pass2", true));
	}

	public static void main(String[] args) {
		SpringApplication.run(AuthServerDemoApplication.class, args);
	}
}

@Configuration
@EnableAuthorizationServer
class AuthServiceConfiguration extends AuthorizationServerConfigurerAdapter {

	AuthenticationManager authenticationManager;

	AuthServiceConfiguration(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

	/*
	 * @Override public void configure(AuthorizationServerSecurityConfigurer
	 * arg0) throws Exception { }
	 */

	@Override
	public void configure(ClientDetailsServiceConfigurer clients)
			throws Exception {
		clients.inMemory().withClient("client1").secret("pass")
				.authorizedGrantTypes("password").scopes("openid");
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints)
			throws Exception {
		endpoints.authenticationManager(this.authenticationManager);
	}

}

@RestController
class AcctRestController {
	// here no need for @Autowired
	AccountRepository accountRepository;

	/*
	 * @Autowired public void setAccountRepository(AccountRepository
	 * accountRepository) { this.accountRepository = accountRepository; }
	 */
	public AcctRestController(AccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}

	@RequestMapping(value = "/healthstatus", method = RequestMethod.GET)
	public String healthStatus() {
		Account acct = new Account();
		acct = accountRepository.findAll().get(0);
		return "up and running " + acct.getUsername();
	}
}

@Service
class AccountUserDetailService implements UserDetailsService {

	AccountRepository accountRepository;

	public AccountUserDetailService(AccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {
		/*	Optional<Account> optional = accountRepository.findByUsername(username);
		Account acct = optional.get();
		if (acct == null) {
			throw new UsernameNotFoundException("username mismatch");
		}
		User user = new User(acct.getUsername(), acct.getPassword(),
				acct.isActive(), acct.isActive(), acct.isActive(),
				acct.isActive(), AuthorityUtils.createAuthorityList(
						"ROLE_ADMIN", "ROLE_USER"));*/
		User user = new User("sss", "pass1",
				true,true,true,true,AuthorityUtils.createAuthorityList(
						"ROLE_ADMIN", "ROLE_USER"));

		return user;
	}

}

interface AccountRepository extends JpaRepository<Account, Long> {

	Optional<Account> findByUsername(String username);

}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
class Account {

	@Id
	@GeneratedValue
	private int id;
	private String username;
	private String password;
	private boolean active;

	Account(String username, String password, boolean active) {
		this.username = username;
		this.password = password;
		this.active = active;
	}

	public Account() {
		// TODO Auto-generated constructor stub
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

}