package com.xsf.dev.myapplication.serviceinterface;



/**
 * Created by xsf on 2018/10/15.
 * Description:
 */
//@ServiceProvider(PrivateServiceInterface.class)
public class PrivateServiceImppl implements PrivateServiceInterface {
    @Override
    public String getName() {
        return "xsfdev";
    }

    @Override
    public String getSex() {
        return "male";

    }
}
