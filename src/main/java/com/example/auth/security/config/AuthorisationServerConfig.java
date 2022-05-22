package com.example.auth.security.config;

import java.security.KeyPair;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.rsa.crypto.KeyStoreKeyFactory;

@Configuration
@EnableAuthorizationServer
public class AuthorisationServerConfig extends AuthorizationServerConfigurerAdapter {

	private static final String RESOURCE_ID = "couponservice";

	@Autowired
	private UserDetailsService userDetaillsService;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private PasswordEncoder encoder;

	@Value("${keyFile}")
	private String keyFile;

	@Value("${password}")
	private String password;

	@Value("${alias}")
	private String alias;

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {

		endpoints.tokenStore(tokenStore()).accessTokenConverter(jwtAccessTokenConverter())
				.authenticationManager(authenticationManager).userDetailsService(userDetaillsService);
	}

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.inMemory().withClient("patientclient").secret(encoder.encode("1234"))
				.authorizedGrantTypes("password", "refresh_token").scopes("read", "write").accessTokenValiditySeconds(1800).resourceIds(RESOURCE_ID);
	}

	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
		security.tokenKeyAccess("permitAll");
	}
	
	@Bean
	public TokenStore tokenStore() {
		return new JwtTokenStore(jwtAccessTokenConverter());
	}

	@Bean
	public JwtAccessTokenConverter jwtAccessTokenConverter() {
		JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
		KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource(keyFile),
				password.toCharArray());
		KeyPair keyPair = keyStoreKeyFactory.getKeyPair(alias);
		jwtAccessTokenConverter.setKeyPair(keyPair);
		return jwtAccessTokenConverter;
	}
}
