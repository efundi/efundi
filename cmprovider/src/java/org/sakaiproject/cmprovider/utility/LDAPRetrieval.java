package org.sakaiproject.cmprovider.utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.component.api.ServerConfigurationService;

public class LDAPRetrieval {

    private final Log log = LogFactory.getLog(LDAPRetrieval.class);

    private ServerConfigurationService serverConfigurationService;

    private Properties ldapProperties;

    private DirContext context;

    private String baseURL;

    public LDAPRetrieval(ServerConfigurationService serverConfigurationService) {
        this.serverConfigurationService = serverConfigurationService;
        configure(getLDAPProperties());
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
    private void configure(Properties properties) throws IllegalArgumentException {
        String providerURL = properties.getProperty(Context.PROVIDER_URL);
        if (providerURL == null) {
            throw new IllegalArgumentException("Missing " + Context.PROVIDER_URL);
        }
        baseURL = providerURL;
        String baseDN = properties.getProperty("za.globed.authenticator.basedn");
        if (baseDN != null) {
            baseURL += "/" + baseDN;
        }
        String user = properties.getProperty("za.globed.authenticator.user");
        String password = properties.getProperty("za.globed.authenticator.password");
        String expiryTimeAN = properties.getProperty("za.globed.authenticator.expirytimeattr");
        if (expiryTimeAN == null) {
            throw new IllegalArgumentException("Missing za.globed.authenticator.expirytimeattr");
        }
        String loginsLeftAN = properties.getProperty("za.globed.authenticator.loginsleftattr");
        if (loginsLeftAN == null) {
            throw new IllegalArgumentException("Missing za.globed.authenticator.loginsleftattr");
        }
        String intruderattemptsAN = properties
                .getProperty("za.globed.authenticator.loginintruderattempts");
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
            }
            else {
                config.put(Context.SECURITY_AUTHENTICATION, "none");
            }
            context = new InitialDirContext(config);
        }
        catch (NamingException configError) {
            throw new IllegalArgumentException("Error configuring LDAPRetrieval.\n"
                    + configError.getExplanation());
        }
    }

    private Properties getLDAPProperties() {
        if (ldapProperties == null) {
            ldapProperties = new Properties();
            ldapProperties
                    .setProperty(Context.INITIAL_CONTEXT_FACTORY, serverConfigurationService
                            .getString(Context.INITIAL_CONTEXT_FACTORY,
                                "com.sun.jndi.ldap.LdapCtxFactory"));
            ldapProperties.setProperty(Context.PROVIDER_URL, serverConfigurationService.getString(
                Context.PROVIDER_URL, "ldap://v-ldp-lnx5.p.nwu.ac.za:389"));
            ldapProperties
                    .setProperty("za.globed.authenticator.user", serverConfigurationService
                            .getString("za.globed.authenticator.user",
                                "cn=sakaiadmin,ou=system-ids,o=nwu"));
            ldapProperties.setProperty("za.globed.authenticator.password",
                serverConfigurationService.getString("za.globed.authenticator.password",
                    "OrenAxNew6"));
            ldapProperties.setProperty("za.globed.authenticator.basedn", serverConfigurationService
                    .getString("za.globed.authenticator.basedn", "ou=users,o=nwu"));
            ldapProperties.setProperty("za.globed.authenticator.expirytimeattr",
                serverConfigurationService.getString("za.globed.authenticator.expirytimeattr",
                    "NWU-PasswordExpirationTime"));
            ldapProperties.setProperty("za.globed.authenticator.loginsleftattr",
                serverConfigurationService.getString("za.globed.authenticator.loginsleftattr",
                    "loginGraceRemaining"));
            ldapProperties.setProperty("za.globed.authenticator.loginintruderattempts",
                serverConfigurationService.getString(
                    "za.globed.authenticator.loginintruderattempts", "loginintruderattempts"));
            ldapProperties.setProperty("ldap.firstname",
                serverConfigurationService.getString("ldap.firstname", "givenName"));
            ldapProperties.setProperty("ldap.surname",
                serverConfigurationService.getString("ldap.surname", "sn"));
            ldapProperties.setProperty("ldap.email",
                serverConfigurationService.getString("ldap.email", "mail"));
        }
        return ldapProperties;
    }

    public void setRosterUserDetails(RosterUser rosterUser, String filter) throws Exception {
        List<RosterUser> rosterUsers = new ArrayList<RosterUser>();
        rosterUsers.add(rosterUser);
        setRosterUserDetails(rosterUsers, filter);
    }

    public void setRosterUserDetails(List<RosterUser> rosterUsers, String filter) throws Exception {
        try {
            NamingEnumeration<SearchResult> results = context.search(baseURL
                    + "??sub?"
                    + getRosterUserFilter(filter, rosterUsers), null);
            while (results.hasMoreElements()) {
                SearchResult element = results.nextElement();
                Attributes elementAttributes = element.getAttributes();
                Attribute userName = elementAttributes.get("cn");
                for (final RosterUser rosterUser : rosterUsers) {
                    if (isEqual(rosterUser.getUserName(), (String) userName.get())) {
                        rosterUser.setFirstName(getUserAttribute(elementAttributes,
                            (String) context.getEnvironment().get("ldap.firstname")));
                        rosterUser.setSurname(getUserAttribute(elementAttributes, (String) context
                                .getEnvironment().get("ldap.surname")));
                        rosterUser.setEmail(getUserAttribute(elementAttributes, (String) context
                                .getEnvironment().get("ldap.email")));
                        rosterUser.setFoundInLDAP(true);
                        log.info("Set LDAP info for user " + rosterUser.getUserName());
                        break;
                    }
                }
            }
            logRosterUserNotFoundWarnings(rosterUsers);
        }
        catch (Exception e) {
            log.error("An exception occurred while searching for LDAP student users", e);
            throw e;
        }
    }
    
    public static boolean isEqual(String a, String b) {
        return a == null ? b == null : a.equals(b);
    }

    private String getUserAttribute(Attributes elementAttributes, String attributeId)
            throws Exception {
        Attribute attribute = elementAttributes.get(attributeId);
        if (attribute != null) {
            return (String) attribute.get();
        }
        return null;
    }

    private String getRosterUserFilter(String memberOfFilter, List<RosterUser> rosterUsers) {
        StringBuilder filter = new StringBuilder("(&");
        filter.append(memberOfFilter);
        if (rosterUsers != null && !rosterUsers.isEmpty()) {
            filter.append("(|");
            for (RosterUser rosterUser : rosterUsers) {
                filter.append("(");
                filter.append("cn=");
                filter.append(rosterUser.getUserName());
                filter.append(")");
            }
            filter.append(")");
        }
        filter.append(")");
        return filter.toString();
    }

    private void logRosterUserNotFoundWarnings(List<RosterUser> rosterUsers) {
        for (RosterUser rosterUser : rosterUsers) {
            if (!rosterUser.isFoundInLDAP()) {
                log.warn("User with id " + rosterUser.getUserName() + " was not found in LDAP.");
            }
        }
    }

    public DirContext getContext() {
        return context;
    }
}
