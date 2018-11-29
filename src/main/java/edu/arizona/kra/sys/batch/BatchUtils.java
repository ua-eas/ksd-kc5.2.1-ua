package edu.arizona.kra.sys.batch;

import org.apache.log4j.NDC;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.MessageFormat;

/**
 * nataliac on 8/22/18: Batch framework Imported and adapted from KFS
 **/
public class BatchUtils {

    public static Object getTargetIfProxied(Object object) {
        if (AopUtils.isAopProxy(object) && object instanceof Advised) {
            Advised advised = (Advised) object;
            try {
                Object target = advised.getTargetSource().getTarget();

                return target;
            } catch (Exception ex) {
                throw new RuntimeException("Unable to get class for proxy: " + ex.getMessage(), ex);
            }
        }

        return object;
    }

    public static String getHostname(){
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e){
            e.printStackTrace();
        }
        return null;
    }



}
