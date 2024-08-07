#### 1. OAuth 2.0 -> open authorization version 2


#### 2. OAuth 2.0 Roles

    - Resource owner : A user that is trying to access the information

    - Client: Client is an application that access the information on user behalf

    - Resource server: Hosting user data

    - Authorization server: Provides access token to client application after successful authenticating the resource owner,

        google, fb and twitter all have a authorization server. we can have our own authorization server as well like keycloak

#### 3. There are 2 types of clients
    - Confidential
    - Public

#### 4. Conidential clients securely stores client secret key where as there is no key for the public clients

#### 5. Both the clients have different ways to authenticate itself with the authentication server

#### 6. Access token
    - When the resource user authenticates the client application using the authentication server. the authentication server provides a access token to the client applicaition
    - Then the client application sends this access token in the request to the resource server to access the data
    - Resource server validates the access token with the authentication server that it is valid and gets the user details from the authentication server

#### 7. There are 2 types of access token
    - Self container type: Token itself contains the user information that the resource server can use in JSON format
    - Identifier type : used just for the validity of the user, then the resource server can use this token to get the user information from the authorization server. Authorization server stores the mapping of the token with the data to share with the resource

#### 8. Self container token can be divided into 3 sections
    - Header section
    - Payload
    - Signature section

#### 9. OpenID connect
    - When an client gets authenticate with the authorization server, authorization server does not provide any information about the user to the client application
    - OpenID connect helps in this scenario
    - OpenID connect sits on top of the authorization server makes it Identity provider
    - Authorization server on authentication provides an access token only but with OpenID connect it additionally provides an ID token
    - ID token contains the user information that the client application can use

#### 10. Grant Type
     - It is a way an applicaiton gets an access token
     - Different types of client application uses different types of Grant type to get an access token
     - Types
        - Authorization code Grant type for Service side web Application (when application can handle the client secret key securely)
        - Client credentials Grant type for Server side script with no UI
        - PKCE Enhanced authorization code Grant type for Javascript Single page application (when application cannot handle the client secret key serurely)
        - Device code Grant type for different devices like Gaming console

     - Depricated grant types
        - Implicit Flow Grant type
        - password Grant type - client application collects the user credentials and sends to the authorization server

#### 11. Authorization code Grant
    - When you visit any website and try to login using google or faceboook
    - Client application (website) redirects to the facebook authorization page
    - The request sent to authorization server from client contains few value
        - GET type of request
        - ?response_type=code  (to get authorization code) - Mandatory
        - ?state=<some-random-string>  (generated by client application, response also comes with the same state value. Client compares both to check it is the right response) - recommended
        - ?redirect_uri=<rediret-uri> (the uri where the authorization server will redirect when authorization is completed, client must be able to handle the redirect for example on a different page of the client application) - Optional
        - ?scope=<scopes> (defines the type of information that the client application will access on behalf of the user) - Optional
        - ?client_id=<clientId> (Id of the client application) - Mandatory
    - When the authorization server received the request it opens a login form
    - When user authenticates itself then the authorization server generates a short lived authorization_code
    - Attached it with the redirect_uri along with the state as a query parameter
        <redirect-uri>?code=34dwsw3as&state=asdq23421dassad
    - Then the client application sends a new request to the authorization server with the authorization code to get the access token
    - Request structure
```
        - POST https://blabla.com/token
            ?grant_type=authorization_code
            &code=<Same-code-as-we-received>
            &redirect_uri=<same-as-the-first-request>
            &client_id=<client-id>
            &client_secret=<client-secret>
```
    - Authorization server responds with an access token and a refresh token
    - User then can request the resource from a resource server with that access token in the request header
    - Resource server validates the access token with the authorization server

#### 12. PKCE enhanced Authorization code Grant
    - PKCE - Proof key for code exchange (permounced as Pixie)
    - It is very similar to the authorization_code grant flow, it provides extra level of security
    - It is used for the applications that does not store the client_secret for example javascript single page application
    - In the initial request to the authorization server from the client application, we needs to send 2 extra query params
    - Request structure
```

        - GET type of request
        - ----- all parameters same as the authorization_code -----
        - ?code_challenge=<base-64 encryped-value> : Client application generates it
        - &code_challenge_method=S256 : needs to be configured in the authorization server (s256 is recommeded value)
```
    - To generate the code_challenge value, the client application first needs to generate the code_verifier value and from that it derives the code_challenge value
    - code_challenge_method can have 2 different value : S256 or plain 
        - S256 - related to the encrytion of the code_challenge value
        - plain means not encryped (not secure)
    - Once the authorization server generates the authorization code then in the subsequent request where we sends the authorization_code back to the authorization server, we need to send code_verifier as well
    - Authorization server validates both the authorization_code and code_verifier enerbefore generating the access_token

#### 13. Generating code verifier value
    - It needs to be a random 33 to 128 character alpha-numeric string
    - Needs to be Base64 encoded
    - Using the SecureRandom class
        SecureRandom() secureRandom = new SecureRandom();
        byte[] codeverifier = new byte[32];
        secureRandom.nextBytes(codeVerifier);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(codeverifier);

#### 14. Generating code challenge value
    - code_challenge = BASE64URL-ENCODE(SHA256(ASII(code_verifier)))
    - code in java
```
        byte[] bytes = codeVerifier.getBytes("US-ASCII");
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        messageDigest.update(bytes,0,bytes.length);
        byte[] digent = messageDigest.digest();
        return Base64.getUrlEncoder().pwithoutPadding().encodeToString(digest);
```
    
#### 15. Client credentials Grant
    - Used when one application needs to send request to another application
    - No user involved, no login page
    - Machine to machine communication example two microserices
    - Client application directly sends the request for access token to authorization server
    - Request
```    
        - POST request with headers defined below
        - grant_type="client_credentials"
        - scope=<scope>
        - client_id=<client-id>
        - client_secret=<client-secret>
```

#### 16. Password Grant
    - Should only be used when client application does not support redirect
    - If the application is secure and user is ready to share the credentials with the client application
    - Flow
        - Used shares username and password with the client application (custom login page in client application)
        - Client application sends the request to the authorization server to directly get the access token
            - request contains
                - grant_type="password"
                - username=<username>
                - password=<password>
                - client_id=<client-id>
                - client_secret=<client-secret>
        - The authorization server on validation shares access token with the client application

#### 17. Refreshing token
    - There is a TTL associated with every token
    - grant_type should be refresh_token
    - When the access token gets expired, the client application sends a new request to the authorization server with the refresh token it has to get a new access token
    - Authorization server returns an access token and optioanlly a new refresh token
    - There are 2 types of refresh token
        - With an expiration time, that expires
        - Without an expiration time, never expires (in this case the expires_in time will be 0)
        - Need to add "offline_access" in scope to get a refresh token that never expires

#### 18. Keycloak
    - Open source Identity and access management solution 
    - Supports Single-Sign On (SSO)    
    - Social login (Facebook, twitter, etc)
    - User federation (LDAP)
 
#### 19. Download key clock
 
#### 20. Start kekcloak from bin folder sh ./kc.sh start-dev (development-mode)
    - In development mode - sh ./kc.sh start-dev
    - In Production mode - sh ./kc.sh start
    - to run on a different port sh ./kc.sh start-dev --http-port=8081 (default is 8080)
 
#### 21. Create an admin user on the front page of the keycloak
 
#### 22. Creating a new Realm
    - Reams is a section where we can define a set of users, their roles and their credentials
    - we can have multiple realm in an authorization server
    - a new realm is created from the side menu by clicking on the master realm -> create new realm button

#### 23. Client ID and Client secret can be found in Realm -> client -> client credentials


------------------ CODE SNIPPETS --------------------

#### 24. To make a spring boot component a resource server, add a resource-server dependency:
```
    	<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
	</dependency>
```

#### 25. Not by default every end point of the component will expect an access token in all the rest endpoint request, if not present will fail with error 401 Unauthorized

#### 26. To attach an authorization server with the resource server, add an authorization server path in the config file like
    spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://localhost:8080/realms/appsdeveloperblog/protocol/openid-connect/certs

#### 27. To access a JWT token in the resource server or to return the content of it use AuthenticationPrincipal annotation
    It will populte the Jwt object with the content of the token and that we can use to see token and claims
```
        @GetMapping("/token")
        public Map<String, Object> getToken(@AuthenticationPrincipal Jwt jwt) {
            return Collections.singletonMap("principal", jwt);
        }
```

#### 28. To send a valid request to the resource server add a header "Authorization" as key with "Bearer <access-token>" as value       

---------------------------------------------------------------------------------------------------------

#### 29. Scope based access control
    - Scope decides the information that the authorization server will share with the resource-server through the access token
    - scope openId means the client application is requesting an ID_token with the access token
    - profile means the client application is requesting the user profile like username, lastname, given_name, gender, website, etc
    - email means the email id of the user
    - other scopes are phone, address, offline_access (refresh token with no expiration date)
    - we can have custom scopes as well

#### 30. Role based access control
    - Roles are entities having a collection of authorities

--------------------------- CODE SNIPPETS --------------------------------------------------------

#### 31. We can filter the request before it reaches the Controller class
    - We can add conditions like : User must have a specific scope to access an end point
```    
        @Bean
        SecurityFilterChain configure(HttpSecurity http) throws Exception {
            http.authorizeHttpRequests(authz ->
                            authz
                                    .requestMatchers(HttpMethod.GET, "/users/status/check").hasAuthority("SCOPE_test")
                                    .anyRequest()
                                    .authenticated())
                    .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwtConfigurer -> {}));
            return http.build();
        }
```

    - We can add condition like : User must have a role or have one of the following roles
```
        @Bean
        SecurityFilterChain configure(HttpSecurity http) throws Exception {
            JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
            jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KeycloakRoleConverter());

            http.authorizeHttpRequests(authz ->
                            authz
                                    .requestMatchers(HttpMethod.GET, "/users/status/check")
                                    //.hasRole("developer")
                                    .hasAnyRole("developer", "tester")
                                    .anyRequest()
                                    .authenticated())
                    .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)));
            return http.build();
        }
```

#### 32. Method/Class level Security
    - We can add annotations on the class or methods where we define the authority (who can access it)
    - Need to add @EnableMethodSecurity(securedEnabled=true, prePostEnabled=true) to a configuration class (Any class with @Configuration annotation)
    - We can use @Secured annotation on a method like
```
            @Secured("ROLE_developer")
            @DeleteMapping(path = "/{id}")
            public String deleteUser(@PathVariable String id, @AuthenticationPrincipal Jwt jwt) {
                return "Deleted user with id " + id + " and JWT subject " + jwt.getSubject();
            }
```
    - We can use different annotation to do the same thing, the difference is in this annotation we can pass the expression (who can access the method)
    - Expression example if the user has the given authority or the id passed as a path variable matches the id present in the access token
```
        @PreAuthorize("hasAuthority('ROLE_developer') or #id == #jwt.subject")
        @DeleteMapping(path = "/{id}")
        public String deleteUser(@PathVariable String id, @AuthenticationPrincipal Jwt jwt) {
            return "Deleted user with id " + id + " and JWT subject " + jwt.getSubject();
        }
```

#### 33. PostAuthorize annotation
    - Enable it using the same @EnablemethodSecurity annotation
```    
        - @EnableMethodSecurity(securedEnabled=true, prePostEnabled=true)
```
    - PostAuthorize annotation can be added on top of a method like
```
        - @PostAuthorize("returnObject.id == #jwt.subject")
```
    - PostAuthorize first immplements the method and then try to satsfy its condition
    - It has access to the return object of the method

#### 34. Eureka discovery
    - Helps discover different components of the application
    - Finding the IP address and port numbers
    - Each component register itself in Eureka
    - There is an another component between the client and the Eureka that is API Gateway
    - Cliend sends the request to API gateway, API gateway fetches address of all the components from Eureka
    - Then it redirects the request to right component
    - API gateway also works as a load balancer

#### 35. Creating Eureka Service discovery component
    - First create a new component with Eureka server and Eureka Client dependencies

```
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
		</dependency>
```
    - Then in the Application Elecutable class add @EnableEurekaServer annotaiton
    - If we have a single instance of Eureka then add the below config

``` 
        eureka.client.registerWithEureka=false
        eureka.client.fetchRegistry=false

        logging.level.com.netflix.eureka=OFF
        logging.level.com.netflix.discovery=OFF   
```

#### 36. Regisering a component in Eureka Service discovery
    - Each component that gets registered in Eureka is a client for Eureka discovery service
    - Need to add Eureka client dependency
```
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
		</dependency>
```        
    - Add @EnableDiscoveryClient in the application main class
    - Update the property file to pass the Eureka Discovery service end point

```
    eureka.client.serviceUrl.defaultZone = http://localhost:8010/eureka
```
#### 37. Load balancer
    - So we can run multiple instances of a resource server
    - all the resource server will regiter them selves on the Eureka server
    - Spring cloud API gateway can fetch all the registered resource servers from the Eureka server
    - And can behave as a load balancer client (it will pass the incoming requests to resource servers)

#### 38. Runnning multiple instances of same component
    - Server port number must be different for each instance
    - So we have to give random port numbers to each instances (dont know how many instances will be created)
    - to start instance on random port 
        - server.port=0


#### 39. Registering multiple instances of same component in Eureka
    - Need to provide Eureka instance id in all the clients (resource servers)
        eureka.instance.instance-id = ${spring.application.name}:${instanceId:${random.value}}
    - We can run from intelliJ or from terminal like
        - mvn spring-boot:run -Dspring-boot.run.arguments=--instanceId=abc

#### Configuring API gateway to fetch data from Eureka
    - Need to configure it as Eureka client (just like any resource server)

    
#### Client application
    - To fetch the id token details in one of the controller class we can add the following in a method
        - @AuthenticationPrincipal OidcUser principal

```
    @GetMapping("/albums")
    public String getAlbums(Model model, @AuthenticationPrincipal OidcUser principal) {

        OidcIdToken idToken = principal.getIdToken();
        String tokenValue = idToken.getTokenValue();
    }
```    
    - Need to update the configuration of the client application as mentioned in the photo-app-web-client application

#### Web Client 
    - Web client from spring webflux can be used to send the requests from the client with access token to resource servers
    - Need to add dependencies of that
```
		<dependency>
			<groupId>org.springframework</groupId>
			<artifactId>spring-webflux</artifactId>
		</dependency>

		<dependency>
			<groupId>io.projectreactor.netty</groupId>
			<artifactId>reactor-netty</artifactId>
		</dependency>
```
    - Need to create a bean of Web client in main application java class to add the access token to all the requests
```
	@Bean
	public WebClient webClient(ClientRegistrationRepository clientRegistrationrepository,
							   OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository) {
		ServletOAuth2AuthorizedClientExchangeFilterFunction oauth2 =
				new ServletOAuth2AuthorizedClientExchangeFilterFunction(clientRegistrationrepository,
						oAuth2AuthorizedClientRepository);

		oauth2.setDefaultOAuth2AuthorizedClient(true);

		return WebClient.builder().apply(oauth2.oauth2Configuration()).build();
	}
```
    - Now autowire it in controller class and use the webclient to send requests
```
    @Autowired
    WebClient webClient;


    @GetMapping("/albums")
    public String getAlbums(Model model, @AuthenticationPrincipal OidcUser principal) {

        String url = "http://localhost:8099/albums";

        List<AlbumsDto> albums = webClient.get().uri(url).retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<AlbumsDto>>(){}).block();

        model.addAttribute("albums", albums);
        return "albums";
    }

```

#### OAuth 2.0 Social login
    - First create a component as a OAuth2 client 
    - With main dependency
```

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-oauth2-client</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>
```
    - when we add OAuth2 client dependency, by default all the controller class needs an access token to 
    allow excess to the end point but we can override this using WebSecurityConfigurationAdapter
    (check code of WebSecurity.java class in social-login-webclient)
    - we dont need to provide providers details for google, fb as they are well known (endpoints in application.properties)
    - we have to provide the client-id and client secret of the identity server
        -like for google
```
spring.security.oauth2.client.registration.google.client-id =  
spring.security.oauth2.client.registration.google.client-secret =  
```
        - for facebook
```
spring.security.oauth2.client.registration.facebook.client-id =  
spring.security.oauth2.client.registration.facebook.client-secret =  

```

    - to get the client-if and client-secret from say facebook
        - need to login in developer.facebook.com
        - Go to MyApps
        - Create an App
        - Select option "For everything else"
        - then go inside the newly created App
        - Go to settings
        - there App Id and App Secret can be found
    - to get it from google
        - need to login in https://console.cloud.google.com/welcome?project=lunar-ensign-136404

#### Okta
    - It is also an access and Identity provider
    - It is a cloud based solution as compare to KeyKloak that is a opensource solution (docker based)

#### Implementing logout
    - Need to set the redirect path and session. authentication setting like:

```
  @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http.authorizeHttpRequests(authorizeRequests ->
                            authorizeRequests
                                    .requestMatchers(new AntPathRequestMatcher("/"))
                                    .permitAll()
                                    .anyRequest()
                                    .authenticated()
                    )
                    .oauth2Login(Customizer.withDefaults())
                    .logout(logout -> logout
                            .logoutSuccessHandler(oidcLogoutSuccessHandler())
                            .invalidateHttpSession(true)
                            .clearAuthentication(true)
                            .deleteCookies("JSESSIONID")
                    );

            return http.build();
        }

        private OidcClientInitiatedLogoutSuccessHandler oidcLogoutSuccessHandler() {
            OidcClientInitiatedLogoutSuccessHandler successHandler = new OidcClientInitiatedLogoutSuccessHandler(
                    clientRegistrationRepository);
            successHandler.setPostLogoutRedirectUri("http://localhost:8099/");
            return successHandler;
        }
```
    - Here we are setting to clear the session, authenticated user and adding the redirect URL to be called on logout
    - we can provide an endpoint on GUI with "/logout" and the OAuth will map the logout to this method
```
        <div>
            <a href="/logout">Logout</a>
        </div>
```