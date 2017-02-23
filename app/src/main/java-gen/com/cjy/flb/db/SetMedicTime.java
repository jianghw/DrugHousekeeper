package com.cjy.flb.db;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table "SET_MEDIC_TIME".
 */
public class SetMedicTime {

    private Long id;
    /** Not-null value. */
    private String number;
    /** Not-null value. */
    private String device_uid;
    private String medicine;
    private Integer quantity;
    private Integer medicine_id;
    private String unit;
    private String eat_time;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public SetMedicTime() {
    }

    public SetMedicTime(Long id) {
        this.id = id;
    }

    public SetMedicTime(Long id, String number, String device_uid, String medicine, Integer quantity,
                        Integer medicine_id, String unit, String eat_time) {
        this.id = id;
        this.number = number;
        this.device_uid = device_uid;
        this.medicine = medicine;
        this.quantity = quantity;
        this.medicine_id = medicine_id;
        this.unit = unit;
        this.eat_time = eat_time;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /** Not-null value. */
    public String getNumber() {
        return number;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setNumber(String number) {
        this.number = number;
    }

    /** Not-null value. */
    public String getDevice_uid() {
        return device_uid;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setDevice_uid(String device_uid) {
        this.device_uid = device_uid;
    }

    public String getMedicine() {
        return medicine;
    }

    public void setMedicine(String medicine) {
        this.medicine = medicine;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getMedicine_id() {
        return medicine_id;
    }

    public void setMedicine_id(Integer medicine_id) {
        this.medicine_id = medicine_id;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getEat_time() {
        return eat_time;
    }

    public void setEat_time(String eat_time) {
        this.eat_time = eat_time;
    }

    // KEEP METHODS - put your custom methods here
    // KEEP METHODS END

}
