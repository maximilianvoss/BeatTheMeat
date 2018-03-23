package rocks.voss.beatthemeat.widgets;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.NumberPicker;

import rocks.voss.beatthemeat.R;
import rocks.voss.beatthemeat.data.ThermometerSettings;

public class ThermometerSettingActivity extends Activity {
    private NumberPicker pickerTemp1;
    private NumberPicker pickerTemp2;
    private CheckBox isRange;
    private ThermometerSettings thermometerSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thermometer_setting);

        thermometerSettings = (ThermometerSettings) getIntent().getParcelableExtra ("rocks.voss.beatthemeat.thermometerSettings");
        pickerTemp1 = (NumberPicker) findViewById(R.id.temp1);
        pickerTemp2 = (NumberPicker) findViewById(R.id.temp2);
        isRange = (CheckBox) findViewById(R.id.isRange);

        initTemperature(pickerTemp1, thermometerSettings.getTemp1());
        initTemperature(pickerTemp2, thermometerSettings.getTemp2());
        initRangeCheckbox();
    }

    private void initTemperature(NumberPicker picker, int startValue) {
        picker.setMinValue(0);
        picker.setMaxValue(350);
        picker.setWrapSelectorWheel(true);
        picker.setValue(startValue);

        picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                thermometerSettings.setTemp1(pickerTemp1.getValue());
                thermometerSettings.setTemp2(pickerTemp2.getValue());
            }
        });
    }

    private void initRangeCheckbox() {
        if (thermometerSettings.isRange()) {
            pickerTemp2.setVisibility(View.VISIBLE);
            isRange.setChecked(true);
        }

        isRange.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                pickerTemp2.setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE);
                thermometerSettings.setRange(isChecked);

                if (isChecked && pickerTemp1.getValue() >= pickerTemp2.getValue()) {
                    pickerTemp2.setValue(pickerTemp1.getValue() + 10);
                    thermometerSettings.setTemp2(pickerTemp1.getValue() + 10);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        getIntent().putExtra("rocks.voss.beatthemeat.thermometerSettings", thermometerSettings);
        super.onBackPressed();
    }
}
