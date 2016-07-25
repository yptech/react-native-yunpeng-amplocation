package com.yunpeng.amaplocation.sqlite;

import android.os.Parcel;
import android.os.Parcelable;

import com.amap.api.location.AMapLocation;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by m2mbob on 16/7/23.
 */
public class BackgroundLocation implements Parcelable {

    private Long locationId = null;
    private AMapLocation location;

    public BackgroundLocation(AMapLocation location) {
        this.location = location;
    }

    public BackgroundLocation() {
        super();
    }

    protected BackgroundLocation(Parcel in) {
        setLocationId(in.readLong());
        setLocation((AMapLocation) in.readParcelable(location.getClass().getClassLoader()));
    }

    public static final Creator<BackgroundLocation> CREATOR = new Creator<BackgroundLocation>() {
        @Override
        public BackgroundLocation createFromParcel(Parcel in) {
            return new BackgroundLocation(in);
        }

        @Override
        public BackgroundLocation[] newArray(int size) {
            return new BackgroundLocation[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(getLocationId());
        dest.writeParcelable(location, flags);
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public AMapLocation getLocation() {
        return location;
    }

    public void setLocation(AMapLocation location) {
        this.location = location;
    }

    public Double getLatitude() {
        return location.getLatitude();
    }

    public void setLatitude(Double latitude) {
        location.setLatitude(latitude);
    }

    public Double getLongitude() {
        return location.getLongitude();
    }

    public void setLongitude(Double longitude) {
       location.setLongitude(longitude);
    }

    public Float getAccuracy() {
        return location.getAccuracy();
    }

    public void setAccuracy(Float accuracy) {
        location.setAccuracy(accuracy);
    }

    public String getAddress() {
        return location.getAddress();
    }

    public void setAddress(String address) {
        location.setAddress(address);
    }

    public String getCountry() {
        return location.getCountry();
    }

    public void setCountry(String country) {
        location.setCountry(country);
    }

    public String getProvince() {
        return location.getProvince();
    }

    public void setProvince(String province) {
        location.setProvince(province);
    }

    public String getCity() {
        return location.getCity();
    }

    public void setCity(String city) {
        location.setCity(city);
    }

    public String getDistrict() {
        return location.getDistrict();
    }

    public void setDistrict(String district) {
        location.setDistrict(district);
    }

    public String getStreet() {
        return location.getStreet();
    }

    public void setStreet(String street) {
        location.setStreet(street);
    }

    public String getCity_code() {
        return location.getCityCode();
    }

    public void setCity_code(String city_code) {
        location.setCityCode(city_code);
    }

    public String getAd_code() {
        return location.getAdCode();
    }

    public void setAd_code(String ad_code) {
        location.setAdCode(ad_code);
    }

    public Long getTime() {
        return location.getTime();
    }

    public void setTime(Long time) {
        location.setTime(time);
    }

    public String getPoi_name() {
        return location.getPoiName();
    }

    public void setPoi_name(String poi_name) {
        location.setPoiName(poi_name);
    }

    public String getAoi_name() {
        return location.getAoiName();
    }

    public void setProvider(String provider) {
        location.setProvider(provider);
    }

    public String getProvider() {
        return location.getProvider();
    }

    public void setAoi_name(String aoi_name) {
        location.setAoiName(aoi_name);
    }

    public JSONObject toJSONObject() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("locationId", getLocationId());
        json.put("time", getTime());
        json.put("latitude", getLatitude());
        json.put("longitude", getLongitude());
        json.put("accuracy", getAccuracy());
        json.put("country", getCountry());
        json.put("address", getAddress());
        json.put("province", getProvince());
        json.put("city", getCity());
        json.put("district", getDistrict());
        json.put("street", getStreet());
        json.put("city_code", getCity_code());
        json.put("ad_code", getAd_code());
        json.put("time", getTime());
        json.put("poi_name", getPoi_name());
        json.put("aoi_name", getAoi_name());
        json.put("provider", getProvider());

        return json;
    }

}
