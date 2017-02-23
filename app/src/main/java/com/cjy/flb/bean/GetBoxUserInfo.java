package com.cjy.flb.bean;

/**
 * Created by Administrator on 2015/12/22 0022.
 */
public class GetBoxUserInfo {


    /**
     * id : 1
     * user_id : 3
     * device_id : 2
     * device_use_name : aaa
     * device_use_phone : 1234567
     * year : 2019
     * mouth : 12
     * gender : false
     * emergency_contact_1_name : null
     * emergency_contact_1_phone : null
     * emergency_contact_2_name : null
     * emergency_contact_2_phone : null
     * visiting_staff_name : null
     * visiting_staff_phone : null
     * avatar : null
     * created_at : 2015-12-18T00:00:00.000Z
     * updated_at : 2015-12-18T00:00:00.000Z
     */

    private int id;
    private int user_id;
    private int device_id;
    private String device_use_name;
    private String device_use_phone;
    private String year;
    private String mouth;
    private boolean gender;
    private Object emergency_contact_1_name;
    private Object emergency_contact_1_phone;
    private Object emergency_contact_2_name;
    private Object emergency_contact_2_phone;
    private Object visiting_staff_name;
    private Object visiting_staff_phone;
    private Object avatar;
    private String created_at;
    private String updated_at;

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public int getUser_id()
    {
        return user_id;
    }

    public void setUser_id(int user_id)
    {
        this.user_id = user_id;
    }

    public int getDevice_id()
    {
        return device_id;
    }

    public void setDevice_id(int device_id)
    {
        this.device_id = device_id;
    }

    public String getDevice_use_name()
    {
        return device_use_name;
    }

    public void setDevice_use_name(String device_use_name)
    {
        this.device_use_name = device_use_name;
    }

    public String getDevice_use_phone()
    {
        return device_use_phone;
    }

    public void setDevice_use_phone(String device_use_phone)
    {
        this.device_use_phone = device_use_phone;
    }

    public String getYear()
    {
        return year;
    }

    public void setYear(String year)
    {
        this.year = year;
    }

    public String getMouth()
    {
        return mouth;
    }

    public void setMouth(String mouth)
    {
        this.mouth = mouth;
    }

    public boolean isGender()
    {
        return gender;
    }

    public void setGender(boolean gender)
    {
        this.gender = gender;
    }

    public Object getEmergency_contact_1_name()
    {
        return emergency_contact_1_name;
    }

    public void setEmergency_contact_1_name(Object emergency_contact_1_name)
    {
        this.emergency_contact_1_name = emergency_contact_1_name;
    }

    public Object getEmergency_contact_1_phone()
    {
        return emergency_contact_1_phone;
    }

    public void setEmergency_contact_1_phone(Object emergency_contact_1_phone)
    {
        this.emergency_contact_1_phone = emergency_contact_1_phone;
    }

    public Object getEmergency_contact_2_name()
    {
        return emergency_contact_2_name;
    }

    public void setEmergency_contact_2_name(Object emergency_contact_2_name)
    {
        this.emergency_contact_2_name = emergency_contact_2_name;
    }

    public Object getEmergency_contact_2_phone()
    {
        return emergency_contact_2_phone;
    }

    public void setEmergency_contact_2_phone(Object emergency_contact_2_phone)
    {
        this.emergency_contact_2_phone = emergency_contact_2_phone;
    }

    public Object getVisiting_staff_name()
    {
        return visiting_staff_name;
    }

    public void setVisiting_staff_name(Object visiting_staff_name)
    {
        this.visiting_staff_name = visiting_staff_name;
    }

    public Object getVisiting_staff_phone()
    {
        return visiting_staff_phone;
    }

    public void setVisiting_staff_phone(Object visiting_staff_phone)
    {
        this.visiting_staff_phone = visiting_staff_phone;
    }

    public Object getAvatar()
    {
        return avatar;
    }

    public void setAvatar(Object avatar)
    {
        this.avatar = avatar;
    }

    public String getCreated_at()
    {
        return created_at;
    }

    public void setCreated_at(String created_at)
    {
        this.created_at = created_at;
    }

    public String getUpdated_at()
    {
        return updated_at;
    }

    public void setUpdated_at(String updated_at)
    {
        this.updated_at = updated_at;
    }
}
