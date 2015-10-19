package org.glassfish.jsr375.cdi;

import static org.glassfish.jsr375.cdi.CdiUtils.getAnnotation;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessBean;
import javax.security.identitystore.IdentityStore;
import javax.security.identitystore.annotation.EmbeddedIdentityStoreDefinition;

import org.glassfish.jsr375.identitystores.EmbeddedIdentityStore;

public class CdiExtension implements Extension {

    private Bean<IdentityStore> identityStoreBean;

    public <T> void processBean(@Observes ProcessBean<T> eventIn, BeanManager beanManager) {

        ProcessBean<T> event = eventIn; // JDK8 u60 workaround

        Optional<EmbeddedIdentityStoreDefinition> result = getAnnotation(beanManager, event.getAnnotated(), EmbeddedIdentityStoreDefinition.class);
        if (result.isPresent()) {
            identityStoreBean = new CdiProducer<IdentityStore>()
                .scope(ApplicationScoped.class)
                .types(IdentityStore.class)
                .create(e -> new EmbeddedIdentityStore(result.get().value()));

        }
    }

    public void afterBean(final @Observes AfterBeanDiscovery afterBeanDiscovery) {
        if (identityStoreBean != null) {
            afterBeanDiscovery.addBean(identityStoreBean);
        }
    }

}
