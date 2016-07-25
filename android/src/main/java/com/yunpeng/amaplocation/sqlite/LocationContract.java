package com.yunpeng.amaplocation.sqlite;

import android.provider.BaseColumns;

/**
 * Created by m2mbob on 16/7/23.
 */
public class LocationContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public LocationContract() {}

    /* Inner class that defines the table contents */
    public static abstract class LocationEntry implements BaseColumns {
        public static final String TABLE_NAME = "location";
        public static final String COLUMN_NAME_LATITUDE = "latitude";
        public static final String COLUMN_NAME_LONGITUDE = "longitude";
        public static final String COLUMN_NAME_ACCURACY = "accuracy";
//        public static final String COLUMN_NAME_ALTITUDE = "altitude";
//        public static final String COLUMN_NAME_BEARING = "bearing";
        public static final String COLUMN_NAME_ADDRESS = "address";
        public static final String COLUMN_NAME_COUNTRY = "country";
        public static final String COLUMN_NAME_PROVINCE = "province";
        public static final String COLUMN_NAME_CITY = "city";
        public static final String COLUMN_NAME_DISTRICT = "district";
        public static final String COLUMN_NAME_STREET = "street";
//        public static final String COLUMN_NAME_STREET_NUM = "street_num";
        public static final String COLUMN_NAME_CITY_CODE = "city_code";
        public static final String COLUMN_NAME_AD_CODE = "ad_code";
        public static final String COLUMN_NAME_POI_NAME = "poi_name";
        public static final String COLUMN_NAME_AOI_NAME = "aoi_name";
        public static final String COLUMN_NAME_PROVIDER = "provider";
//        public static final String COLUMN_NAME_LOCATION_TYPE = "location_type";
//        public static final String COLUMN_NAME_LOCATION_DETAIL = "location_detail";
//        public static final String COLUMN_NAME_ERROR_INFO = "error_info";
//        public static final String COLUMN_NAME_ERROR_CODE = "error_code";
        public static final String COLUMN_NAME_TIME = "time";
    }
}
