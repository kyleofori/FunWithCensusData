package com.detroitlabs.kyleofori.funwithcensusdata;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

/**
 * Created by kyleofori on 6/19/15.
 */
public class BoundaryDataReceiver extends ResultReceiver {

    public interface Receiver {
        public void onReceiveResult(int resultCode, Bundle resultData);
    }

    private Receiver boundaryDataReceiver;

    public BoundaryDataReceiver(Handler handler) {
        super(handler);
    }

    public void setReceiver(Receiver boundaryDataReceiver) {
        this.boundaryDataReceiver = boundaryDataReceiver;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        super.onReceiveResult(resultCode, resultData);
        if(boundaryDataReceiver != null) {
            boundaryDataReceiver.onReceiveResult(resultCode, resultData);
        }
    }
}
