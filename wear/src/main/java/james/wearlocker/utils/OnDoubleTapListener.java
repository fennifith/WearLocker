package james.wearlocker.utils;

import android.view.View;

public abstract class OnDoubleTapListener implements View.OnClickListener {

    private long previousClick = 0;

    @Override
    public void onClick(View v) {
        long clickTime = System.currentTimeMillis();
        if (clickTime - previousClick < 300) {
            onDoubleClick(v);
            previousClick = 0;
        } else {
            onSingleClick(v);
        }

        previousClick = clickTime;
    }

    public abstract void onSingleClick(View v);

    public abstract void onDoubleClick(View v);
}
