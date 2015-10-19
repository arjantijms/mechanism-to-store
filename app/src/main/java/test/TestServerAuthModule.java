package test;

import static javax.security.auth.message.AuthStatus.SEND_SUCCESS;
import static javax.security.auth.message.AuthStatus.SUCCESS;
import static javax.security.identitystore.CredentialValidationResult.Status.VALID;

import java.io.IOException;
import java.security.Principal;
import java.util.Map;

import javax.enterprise.inject.spi.CDI;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.message.AuthException;
import javax.security.auth.message.AuthStatus;
import javax.security.auth.message.MessageInfo;
import javax.security.auth.message.MessagePolicy;
import javax.security.auth.message.callback.CallerPrincipalCallback;
import javax.security.auth.message.callback.GroupPrincipalCallback;
import javax.security.auth.message.module.ServerAuthModule;
import javax.security.identitystore.CredentialValidationResult;
import javax.security.identitystore.IdentityStore;
import javax.security.identitystore.credential.Password;
import javax.security.identitystore.credential.UsernamePasswordCredential;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Very basic SAM that grabs a caller name and password from the request and
 * delegates to a JSR 375 identity store using CDI.
 * 
 */
public class TestServerAuthModule implements ServerAuthModule {

    private CallbackHandler handler;
    private Class<?>[] supportedMessageTypes = new Class[] { HttpServletRequest.class, HttpServletResponse.class };

    @Override
    public void initialize(MessagePolicy requestPolicy, MessagePolicy responsePolicy, CallbackHandler handler, @SuppressWarnings("rawtypes") Map options) throws AuthException {
        this.handler = handler;
    }

    @Override
    public AuthStatus validateRequest(MessageInfo messageInfo, Subject clientSubject, Subject serviceSubject) throws AuthException {

        HttpServletRequest request = (HttpServletRequest) messageInfo.getRequestMessage();

        Callback[] callbacks;

        if (request.getParameter("name") != null && request.getParameter("password") != null) {

            // Get the (caller) name and password from the request
            // NOTE: This is for the smallest possible example only. In practice
            // putting the password in a request query parameter is highly
            // insecure
            String name = request.getParameter("name");
            Password password = new Password(request.getParameter("password"));

            // Obtain a reference to the Identity Store
            IdentityStore identityStore = CDI.current().select(IdentityStore.class).get();

            // Delegate the {credentials in -> identity data out} function to
            // the Identity Store
            CredentialValidationResult result = identityStore.validate(new UsernamePasswordCredential(name, password));

            if (result.getStatus() == VALID) {
                callbacks = new Callback[] {
                    // The name of the authenticated caller
                    new CallerPrincipalCallback(clientSubject, result.getCallerName()),
                    // the groups of the authenticated caller (for test
                    // assume non-null, non-empty)
                    new GroupPrincipalCallback(clientSubject, result.getCallerGroups().toArray(new String[0])) };
            } else {
                throw new AuthException("Login failed");
            }
        } else {
            // The JASPIC protocol for "do nothing"
            callbacks = new Callback[] { new CallerPrincipalCallback(clientSubject, (Principal) null) };
        }

        try {
            // Communicate the details of the authenticated user to the
            // container. In many
            // cases the handler will just store the details and the container
            // will actually handle
            // the login after we return from this method.
            handler.handle(callbacks);
        } catch (IOException | UnsupportedCallbackException e) {
            throw (AuthException) new AuthException().initCause(e);
        }

        return SUCCESS;
    }

    @Override
    public Class<?>[] getSupportedMessageTypes() {
        return supportedMessageTypes;
    }

    @Override
    public AuthStatus secureResponse(MessageInfo messageInfo, Subject serviceSubject) throws AuthException {
        return SEND_SUCCESS;
    }

    @Override
    public void cleanSubject(MessageInfo messageInfo, Subject subject) throws AuthException {

    }
}