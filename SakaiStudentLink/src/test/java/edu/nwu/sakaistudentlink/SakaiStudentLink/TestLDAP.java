package edu.nwu.sakaistudentlink.SakaiStudentLink;

import javax.naming.NamingEnumeration;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchResult;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.nwu.sakai.studentlink.server.LDAPAuthenticator;
import edu.nwu.sakai.studentlink.server.SettingsProperties;
import edu.nwu.sakai.studentlink.shared.LoginException;

public class TestLDAP {

	private static final LDAPAuthenticator ldapAuthenticator = new LDAPAuthenticator();

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		ldapAuthenticator.configure(SettingsProperties.getSettingProperties());
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		if (ldapAuthenticator != null && ldapAuthenticator.getBaseCtx() != null) {
			ldapAuthenticator.getBaseCtx().close();
		}
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void printAllLDAPUsersTest() {
		NamingEnumeration<SearchResult> results = null;
		InitialDirContext loginContext = null;

		System.out.println("###########################################");
		System.out.println("##  Authenticating LDAP user for login.  ##");
		System.out.println("###########################################");
		// First, resolve the username.
		// SearchResult user = getUser(username);
		String filter = SettingsProperties.getProperty(
				"ldap.user.search.filter",
				"(memberOf=cn=efundi-instructors,ou=orgunits,ou=groups,o=nwu)");
		try {
			results = ldapAuthenticator.getBaseCtx().search(
					ldapAuthenticator.getBaseURL() + "??sub?(&" + filter + ")",
					null);

			if (!results.hasMoreElements()) {
				throw new LoginException("Invalid user id.");
			}
			while (results.hasMoreElements()) {
				String username = ((SearchResult) results.nextElement())
						.getName();
				//System.out.println(username);
			}
		} catch (Exception e) {
			// e.printStackTrace();
		}

	}
}
