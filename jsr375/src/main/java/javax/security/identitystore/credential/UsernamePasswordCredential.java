/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2015 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * http://glassfish.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package javax.security.identitystore.credential;

/**
 * Represents the credentials typically used by standard username/password authentication.
 */
public class UsernamePasswordCredential extends AbstractCredential {

    private final String caller;
    private final Password password;

    /**
     * Constructor.
     *
     * @param callerName The user name
     * @param password The password
     */
    public UsernamePasswordCredential(String callerName, javax.security.identitystore.credential.Password password) {
        this.caller = callerName;
        this.password = password;
    }

    /**
     * Determines the password.
     * @return The password.
     */
    public Password getPassword() {
        return password;
    }

    /**
     * Invokes the specific subclass to securely clear the credential value.
     * Some <code>{@link Credential}</code> subclasses contain credential values
     * which are inherently secure, such as tokens, for which clearing the
     * credential may not be necessary.
     * <p>
     * For example, if the credential includes a password,
     * this method would overwrite the password value.
     */
    @Override
    public void clearCredential() {
        password.clear();
    }

    /**
     * Determines the caller associated with this credential. This value would
     * usually be the unique value identifying the caller, like a login name.
     *
     * @return The caller associated with this credential
     */
    @Override
    public String getCaller() {
        return caller;
    }

    /**
     * Determines whether the credential is valid. This would be called as part of
     * the credential validation process to check the integrity of the credential,
     * such as a signature check. This check would be self-contained,
     * not requiring identity store access.
     *
     * @return <code>true</code> if credential has integrity.
     */
    @Override
    public boolean isValid() {
        return true;
    }
}
