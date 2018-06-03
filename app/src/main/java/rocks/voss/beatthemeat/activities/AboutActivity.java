package rocks.voss.beatthemeat.activities;

import android.app.Activity;
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

        TextView copyrightText = (TextView) findViewById(R.id.copyrighttext);
        copyrightText.setMovementMethod(LinkMovementMethod.getInstance());
        String text = copyrightText.getText().toString();
        text = text.replace("Version: X", "Version : " + BuildConfig.VERSION_NAME);
        copyrightText.setText(text);
    }
}
