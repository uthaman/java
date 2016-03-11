import javax.naming.Context;

import weblogic.jndi.Environment;

import weblogic.management.MBeanHome;
import weblogic.management.configuration.DomainMBean;
import weblogic.management.configuration.SecurityConfigurationMBean;
import weblogic.management.security.RealmMBean;
import weblogic.management.security.authentication.AuthenticationProviderMBean;
import weblogic.management.security.authentication.GroupMemberListerMBean;

import weblogic.security.providers.authentication.ActiveDirectoryAuthenticatorMBean;


public class WlsADAuthenticator {
    
    /** Requires WebLogic 10.3 Remote-Client.jar */
    public static void main(String[] args) throws Exception {
        Environment env = new Environment();
        env.setProviderUrl("t3://127.0.0.1:7101");
        env.setSecurityPrincipal("weblogic");
        env.setSecurityCredentials("weblogic123");
        Context con = env.getInitialContext();
        MBeanHome mbeanHome = (MBeanHome) con.lookup(MBeanHome.ADMIN_JNDI_NAME);
        DomainMBean domain = mbeanHome.getActiveDomain();
        SecurityConfigurationMBean secMB = domain.getSecurityConfiguration();
        RealmMBean rMB = secMB.findDefaultRealm();
        AuthenticationProviderMBean aMB[] = rMB.getAuthenticationProviders();
        for(int i=0; i < aMB.length; i++) {
            if(aMB[i] instanceof ActiveDirectoryAuthenticatorMBean) {                    
                ActiveDirectoryAuthenticatorMBean admbean = (ActiveDirectoryAuthenticatorMBean) aMB[i];
                String listers = admbean.listUsers("userName",0);
                while(admbean.haveCurrent(listers)) {
                    String user = admbean.getCurrentName(listers);
                    System.out.println("\n user Name="+user);
                    admbean.advance(listers);
                }
                String grps = admbean.listMemberGroups("userName");
                while(admbean.haveCurrent(grps)) {
                    String user = admbean.getCurrentName(grps);
                    System.out.println("\n group Name="+user);
                    admbean.advance(grps);
                }
            }
            if(aMB[i] instanceof GroupMemberListerMBean) { 
                GroupMemberListerMBean groupReaderMBean = (GroupMemberListerMBean)aMB[i];
                if("ActiveDirectoryAuthenticator".equalsIgnoreCase(groupReaderMBean.getName())) {
                    String cursorName = groupReaderMBean.listGroups("*", 0);
                    while (groupReaderMBean.haveCurrent(cursorName)) {
                        String group = groupReaderMBean.getCurrentName(cursorName);
                        System.out.println("\n Group=" + group);
                        groupReaderMBean.advance(cursorName);
                    }
                }
            }
        }
    }
}
