package com.github.ajanthan.identity.jwt;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.oauth2.IdentityOAuth2Exception;
import org.wso2.carbon.identity.oauth2.authcontext.DefaultClaimsRetriever;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.SortedMap;
//

/**
 * CustomClaimRetriever populate JWT claims from a database
 */
public class CustomClaimRetriever extends DefaultClaimsRetriever {
    private Log log = LogFactory.getLog(CustomClaimRetriever.class);
    private DataSource dataSource;

    @Override
    public void init() {
        try {
            Hashtable environment = new Hashtable();
            environment.put(Constants.JNDI_FACTORY_NAME, Constants.JNDI_FACTORY_VALUE);
            Context initContext = new InitialContext(environment);
            Object result = initContext.lookup(Constants.DS_NAME);
            if (result != null) {
                dataSource = (DataSource) result;
            } else {
                log.error("Cannot find " + Constants.DS_NAME);
            }
        } catch (NamingException e) {
            log.error(e);
        }
    }

    @Override
    public SortedMap<String, String> getClaims(String endUserName, String[] requestedClaims) throws IdentityOAuth2Exception {
        SortedMap<String, String> claimValues = super.getClaims(endUserName, requestedClaims);

        try {
            PreparedStatement statement = dataSource.getConnection().prepareCall(Constants.GET_ROLE_ACTION_SQL);
            statement.setString(1, endUserName);
            ResultSet roleActionRS = statement.executeQuery();
            while (roleActionRS.next()) {
                String roleName = roleActionRS.getString(Constants.ROLE_COLUMN_NAME);
                String roleAction = roleActionRS.getString(Constants.ACTION_COLUMN_NAME);
                claimValues.put(roleName, roleAction);
            }
        } catch (SQLException e) {
            throw new IdentityOAuth2Exception(e.getMessage());
        }

        return claimValues;
    }


}
