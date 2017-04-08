package james.wearlocker.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import james.wearlocker.R;

public class OptionActivity extends Activity implements View.OnClickListener {

    public static final String EXTRA_TITLE = "james.wearlocker.EXTRA_TITLE";
    public static final String EXTRA_OPTIONS = "james.wearlocker.EXTRA_OPTIONS";
    public static final String EXTRA_INDEX = "james.wearlocker.EXTRA_INDEX";

    private LinearLayout layout;
    private List<String> options;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);
        setResult(RESULT_CANCELED);

        TextView title = (TextView) findViewById(R.id.title);
        layout = (LinearLayout) findViewById(R.id.options);

        title.setText(getIntent().getStringExtra(EXTRA_TITLE));

        options = getIntent().getStringArrayListExtra(EXTRA_OPTIONS);
        LayoutInflater inflater = LayoutInflater.from(this);
        for (int i = 0; i < options.size(); i++) {
            TextView view = (TextView) inflater.inflate(R.layout.item_option, layout, false);
            view.setText(options.get(i));
            view.setTag(i);
            view.setOnClickListener(this);
            layout.addView(view);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_INDEX, (int) v.getTag());
        setResult(RESULT_OK, intent);
        finish();
    }
}
