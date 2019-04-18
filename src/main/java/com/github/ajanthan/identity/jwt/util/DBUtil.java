package com.github.ajanthan.identity.jwt.util;

import com.github.ajanthan.identity.jwt.internal.Constants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.oauth2.IdentityOAuth2Exception;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class DBUtil {
    private static final Log log = LogFactory.getLog(DBUtil.class);
    private static DataSource dataSource = null;

    public static DataSource getDatasource() throws IdentityOAuth2Exception {
        if (dataSource == null) {
            synchronized (DBUtil.class) {
                if (dataSource == null) { //to avoid initializing multiple time by waiting threads
                    try {
                        Object result = new InitialContext().doLookup(Constants.DS_NAME);
                        if (result != null) {
                            dataSource = (DataSource) result;
                        } else {
                            new IdentityOAuth2Exception("Cannot find " + Constants.DS_NAME);
                        }
                    } catch (NamingException e) {
                        throw new IdentityOAuth2Exception("Error while looking up datasource " + Constants.DS_NAME, e);
                    }
                }
            }
        }
        return dataSource;
    }
}
