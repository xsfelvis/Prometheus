package com.xsf.dev.myapplication.serviceinterface;


import com.xsf.dev.spiannotation.ServiceProvider;

/**
 * Created by xsf on 2018/10/15.
 * Description:
 */
@ServiceProvider(value = PernalInfoInterface.class)
public class PernalInfoServiceImppl implements PernalInfoInterface {
    @Override
    public String getName() {
        return "xsfdev";
    }

    @Override
    public String getPhone() {
        return "10086";

    }
}
