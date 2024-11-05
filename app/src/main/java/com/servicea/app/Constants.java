package com.servicea.app;

public class Constants {
    public static final String f_open = "f_open";
    public static final String f_close = "f_close";
    public static final String l_open = "l_open";
    public static final String l_close = "l_close";
    public static final String job_category_id = "job_category_id";
    public static final String service_center_id = "service_center_id";
    public static final String selected_reserve_time = "selected_reserve_time";
    public static final String customer_car_id = "customer_car_id";
    public static final String CAR_ID = "id_car";
    public static final String reserve_description = "reserve_description";
    public static final String reserve_payment_amount = "reserve_payment_amount";
    public static final String reserve_request_reserve = "reserve_request_reserve";
    public static final String reserve_payment_result = "reserve_payment_result";
    public static final String reserve_service = "reserve_service";
    public static final String reserve_product_group = "reserve_product_group";
    public static final String reserve_model = "reserve_model";
    public static final String IS_RESERVED = "is_reserved";
    public static final String  UPDATE_RESERVE_TIME = "update_reserve_time";
    public static final String  RESERVED_SERVICE_ID = "reserved_service_id";
    public static final String PROVINCE_ID = "province_id";
    public static final String CITY_ID = "city_id";
    public static final String  USER_TYPE = "user_type";
    public static final String  DATE_TIME = "date_time";
    public static final String  STATUS = "status";
    public static final String  SMS_NOTIFICATION = "sms_notification";
    public static final String  RECEIVER_ID = "receiver_id";
    public static final String  JOB_CATEGORY_ID = "job_category_id";
    public static final String  NOTIFICATION_PROV_ID = "notification_prov_id";

    public static final String  NOTIFICATION_ID = "notification_id";

    public static final String  PUSH_NOTIFICATION = "push_notification";
    public static final String  USER_ID = "user_id";
    public static final String  CREATED_AT = "created_at";
    public static final String  UPDATED_AT = "updated_at";
    public static final String  SERVICE_REQUEST_ID = "service_request_id";

    public static final String CAR_PLATE_TYPE = "car_plate_type";

    public static final String PLAK_AZAD_OLD_CITY_INDEX = "plk_azad_old_city_index";

    public static final String CUSTOMER_MODEL = "customer_model";

    public static final int RESERVE_NOTIFICATION_PROV_ID = 4;

    public final static long SEND_AGAIN_OTP_CODE_TIME = 75000;

    public enum PLAK_TYPE{
        PLAK_GENERAL(1),
        PLAK_TAXI(2),
        PLAK_EDARI(3),
        PLAK_ENTEZAMI(4),
        PLAK_MAOLOIN(5),
        PLAK_AZAD_NEW(6),
        PLAK_AZAD_OLD(7);

        public int id;
        PLAK_TYPE(int id) {
            this.id = id;
        }
    }

    public static PLAK_TYPE findById(int id){
        for(PLAK_TYPE type : PLAK_TYPE.values()){
            if( type.id == id){
                return type;
            }
        }
        return null;
    }

    public static final String  ADD_CUSTOMER_ACTIVITY_TAG = "AddCustomerActivity";


}
