package com.github.ajanthan.identity.jwt;

import com.github.ajanthan.identity.jwt.internal.Constants;
import com.github.ajanthan.identity.jwt.util.DBUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.oauth2.IdentityOAuth2Exception;
import org.wso2.carbon.identity.oauth2.authz.OAuthAuthzReqMessageContext;
import org.wso2.carbon.identity.oauth2.dto.OAuth2AccessTokenRespDTO;
import org.wso2.carbon.identity.oauth2.dto.OAuth2AuthorizeRespDTO;
import org.wso2.carbon.identity.oauth2.token.OAuthTokenReqMessageContext;
import org.wso2.carbon.identity.openidconnect.ClaimProvider;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
//

/**
 * CustomClaimProvider populates JWT claims of ID token from a database
 */
public class CustomClaimProvider implements ClaimProvider {
    private Log log = LogFactory.getLog(CustomClaimProvider.class);

    private Map<String, Object> getClaims(String endUserName) throws IdentityOAuth2Exception {
        DataSource dataSource = DBUtil.getDatasource();
        Map<String, Object> permissionClaims = new HashMap<>();
        if (log.isDebugEnabled()) {
            log.info("Getting roles and action for " + endUserName);
        }
        PreparedStatement statement = null;
        try {
            statement = dataSource.getConnection().prepareCall(Constants.GET_ROLE_ACTION_SQL);
            statement.setString(1, endUserName);
            ResultSet roleActionRS = statement.executeQuery();

            /**
             * Building following json using java maps
             *
             * {
             *    "rdb_role":{
             *                  "action":"admin,read"
             *               },
             *    "spark_role":{
             *                  "action":"read"
             *                  }
             * }
             */
            while (roleActionRS.next()) {
                String roleName = roleActionRS.getString(Constants.ROLE_COLUMN_NAME);
                String roleAction = roleActionRS.getString(Constants.ACTION_COLUMN_NAME);
                Map<String, Object> existingActions = (Map<String, Object>) permissionClaims.get(roleName);
                if (existingActions != null) {
                    String actions = (String) existingActions.get("action");
                    existingActions.put("action", actions.concat(",").concat(roleAction));
                    permissionClaims.put(roleName, existingActions);
                } else {
                    Map<String, Object> actions = new HashMap<>();
                    actions.put("action", roleAction);
                    permissionClaims.put(roleName, actions);
                }
            }
            roleActionRS.close();
        } catch (SQLException e) {
            throw new IdentityOAuth2Exception(e.getMessage());
        } finally {
            try {
                statement.close();
            } catch (SQLException e) {
                //ignore
            }
        }
        Map<String, Object> additionalClaims = new HashMap<>();
        additionalClaims.put("permission", permissionClaims);
        return additionalClaims;
    }


    @Override
    public Map<String, Object> getAdditionalClaims(OAuthAuthzReqMessageContext oAuthAuthzReqMessageContext, OAuth2AuthorizeRespDTO oAuth2AuthorizeRespDTO) throws IdentityOAuth2Exception {
        String endUserName = oAuthAuthzReqMessageContext.getAuthorizationReqDTO().getUser().getUserName();
        return getClaims(endUserName);
    }

    @Override
    public Map<String, Object> getAdditionalClaims(OAuthTokenReqMessageContext oAuthTokenReqMessageContext, OAuth2AccessTokenRespDTO oAuth2AccessTokenRespDTO) throws IdentityOAuth2Exception {
        String endUserName = oAuthTokenReqMessageContext.getAuthorizedUser().getUserName();
        return getClaims(endUserName);
    }
}
