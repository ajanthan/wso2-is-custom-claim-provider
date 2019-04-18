# wso2-is-custom-claim-provider
This extension populates OpenID connect ID token with additional claims from a RDBMS. This is useful when claims from none userstore source need to be inserted into the ID token.

### Prerequisites
Make sure followings are installed properly.
- [Java](https://openjdk.java.net/install/index.html)
- [Maven](https://maven.apache.org/download.cgi)
- [Postgres DB](https://www.postgresql.org/download/)
- [WSO2 Identity Server](https://docs.wso2.com/display/IS570/Installation+Guide)


### Step 1: Building the extension
Clone the this repository.

```text
git clone https://github.com/ajanthan/wso2-is-custom-claim-provider.git
```

Go to `wso2-is-custom-claim-provider` directory and execute maven build command.

 ```text
 mvn clean install
 ```

The extension will be available in `target` directory on successful build. Copy the extension(`custom-claim-provider-6.0.53.jar`) to WSO2_IS_HOME/repository/components/dropins/.

### Step 2: Setting up Database

In this guide we are going to use postgres database. Create a database `user_role_db`. Use [dbscript/schema.sql](dbscript/schema.sql) to create table under the database. Execute [dbscript/data.sql](dbscript/data.sql) to populate with sample data.

### Step 3: Setting up WSO2 Identity Server

Do the basic WSO2 identity server installation as mentioned in prerequisites. Download [postgresql java driver](https://jdbc.postgresql.org/download.html) and copy to WSO2_IS_HOME/repository/components/lib/.
Modify the host,port and database name in `url`,`username` and `password` in [conf/roles-datasources.xml](conf/roles-datasources.xml) according to the postgres installation and `Step 2`. Copy the modified [conf/roles-datasources.xml](conf/roles-datasources.xml) to WSO2_IS_HOME/repository/conf/datasources/. Finally restart the WSO2 Identity Server.
 
### Step 5: Testing
 
[Create users](https://docs.wso2.com/display/IS570/Configuring+Users#ConfiguringUsers-Addinganewuserandassigningroles) `alex` and `john` from management console and assign default `admin` role. [Create an OAuth/OpenID Connect service provider](https://docs.wso2.com/display/IS570/Adding+and+Configuring+a+Service+Provider) and get ClientID and ClientSecret for testing purpose.
Get ID token by invoking following command from a terminal after updating the parameters according to registered user `alex` and the service provider.
 
 ```text
curl -k -d "grant_type=password&username=alex&password=Admin@123" -H "Authorization: Basic base64encode(clientID:ClientSecret)" -H "Content-Type: application/x-www-form-urlencoded" https://localhost:8243/token
```

Response: 
```text
{"access_token":"5afaf543-f54f-3137-bc74-88acae182387","refresh_token":"5799f8c6-2470-3dbc-b3fa-506742aa876b","scope":"openid","id_token":"eyJ4NXQiOiJOVEF4Wm1NeE5ETXlaRGczTVRVMVpHTTBNekV6T0RKaFpXSTRORE5sWkRVMU9HRmtOakZpTVEiLCJraWQiOiJOVEF4Wm1NeE5ETXlaRGczTVRVMVpHTTBNekV6T0RKaFpXSTRORE5sWkRVMU9HRmtOakZpTVEiLCJhbGciOiJSUzI1NiJ9.eyJhdF9oYXNoIjoiV2plRi1qb3d4UVR1ckJhZ3AwTVJyUSIsImF1ZCI6IkZGbFpnV3pwMGZQTUVfMjY5SldqZmZmcWZMa2EiLCJzdWIiOiJhbGV4IiwibmJmIjoxNTU1NTY1ODYxLCJhenAiOiJGRmxaZ1d6cDBmUE1FXzI2OUpXamZmZnFmTGthIiwiYW1yIjpbInBhc3N3b3JkIl0sImlzcyI6Imh0dHBzOlwvXC9sb2NhbGhvc3Q6OTQ0M1wvb2F1dGgyXC90b2tlbiIsInBlcm1pc3Npb24iOnsicmRiX3JvbGUiOnsiYWN0aW9uIjoicmVhZCJ9LCJzcGFya19yb2xlIjp7ImFjdGlvbiI6ImFkbWluLHJlYWQifX0sImV4cCI6MTU1NTU2OTQ2MSwiaWF0IjoxNTU1NTY1ODYxfQ.YoNyWOqr1rWHuBasNla4RAt7gJJbHU1Nv7RHg-XH9pmJ84fpY_7SxK86wpKvfD0z72GXjs9gz72rWVIyckcfvxrTVOeEKYTsr1ChX-LqRarsyRQwwoUy95VqxQae3ZCxkGQoPcF-Ai5YVgplagv_V7RrLKyIzCl4UPZXXoLURANG0DNnIYecg9zPAjxqB4pX6ZVJSlGl5jAdcQ208xUaKr_jg4Y1COhE_jJcqBLpFd-ezHKchbDd-8b-5vCAtX7mohXVlkXs65Y8VP9KGVyWcL7yWrLEojKgh9cclwxA9w25V8A_T1H7UvzfQK5gzNyttMLHoA8xAKUJVzGra7aDCg","token_type":"Bearer","expires_in":3600}
```
 
 Followings will be the claim section of decoded ID token
 ```json
{
  "at_hash": "WjeF-jowxQTurBagp0MRrQ",
  "aud": "FFlZgWzp0fPME_269JWjfffqfLka",
  "sub": "alex",
  "nbf": 1555565861,
  "azp": "FFlZgWzp0fPME_269JWjfffqfLka",
  "amr": [
    "password"
  ],
  "iss": "https://localhost:9443/oauth2/token",
  "permission": {
    "rdb_role": {
      "action": "read"
    },
    "spark_role": {
      "action": "admin,read"
    }
  },
  "exp": 1555569461,
  "iat": 1555565861
}
```

Note that the `permission` claim is added by the custom claim provider.
 
 
