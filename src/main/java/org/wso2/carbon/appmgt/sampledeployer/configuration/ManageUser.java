package org.wso2.carbon.appmgt.sampledeployer.configuration;

import org.apache.axis2.AxisFault;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.wso2.carbon.appmgt.sampledeployer.appm.LoginAdminServiceClient;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.carbon.base.ServerConfiguration;
import org.wso2.carbon.user.mgt.stub.UserAdminStub;
import org.wso2.carbon.user.mgt.stub.UserAdminUserAdminException;
import org.wso2.carbon.user.mgt.stub.types.carbon.ClaimValue;
import org.wso2.carbon.utils.CarbonUtils;

import java.io.File;
import java.rmi.RemoteException;

/**
 * Created by ushan on 3/31/15.
 */
public class ManageUser {

    //private static final String appmHome = CarbonUtils.getCarbonHome();
    private static final String appmHome = "/home/ushan/Shell_Script_Test/APPM/wso2appm-1.0.0-SNAPSHOT";

    private static final String axis2Repo = appmHome + File.separator + "repository" +
            File.separator + "deployment" + File.separator + "client";
    private static final String axis2Conf =
            ServerConfiguration.getInstance().getFirstProperty("Axis2Config.clientAxis2XmlLocation");
    private UserAdminStub userAdminStub;

    static {
        System.setProperty("javax.net.ssl.trustStore",appmHome+"/repository/resources/security/wso2carbon.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "wso2carbon");
        System.setProperty("javax.net.ssl.trustStoreType", "JKS");
    }

    public ManageUser(String backEndUrl) throws RemoteException, LoginAuthenticationExceptionException {
        ConfigurationContext configContext = ConfigurationContextFactory.createConfigurationContextFromFileSystem(
                axis2Repo, axis2Conf);
        userAdminStub =  new UserAdminStub(configContext,backEndUrl+"/services/UserAdmin");
        LoginAdminServiceClient loginAdminServiceClient = new LoginAdminServiceClient(backEndUrl);
        String session = loginAdminServiceClient.authenticate("admin", "admin");
        Options option;
        ServiceClient serviceClient;
        serviceClient = userAdminStub._getServiceClient();
        option = serviceClient.getOptions();
        option.setManageSession(true);
        option.setProperty(org.apache.axis2.transport.http.HTTPConstants.COOKIE_STRING, session);
    }

    public void addUser() throws RemoteException, UserAdminUserAdminException {
        ClaimValue ffid = new ClaimValue();
        ffid.setClaimURI("http://wso2.org/ffid");
        ffid.setValue("234455666");
        ClaimValue streetaddress = new ClaimValue();
        streetaddress.setClaimURI("http://wso2.org/claims/streetaddress");
        streetaddress.setValue("234455666");
        ClaimValue zipcode = new ClaimValue();
        zipcode.setClaimURI("http://wso2.org/claims/zipcode");
        zipcode.setValue("GL");
        ClaimValue card_number = new ClaimValue();
        card_number.setClaimURI("http://wso2.org/claims/card_number");
        card_number.setValue("001012676878");
        ClaimValue card_holder = new ClaimValue();
        card_holder.setClaimURI("http://wso2.org/claims/card_holder");
        card_holder.setValue("subscriber");
        ClaimValue telephone = new ClaimValue();
        telephone.setClaimURI("http://wso2.org/claims/telephone");
        telephone.setValue("0918886565");
        ClaimValue givenName = new ClaimValue();
        givenName.setClaimURI("http://wso2.org/claims/givenname");
        givenName.setValue("Subscriber");
        ClaimValue lastName = new ClaimValue();
        lastName.setClaimURI("http://wso2.org/claims/lastname");
        lastName.setValue("Subscriber");
        ClaimValue email = new ClaimValue();
        email.setClaimURI("http://wso2.org/claims/emailaddress");
        email.setValue("wso2@wso2.com");
        ClaimValue country = new ClaimValue();
        country.setClaimURI("http://wso2.org/claims/country");
        country.setValue("SriLanka");
        ClaimValue expire_date = new ClaimValue();
        expire_date.setClaimURI("http://wso2.org/claims/expiration_date");
        expire_date.setValue("31/12/2015");
        ClaimValue claimValues[] = new ClaimValue[]{ffid,streetaddress,zipcode,
                card_number,card_holder,telephone
                ,givenName,lastName,email,country,expire_date};
        userAdminStub.addUser("subscriber", "subscriber",
                new String[]{"Internal/subscriber"}, claimValues, "Subscriber");
    }

   /* public static void main(String[] args){
        try {
            ManageUser manageUser = new ManageUser("https://localhost:9443");
            manageUser.addUser();
        } catch (AxisFault axisFault) {
            axisFault.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (UserAdminUserAdminException e) {
            e.printStackTrace();
        } catch (LoginAuthenticationExceptionException e) {
            e.printStackTrace();
        }
    }*/

}
