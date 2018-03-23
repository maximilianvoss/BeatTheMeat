package rocks.voss.beatthemeat.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import lombok.Data;

/**
 * Created by voss on 16.03.18.
 */
@Data
public class ThermometerSettings implements Parcelable{
    private int temp1;
    private int temp2;
    private boolean isRange;

    public ThermometerSettings() {}

    protected ThermometerSettings(Parcel in) {
        temp1 = in.readInt();
        temp2 = in.readInt();
        isRange = in.readByte() != 0;
    }

    public static final Creator<ThermometerSettings> CREATOR = new Creator<ThermometerSettings>() {
        @Override
        public ThermometerSettings createFromParcel(Parcel in) {
            return new ThermometerSettings(in);
        }

        @Override
        public ThermometerSettings[] newArray(int size) {
            return new ThermometerSettings[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(temp1);
        dest.writeInt(temp2);
        dest.writeByte((byte) (isRange ? 1 : 0));
    }
}
