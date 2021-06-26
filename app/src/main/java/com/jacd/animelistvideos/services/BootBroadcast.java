package com.jacd.animelistvideos.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context ctx, Intent intent) {
        ctx.startService(new Intent(ctx, MyServices.class));

    }

}