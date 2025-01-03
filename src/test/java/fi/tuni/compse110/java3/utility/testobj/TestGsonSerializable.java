package fi.tuni.compse110.java3.utility.testobj;

import com.google.gson.Gson;
import fi.tuni.compse110.java3.io.GsonSerializable;

public class TestGsonSerializable extends GsonSerializable {

    public int val1;
    public long val2;
    public float val3;
    public double val4;
    public boolean val5;
    public String val6;

    @Override
    public Gson getGson() {
        return new Gson();
    }
}
