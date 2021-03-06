package javax.security.authenticationmechanism;

import javax.security.auth.message.config.AuthConfigFactory;
import javax.security.auth.message.module.ServerAuthModule;
import javax.servlet.ServletContext;

public final class JaspicUtils {

    private JaspicUtils() {
    }

    /**
     * Registers the given SAM using the standard JASPIC
     * {@link AuthConfigFactory} but using a small set of wrappers that just
     * pass the calls through to the SAM.
     * 
     * @param serverAuthModule
     */
    public static void registerSAM(ServletContext context, ServerAuthModule serverAuthModule) {
        AuthConfigFactory.getFactory().registerConfigProvider(
            new DefaultAuthConfigProvider(serverAuthModule),
            "HttpServlet", 
            getAppContextID(context), 
            "Default single SAM authentication config provider"
        );
    }

    public static String getAppContextID(ServletContext context) {
        return context.getVirtualServerName() + " " + context.getContextPath();
    }

}
