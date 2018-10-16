package com.xsf.dev.myapplication;

import com.xsf.dev.foundation.spi.ServiceLoader;
import com.xsf.dev.myapplication.serviceinterface.PernalInfoInterface;

/**
 * Created by xsf on 2018/10/16.
 * Description:
 */
public class PernalInfoService implements PernalInfoInterface {

    private final PernalInfoInterface mDelegate = ServiceLoader.load(PernalInfoInterface.class).get();

    private PernalInfoService() {
    }

    public static final PernalInfoService getInstance() {
        return Singleton.instance;
    }


    @Override
    public final String getName() {
        return null != this.mDelegate ? this.mDelegate.getName() : null;
    }

    @Override
    public final String getPhone() {
        return null != this.mDelegate ? this.mDelegate.getPhone() : null;
    }

    private static final class Singleton {
        static final PernalInfoService instance = new PernalInfoService();
    }
}
