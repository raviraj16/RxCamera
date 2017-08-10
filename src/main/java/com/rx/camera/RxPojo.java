package com.rx.camera;

import android.content.Intent;

/**
 * Created by Ravi Raj Priyadarshi on 10-08-2017.
 */

public class RxPojo {

    private Intent intent;
    private int resultCode;

    public RxPojo(Intent intent, int resultCode) {
        this.intent = intent;
        this.resultCode = resultCode;
    }

    public Intent getIntent() {
        return intent;
    }

    public void setIntent(Intent intent) {
        this.intent = intent;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }
}
