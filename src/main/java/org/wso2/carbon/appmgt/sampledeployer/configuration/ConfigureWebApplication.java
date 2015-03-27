package org.wso2.carbon.appmgt.sampledeployer.configuration;

import org.apache.axis2.AxisFault;
import org.wso2.carbon.appmgt.sampledeployer.appm.ClaimManagementServiceClient;
import org.wso2.carbon.appmgt.sampledeployer.appm.LoginAdminServiceClient;
import org.wso2.carbon.appmgt.sampledeployer.appm.RemoteUserStoreManagerServiceClient;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.carbon.claim.mgt.stub.ClaimManagementServiceException;
import org.wso2.carbon.um.ws.api.stub.RemoteUserStoreManagerServiceUserStoreExceptionException;

import java.rmi.RemoteException;

/**
 * Created by ushan on 3/27/15.
 */
public class ConfigureWebApplication {

    private static ClaimManagementServiceClient claimManagementServiceClient;
    private static LoginAdminServiceClient loginAdminServiceClient;
    private static String  session;
    private static RemoteUserStoreManagerServiceClient remoteUserStoreManagerServiceClient;

    public ConfigureWebApplication(String backendUrl) throws RemoteException, LoginAuthenticationExceptionException {
        loginAdminServiceClient = new LoginAdminServiceClient(backendUrl);
        session = loginAdminServiceClient.authenticate("admin","admin");
        claimManagementServiceClient =  new ClaimManagementServiceClient(session,backendUrl);
        remoteUserStoreManagerServiceClient =  new RemoteUserStoreManagerServiceClient(session,backendUrl);
    }

    public void addClaimMapping() throws RemoteException, ClaimManagementServiceException {
        claimManagementServiceClient.addClaim("FrequentFlyerID", "http://wso2.org/ffid", true);
        claimManagementServiceClient.addClaim("zipcode", "http://wso2.org/claims/zipcode", true);
        claimManagementServiceClient.addClaim("Credit card number", "http://wso2.org/claims/card_number", true);
        claimManagementServiceClient.addClaim("Credit cArd Holder Name", "http://wso2.org/claims/card_holder"
                , true);
        claimManagementServiceClient.addClaim("Credit card expiration date", "http://wso2.org/claims/expiration_date"
                , true);
    }

    public void setClaimValues() throws RemoteUserStoreManagerServiceUserStoreExceptionException, RemoteException {
        remoteUserStoreManagerServiceClient.updateClaims("admin", "http://wso2.org/ffid", "12345151");
        remoteUserStoreManagerServiceClient.updateClaims("admin", "http://wso2.org/claims/streetaddress", "21/5");
        remoteUserStoreManagerServiceClient.updateClaims("admin", "http://wso2.org/claims/zipcode", "GL");
        remoteUserStoreManagerServiceClient.updateClaims("admin", "http://wso2.org/claims/card_number"
                , "001012676878");
        remoteUserStoreManagerServiceClient.updateClaims("admin", "http://wso2.org/claims/card_holder", "Admin");
        remoteUserStoreManagerServiceClient.updateClaims("admin", "http://wso2.org/claims/telephone", "091222222");
        remoteUserStoreManagerServiceClient.updateClaims("admin", "http://wso2.org/claims/givenname", "Sachith");
        remoteUserStoreManagerServiceClient.updateClaims("admin", "http://wso2.org/claims/lastname", "Ushan");
        remoteUserStoreManagerServiceClient.updateClaims("admin", "http://wso2.org/claims/emailaddress", "wso2@wso2.com");
        remoteUserStoreManagerServiceClient.updateClaims("admin", "http://wso2.org/claims/country", "SriLanka");
        remoteUserStoreManagerServiceClient.updateClaims("admin", "http://wso2.org/claims/expiration_date"
                , "31/12/2015");
    }
}
