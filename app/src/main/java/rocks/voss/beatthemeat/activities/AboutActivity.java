package rocks.voss.beatthemeat.activities;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import rocks.voss.beatthemeat.BuildConfig;
import rocks.voss.beatthemeat.R;

public class AboutActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView copyrightText = findViewById(R.id.copyrighttext);
        copyrightText.setMovementMethod(LinkMovementMethod.getInstance());

        Resources res = getResources();
        String text = String.format(res.getString(R.string.copyrighttext), BuildConfig.VERSION_NAME);
        copyrightText.setText(text);
    }
}
