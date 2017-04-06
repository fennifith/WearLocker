package james.wearlocker.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.wearable.view.drawer.WearableActionDrawer;
import android.support.wearable.view.drawer.WearableDrawerLayout;
import android.view.Gravity;

import james.wearlocker.R;

public class MainActivity extends Activity {

    private WearableDrawerLayout drawerLayout;
    private WearableActionDrawer actionDrawer;
    private NestedScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = (WearableDrawerLayout) findViewById(R.id.drawerLayout);
        actionDrawer = (WearableActionDrawer) findViewById(R.id.actionDrawer);
        scrollView = (NestedScrollView) findViewById(R.id.scrollView);

        drawerLayout.peekDrawer(Gravity.BOTTOM);
        actionDrawer.lockDrawerClosed();
        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == 0 || scrollY < oldScrollY)
                    drawerLayout.peekDrawer(Gravity.BOTTOM);
                else drawerLayout.closeDrawer(Gravity.BOTTOM);
            }
        });
    }
}
