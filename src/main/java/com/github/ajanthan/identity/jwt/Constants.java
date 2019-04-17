package com.github.ajanthan.identity.jwt;

public class Constants {
    public static final String GET_ROLE_ACTION_SQL = "SELECT UR_ROLE_NAME,UR_ROLE_ACTION FROM USER_ROLE WHERE UR_USER_NAME=?";
    public static final String ROLE_COLUMN_NAME = "UR_ROLE_NAME";
    public static final String ACTION_COLUMN_NAME = "UR_ROLE_ACTION";
    public static final String DS_NAME = "jdbc/user_role_ds";
    public static final String JNDI_FACTORY_NAME = "java.naming.factory.initial";
    public static final String JNDI_FACTORY_VALUE = "org.wso2.carbon.tomcat.jndi.CarbonJavaURLContextFactory";
}
