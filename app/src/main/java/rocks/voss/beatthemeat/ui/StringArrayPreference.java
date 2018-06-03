package rocks.voss.beatthemeat.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.support.design.widget.TextInputEditText;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by voss on 23.03.18.
 */

public class StringArrayPreference extends DialogPreference {

    private final static int DEFAULT_NUMBER_OPTIONS = 5;

    @Getter
    private Set<String> values = new LinkedHashSet<>();

    @Setter
    private int numberOptions = DEFAULT_NUMBER_OPTIONS;

    private final List<TextInputEditText> textfields = new ArrayList<>();

    public StringArrayPreference(Context context) {
        super(context);
    }

    public StringArrayPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StringArrayPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected View onCreateDialogView() {

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;

        FrameLayout dialogView = new FrameLayout(getContext());

        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        dialogView.addView(linearLayout);

        textfields.clear();
        for (int i = 0; i < numberOptions; i++) {
            TextInputEditText textInputEditText = new TextInputEditText(getContext());
            textfields.add(textInputEditText);
            linearLayout.addView(textInputEditText);
        }

        return dialogView;
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        for (int i = 0; i < textfields.size() && i < values.size(); i++) {
            textfields.get(i).setText((String) values.toArray()[i]);
        }
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            Set<String> values = new LinkedHashSet<>();

            for (TextInputEditText textfield : textfields) {
                String text = textfield.getText().toString();
                if (!text.equals("")) {
                    values.add(text);
                }
            }

            if (callChangeListener(values)) {
                setValue(values);
            }
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return new LinkedHashSet<String>();
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        try {
            setValue(restorePersistedValue ? getPersistedStringSet(values) : new LinkedHashSet<>());
        } catch (ClassCastException e) {
            getEditor().remove(getKey()).apply();
            Set<String> values = new LinkedHashSet<>();
            values.add(getPersistedString(""));
            setValue(values);
        }
    }

    public void setValue(Set<String> values) {
        this.values = values;
        persistStringSet(values);
    }
}
