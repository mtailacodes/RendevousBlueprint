package com.mtailacodes.blueprintrendevouz;

import android.app.Application;

import com.mtailacodes.blueprintrendevouz.Util.RxBus;

/**
 * Created by matthewtaila on 12/10/17.
 */

public class MyApplication extends Application {

    private RxBus bus;

    @Override
    public void onCreate() {
        super.onCreate();
        bus = new RxBus();
    }

    public RxBus bus() {
        return bus;
    }
}
