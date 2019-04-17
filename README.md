# wso2-is-custom-claim-retriever
A Custom ClaimRetriever for WSO2 Identity Server for populating JWT claims from Database

### Prerequisites
Make sure followings are installed properly.
- [Java](https://openjdk.java.net/install/index.html)
- [Maven](https://maven.apache.org/download.cgi)
- [Postgres DB](https://www.postgresql.org/download/)
- [WSO2 Identity Server](https://docs.wso2.com/display/IS570/Installation+Guide)


### Step 1: Building the extension
Clone the repository

``git clone https://github.com/ajanthan/wso2-is-custom-claim-retriever.git``

Go to `wso2-is-custom-claim-retriever` directory and execute maven build command.

`mvn clean install`

The extension will be available in `targer` directory. Copy the extension(`custom-claim-retriever-1.0-SNAPSHOT.jar`) to WSO2_IS_HOME/repository/components/lib/.

### Step 2: Setting up Database

In this guide we are going to use postgres database. Create a database `user_role_db`. Use [dbscript/schema.sql](dbscript/schema.sql) to create table under the database. Execute [dbscript/data.sql](dbscript/data.sql) to populate with sample data.

### Step 3: Setting up WSO2 Identity Server

Do the basic WSO2 identity server installation as mentioned in prerequisites. Download [postgresql java driver](https://jdbc.postgresql.org/download.html) and copy to WSO2_IS_HOME/repository/components/lib/.
Modify the host,port and database name in `url`,`username` and `password` in [conf/roles-datasources.xml](conf/roles-datasources.xml) according to the postgres installation and according to `Step 2`. Copy the modified [conf/roles-datasources.xml](conf/roles-datasources.xml) to WSO2_IS_HOME/repository/conf/datasources/.
Locate following section in WSO2_IS_HOME/repository/conf/identity/identity.xml and change value of `ClaimsRetrieverImplClass` to `com.github.ajanthan.identity.jwt.CustomClaimRetriever`. Once it updated the final version should look like [conf/identity.xml](conf/identity.xml).
```xml       
         <AuthorizationContextTokenGeneration>
             <Enabled>false</Enabled>
             <TokenGeneratorImplClass>org.wso2.carbon.identity.oauth2.authcontext.JWTTokenGenerator</TokenGeneratorImplClass>
             <ClaimsRetrieverImplClass>com.github.ajanthan.identity.jwt.CustomClaimRetriever</ClaimsRetrieverImplClass>
             <ConsumerDialectURI>http://wso2.org/claims</ConsumerDialectURI>
             <SignatureAlgorithm>SHA256withRSA</SignatureAlgorithm>
             <AuthorizationContextTTL>15</AuthorizationContextTTL>
         </AuthorizationContextTokenGeneration>
 ```
 
 Finally restart the WSO2 Identity Server.
 
 ### Step 5: Testing
 
 
 
