package edu.nwu.sakai.studentlink.server;

import java.util.Hashtable;
import java.util.Properties;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.OperationNotSupportedException;
import javax.naming.directory.Attribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchResult;

import org.apache.log4j.Logger;

import edu.nwu.sakai.studentlink.client.User;
import edu.nwu.sakai.studentlink.shared.LoginException;

/**
 * This authenticates users against an LDAP directory. Passwords could expire,
 * but may not be changes through this interface.
 * 
 * @author it3aph@puk.ac.za
 */
public class LDAPAuthenticator {

	protected static final Logger log = Logger
			.getLogger(LDAPAuthenticator.class);

	private DirContext baseCtx;

	private String baseURL;

	private String providerURL;

	private String user;

	private String password;

	private String baseDN;

	private String expiryTimeAN;

	private String loginsLeftAN;

	private String intruderattemptsAN;

	public LDAPAuthenticator() {
		baseCtx = null;
		baseURL = null;
		baseDN = null;
		user = null;
		password = null;
		providerURL = null;
		expiryTimeAN = null;
		loginsLeftAN = null;
	}

	/**
	 * The following are mandatory:
	 * <ul>
	 * <li>java.naming.provider.url - e.g. "ldap://server_name_or_ip:port"</li>
	 * <li>za.globed.authenticator.expirytimeattr - the name of the attribute
	 * for the password expiry time.</li>
	 * <li>za.globed.authenticator.loginsleftattr - the name of the attribute
	 * for the number of logins left (after expiry).</li>
	 * </ul>
	 * The following are optional:
	 * <ul>
	 * <li>java.naming.factory.initial - overrides the LDAP context factory
	 * class.</li>
	 * <li>za.globed.authenticator.basedn - base DN limiting the searchable area
	 * of the tree.</li>
	 * </ul>
	 * 
	 * @param properties
	 *            see above for descriptions.
	 */
	public void configure(Properties properties)
			throws IllegalArgumentException {
		providerURL = properties.getProperty(Context.PROVIDER_URL);
		if (providerURL == null) {
			throw new IllegalArgumentException("Missing "
					+ Context.PROVIDER_URL);
		}
		baseURL = providerURL;
		baseDN = properties.getProperty("za.globed.authenticator.basedn",
				"ou=users,o=nwu");
		if (baseDN != null) {
			baseURL += "/" + baseDN;
		}
		user = properties.getProperty("za.globed.authenticator.user",
				"cn=sakaiadmin,ou=system-ids,o=nwu");
		password = properties.getProperty("za.globed.authenticator.password",
				"OrenAxNew6");
		expiryTimeAN = properties.getProperty(
				"za.globed.authenticator.expirytimeattr",
				"NWU-PasswordExpirationTime");
		if (expiryTimeAN == null) {
			throw new IllegalArgumentException(
					"Missing za.globed.authenticator.expirytimeattr");
		}
		loginsLeftAN = properties
				.getProperty("za.globed.authenticator.loginsleftattr",
						"loginGraceRemaining");
		if (loginsLeftAN == null) {
			throw new IllegalArgumentException(
					"Missing za.globed.authenticator.loginsleftattr");
		}
		intruderattemptsAN = properties.getProperty(
				"za.globed.authenticator.loginintruderattempts",
				"loginintruderattempts");
		if (intruderattemptsAN == null) {
			throw new IllegalArgumentException(
					"Missing za.globed.authenticator.loginintruderattempts");
		}
		// Bind to test the configuration - re-used for future searches.
		try {
			Properties config = new Properties();
			config.putAll(properties);
			if ((user != null) && (user != "")) {
				config.put(Context.SECURITY_PRINCIPAL, user);
				config.put(Context.SECURITY_CREDENTIALS, password);
				config.put(Context.SECURITY_AUTHENTICATION, "simple");
			} else {
				config.put(Context.SECURITY_AUTHENTICATION, "none");
			}
			baseCtx = new InitialDirContext(config);
		} catch (NamingException configError) {
			throw new IllegalArgumentException(
					"Error configuring LDAP authenticator.\n"
							+ configError.getExplanation());
		}
	}

	/**
	 * Attempts to resolve the username by searching the whole tree from the
	 * root down before authenticating.
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings( { "rawtypes", "unchecked" })
	public boolean isValidLogon(String username, String password)
			throws Exception {
		NamingEnumeration<SearchResult> results = null;
		InitialDirContext loginContext = null;
		try {
			System.out.println("###########################################");
			System.out.println("##  Authenticating LDAP user for login.  ##");
			System.out.println("###########################################");
			// First, resolve the username.
			// SearchResult user = getUser(username);
			String filter = SettingsProperties
					.getProperty("ldap.user.search.filter",
							"(memberOf=cn=efundi-instructors,ou=orgunits,ou=groups,o=nwu)");
			results = baseCtx.search(baseURL + "??sub?(&(cn=" + username + ")"
					+ filter + ")", null);
			if (!results.hasMoreElements()) {
				throw new LoginException("Invalid user id.");
			}
			while (results.hasMoreElements()) {
				username = ((SearchResult) results.nextElement()).getName();
				username = addBaseDN(username);
				if (log.isDebugEnabled())
					log.debug("Resolved username to " + username);
			}
			// Now, re-bind with the specified credentials.
			log.info("Attempt to rebind to directory with username: "
					+ username);
			Hashtable env = baseCtx.getEnvironment();
			env.put(Context.SECURITY_PRINCIPAL, username);
			env.put(Context.SECURITY_CREDENTIALS, password);
			env.put(Context.SECURITY_AUTHENTICATION, "simple");
			loginContext = new InitialDirContext(env);
			System.out.println("###########################################");
			System.out.println("##  LDAP authentication successful!      ##");
			System.out.println("###########################################");
			return true;
		} catch (AuthenticationException invalidLogon) {
			System.out.println("###########################################");
			System.out.println("##  LDAP authentication failed!   Auth   ##");
			System.out.println("###########################################");
			throw new LoginException("Invalid password.", invalidLogon);
		} catch (OperationNotSupportedException invalidLogon) {
			// normally exceeded max. no of sessions
			System.out.println("###########################################");
			System.out.println("##  LDAP authentication failed!   Ops    ##");
			System.out.println("###########################################");
			throw new LoginException(
					"Error performing lookup against LDAP directory.",
					invalidLogon);
		} catch (NamingException error) {
			System.out.println("###########################################");
			System.out.println("##  LDAP authentication failed!     Name ##");
			System.out.println("###########################################");
			throw new LoginException(
					"Error performing lookup against LDAP directory.", error);
		} finally {
			if (results != null) {
				results.close();
			}
			if (loginContext != null) {
				loginContext.close();
			}
		}
	}

	/**
	 * @param originalDN
	 *            may not be null.
	 * @return the original DN with the baseDN appended.
	 */
	private String addBaseDN(String originalDN) {
		if (baseDN == null) {
			return originalDN;
		} else {
			// TODO it3aph: Need to reverse the baseDN if it's more than one.
			return originalDN + "," + baseDN;
		}
	}

	/**
	 * @return The given attribute value of the user.
	 * @throws Exception
	 */
	public String getUserAttribute(String username, String attribute)
			throws Exception {
		try {
			// Find the user.
			SearchResult result = getUser(username);
			if (log.isDebugEnabled())
				log.debug("Resolved username to " + result.getName());
			Attribute att = result.getAttributes().get(attribute);
			if (att == null) {
				return null;
			} else {
				return (String) att.get();
			}
		} catch (Exception error) {
			throw new LoginException(
					"Invalid username. Error performing lookup against LDAP directory.",
					error);
		}
	}

	public User getPopulatedUser(String username) throws Exception {
		NamingEnumeration<SearchResult> results = null;
		try {
			User user = new User();
			SearchResult result = getUser(username);
			user.setUserName(username);
			user.setFirstName(getAttributeValue(result, SettingsProperties
					.getProperty("ldap.firstname", "initials")));
			user.setSurname(getAttributeValue(result, SettingsProperties
					.getProperty("ldap.surname", "sn")));
			user.setEmail(getAttributeValue(result, SettingsProperties
					.getProperty("ldap.email", "mail")));
			return user;
		} catch (Exception e) {
			// Become user username entered, not valid / not found
			return null;
		} finally {
			if (results != null) {
				results.close();
			}
		}
	}

	private SearchResult getUser(String username) throws Exception {
		NamingEnumeration<SearchResult> results = null;
		try {
			String filter = SettingsProperties
					.getProperty("ldap.user.search.filter",
							"(memberOf=cn=efundi-instructors,ou=orgunits,ou=groups,o=nwu)");
			results = baseCtx.search(baseURL + "??sub?(&(cn=" + username + ")"
					+ filter + ")", null);
			// Didn't find the user.
			if (!results.hasMoreElements()) {
				throw new LoginException("Invalid username.");
			}
			return results.next();
		} finally {
			if (results != null) {
				results.close();
			}
		}
	}

	public String getAttributeValue(SearchResult userResult, String attribute)
			throws LoginException {
		try {
			Attribute att = userResult.getAttributes().get(attribute);
			if (att == null) {
				return null;
			} else {
				return (String) att.get();
			}
		} catch (Exception error) {
			throw new LoginException(
					"Invalid username. Error performing lookup against LDAP directory.",
					error);
		}
	}

	public DirContext getBaseCtx() {
		return baseCtx;
	}

	public String getBaseURL() {
		return baseURL;
	}
}