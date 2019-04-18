package com.github.ajanthan.identity.jwt.internal;

import com.github.ajanthan.identity.jwt.CustomClaimProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.wso2.carbon.identity.openidconnect.ClaimProvider;

@Component(
    name = "identity.oidc.claim.custom.component",
    immediate = true
)
public class CustomClaimProviderComponent {

    private static final Log log = LogFactory.getLog(CustomClaimProviderComponent.class);

    @Activate
    protected void activate(ComponentContext context) {
        CustomClaimProvider claimProviderImpl = new CustomClaimProvider();

        try {
            context.getBundleContext().registerService(ClaimProvider.class.getName(), claimProviderImpl, null);
        } catch (Exception e) {
            String msg = "Error when registering CustomClaimProvider service";
            log.error(msg, e);
            throw new RuntimeException(msg, e);
        }
        if (log.isDebugEnabled()) {
            log.debug("CustomClaimProvider bundle is activated");
        }

    }

}
