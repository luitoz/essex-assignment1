package security;

import java.util.HashMap;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

/**
 * <p>
 * This sample LoginModule authenticates users with a password.
 *
 * <p>
 * This LoginModule only recognizes one user: testUser
 * <p>
 * testUser's password is: testPassword
 *
 * <p>
 * If testUser successfully authenticates itself, a <code>SamplePrincipal</code>
 * with the testUser's user name is added to the Subject.
 *
 * <p>
 * This LoginModule recognizes the debug option. If set to true in the login
 * Configuration, debug messages will be output to the output stream,
 * System.out.
 *
 */
public class Authenticator implements LoginModule {

	// initial state
	private Subject subject;
	private CallbackHandler callbackHandler;
//    private Map sharedState;
//    private Map options;

	// configurable option
	private boolean debug = false;

	// the authentication status
	private boolean succeeded = false;
	private boolean commitSucceeded = false;

	// username and password
	private String username;
	private String password;

	// testUser's SamplePrincipal
	private User userPrincipal;

	/**
	 * Initialize this <code>LoginModule</code>.
	 *
	 * @param subject         the <code>Subject</code> to be authenticated.
	 *                        <p>
	 *
	 * @param callbackHandler a <code>CallbackHandler</code> for communicating with
	 *                        the end user (prompting for user names and passwords,
	 *                        for example).
	 *                        <p>
	 *
	 * @param sharedState     shared <code>LoginModule</code> state.
	 *                        <p>
	 *
	 * @param options         options specified in the login
	 *                        <code>Configuration</code> for this particular
	 *                        <code>LoginModule</code>.
	 */
	public void initialize(Subject subject, CallbackHandler callbackHandler, Map<java.lang.String, ?> sharedState,
			Map<java.lang.String, ?> options) {

		this.subject = subject;
		this.callbackHandler = callbackHandler;
//        this.sharedState = sharedState;
//        this.options = options;

		// initialize any configured options
		debug = "true".equalsIgnoreCase((String) options.get("debug"));
	}

	/**
	 * Authenticate the user by prompting for a user name and password.
	 *
	 * @return true in all cases since this <code>LoginModule</code> should not be
	 *         ignored.
	 *
	 * @exception FailedLoginException if the authentication fails.
	 *                                 <p>
	 *
	 * @exception LoginException       if this <code>LoginModule</code> is unable to
	 *                                 perform the authentication.
	 */
	public boolean login() throws LoginException {

		// prompt for a user name and password
		if (callbackHandler == null)
			throw new LoginException(
					"Error: no CallbackHandler available " + "to garner authentication information from the user");

		Callback[] callbacks = new Callback[2];
		callbacks[0] = new NameCallback("user name: ");
		callbacks[1] = new PasswordCallback("password: ", false);

		try {
			callbackHandler.handle(callbacks);
			username = ((NameCallback) callbacks[0]).getName();
			char[] tmpPassword = ((PasswordCallback) callbacks[1]).getPassword();
			if (tmpPassword == null) {
				// treat a NULL password as an empty password
				tmpPassword = new char[0];
			}
			password = new String(tmpPassword);
			((PasswordCallback) callbacks[1]).clearPassword();

		} catch (java.io.IOException ioe) {
			throw new LoginException(ioe.toString());
		} catch (UnsupportedCallbackException uce) {
			throw new LoginException("Error: " + uce.getCallback().toString()
					+ " not available to garner authentication information " + "from the user");
		}

		// print debugging information
		if (debug) {
			System.out.println("\t\t[SampleLoginModule] " + "user entered user name: " + username);
			System.out.println("\t\t[SampleLoginModule] " + "user entered password: " + password);
		}
		Map<String, String> users = new HashMap<String, String>();
		users.put("testUser", "testPassword");
		users.put("testAdmin", "testPassword");

		// verify the username/password
		boolean usernameCorrect = false;
		boolean passwordCorrect = false;
		if (users.get(username) != null)
			usernameCorrect = true;
		if (usernameCorrect && users.get(username).equals(password)) {
			// authentication succeeded!!!
			passwordCorrect = true;
			if (debug)
				System.out.println("\t\t[SampleLoginModule] " + "authentication succeeded");
			succeeded = true;
			return true;
		} else {

			// authentication failed -- clean out state
			if (debug)
				System.out.println("\t\t[SampleLoginModule] " + "authentication failed");
			succeeded = false;
			username = null;
			password = null;
			if (!usernameCorrect) {
				throw new FailedLoginException("User Name Incorrect");
			} else {
				throw new FailedLoginException("Password Incorrect");
			}
		}
	}

	/**
	 * This method is called if the LoginContext's overall authentication succeeded
	 * (the relevant REQUIRED, REQUISITE, SUFFICIENT and OPTIONAL LoginModules
	 * succeeded).
	 *
	 * If this LoginModule's own authentication attempt succeeded (checked by
	 * retrieving the private state saved by the <code>login</code> method), then
	 * this method associates a <code>SamplePrincipal</code> with the
	 * <code>Subject</code> located in the <code>LoginModule</code>. If this
	 * LoginModule's own authentication attempted failed, then this method removes
	 * any state that was originally saved.
	 *
	 * @exception LoginException if the commit fails.
	 *
	 * @return true if this LoginModule's own login and commit attempts succeeded,
	 *         or false otherwise.
	 */
	public boolean commit() throws LoginException {
		if (succeeded == false) {
			return false;
		} else {
			// add a Principal (authenticated identity)
			// to the Subject

			// assume the user we authenticated is the SamplePrincipal
			userPrincipal = new User(username);
			if (!subject.getPrincipals().contains(userPrincipal))
				subject.getPrincipals().add(userPrincipal);

			if (debug) {
				System.out.println("\t\t[SampleLoginModule] " + "added SamplePrincipal to Subject");
			}

			// in any case, clean out state
			username = null;
			password = null;

			commitSucceeded = true;
			return true;
		}
	}

	/**
	 * This method is called if the LoginContext's overall authentication failed.
	 * (the relevant REQUIRED, REQUISITE, SUFFICIENT and OPTIONAL LoginModules did
	 * not succeed).
	 *
	 * If this LoginModule's own authentication attempt succeeded (checked by
	 * retrieving the private state saved by the <code>login</code> and
	 * <code>commit</code> methods), then this method cleans up any state that was
	 * originally saved.
	 *
	 * @exception LoginException if the abort fails.
	 *
	 * @return false if this LoginModule's own login and/or commit attempts failed,
	 *         and true otherwise.
	 */
	public boolean abort() throws LoginException {
		if (succeeded == false) {
			return false;
		} else if (succeeded == true && commitSucceeded == false) {
			// login succeeded but overall authentication failed
			succeeded = false;
			username = null;
			if (password != null) {
				password = null;
			}
			userPrincipal = null;
		} else {
			// overall authentication succeeded and commit succeeded,
			// but someone else's commit failed
			logout();
		}
		return true;
	}

	/**
	 * Logout the user.
	 *
	 * This method removes the <code>SamplePrincipal</code> that was added by the
	 * <code>commit</code> method.
	 *
	 * @exception LoginException if the logout fails.
	 *
	 * @return true in all cases since this <code>LoginModule</code> should not be
	 *         ignored.
	 */
	public boolean logout() throws LoginException {

		subject.getPrincipals().remove(userPrincipal);
		succeeded = false;
		succeeded = commitSucceeded;
		username = null;
		if (password != null) {
			password = null;
		}
		userPrincipal = null;
		return true;
	}
}
