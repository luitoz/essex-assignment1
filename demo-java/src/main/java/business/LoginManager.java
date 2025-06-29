package business;

import java.io.*;
import java.util.*;
import javax.security.auth.login.*;
import javax.security.auth.*;
import javax.security.auth.callback.*;

/**
 * This Sample application attempts to authenticate a user and reports whether
 * or not the authentication was successful.
 */
public class LoginManager {
	public void handleLogin() {
		// Obtain a LoginContext, needed for authentication. Tell it
		// to use the LoginModule implementation specified by the
		// entry named "Sample" in the JAAS login configuration
		// file and to also use the specified CallbackHandler.
		LoginContext lc = null;
		try {
			lc = new LoginContext("Sample2", new MyCallbackHandler());
		} catch (LoginException le) {
			System.err.println("Cannot create LoginContext. " + le.getMessage());
//			le.printStackTrace();
			System.exit(-1);
		} catch (SecurityException se) {
			System.err.println("Cannot create LoginContext. " + se.getMessage());
//			se.printStackTrace();
			System.exit(-1);
		}

		// the user has 3 attempts to authenticate successfully
		int i;
		for (i = 0; i < 3; i++) {
			try {

				// attempt authentication
				lc.login();

				// if we return with no exception, authentication succeeded
				break;

			} catch (LoginException le) {

				System.err.println("Authentication failed:");
				System.err.println("  " + le.getMessage());
//				le.printStackTrace();
				try {
					Thread.currentThread().sleep(3000);
				} catch (Exception e) {
					// ignore
				}

			}
		}

		// did they fail three times?
		if (i == 3) {
			System.out.println("Sorry");
			System.exit(-1);
		}

		System.out.println("Authentication succeeded!");
	}
}

/**
 * The application implements the CallbackHandler.
 *
 * <p>
 * This application is text-based. Therefore it displays information to the user
 * using the OutputStreams System.out and System.err, and gathers input from the
 * user using the InputStream System.in.
 */
class MyCallbackHandler implements CallbackHandler {

	/**
	 * Invoke an array of Callbacks.
	 *
	 * <p>
	 *
	 * @param callbacks an array of <code>Callback</code> objects which contain the
	 *                  information requested by an underlying security service to
	 *                  be retrieved or displayed.
	 *
	 * @exception java.io.IOException          if an input or output error occurs.
	 *                                         <p>
	 *
	 * @exception UnsupportedCallbackException if the implementation of this method
	 *                                         does not support one or more of the
	 *                                         Callbacks specified in the
	 *                                         <code>callbacks</code> parameter.
	 */
	public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {

		for (int i = 0; i < callbacks.length; i++) {
			if (callbacks[i] instanceof TextOutputCallback) {

				// display the message according to the specified type
				TextOutputCallback toc = (TextOutputCallback) callbacks[i];
				switch (toc.getMessageType()) {
				case TextOutputCallback.INFORMATION:
					System.out.println(toc.getMessage());
					break;
				case TextOutputCallback.ERROR:
					System.out.println("ERROR: " + toc.getMessage());
					break;
				case TextOutputCallback.WARNING:
					System.out.println("WARNING: " + toc.getMessage());
					break;
				default:
					throw new IOException("Unsupported message type: " + toc.getMessageType());
				}

			} else if (callbacks[i] instanceof NameCallback) {

				// prompt the user for a username
				NameCallback nc = (NameCallback) callbacks[i];

				System.err.print(nc.getPrompt());
				System.err.flush();
				nc.setName((new BufferedReader(new InputStreamReader(System.in))).readLine());

			} else if (callbacks[i] instanceof PasswordCallback) {

				// prompt the user for sensitive information
				PasswordCallback pc = (PasswordCallback) callbacks[i];
				System.err.print(pc.getPrompt());
				System.err.flush();
				pc.setPassword(System.console().readPassword());

			} else {
				throw new UnsupportedCallbackException(callbacks[i], "Unrecognized Callback");
			}
		}
	}
}
