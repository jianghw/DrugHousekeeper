package com.cjy.flb.bean;


import java.util.List;

/**
 * Created by Administrator on 2015/12/18 0018.
 */

public class PullSetDrugsBean {


    /**
     * medicine_list : [{"medicine":{"total_user_count":28,"id":41973,"production_unit":"浙江康恩贝制药股份有限公司",
     * "product_name_zh":"可达灵片","name_py":"kdlp","name_pinyin":"kedalingpian","dosage_form":"片剂",
     * "product_name_en":null},"quantity":1},{"medicine":{"total_user_count":9,"id":120397,
     * "production_unit":"通化颐生药业股份有限公司","product_name_zh":"土霉素片","name_py":"tmsp","name_pinyin":"tumeisupian",
     * "dosage_form":"片剂(糖衣)","product_name_en":"Oxytetracycline Tablets"},"quantity":1},
     * {"medicine":{"total_user_count":17,"id":877,"production_unit":"贵州光正制药有限责任公司","product_name_zh":"炎可宁片",
     * "name_py":"yknp","name_pinyin":"yankeningpian","dosage_form":"片剂","product_name_en":null},"quantity":1}]
     * eat_time : 08:30
     * number : 0
     * device_uid : 10001014
     */

    private String eat_time;
    private int number;
    private String device_uid;
    /**
     * medicine : {"total_user_count":28,"id":41973,"production_unit":"浙江康恩贝制药股份有限公司","product_name_zh":"可达灵片",
     * "name_py":"kdlp","name_pinyin":"kedalingpian","dosage_form":"片剂","product_name_en":null}
     * quantity : 1
     */

    private List<MedicineListEntity> medicine_list;

    public String getEat_time()
    {
        return eat_time;
    }

    public void setEat_time(String eat_time)
    {
        this.eat_time = eat_time;
    }

    public int getNumber()
    {
        return number;
    }

    public void setNumber(int number)
    {
        this.number = number;
    }

    public String getDevice_uid()
    {
        return device_uid;
    }

    public void setDevice_uid(String device_uid)
    {
        this.device_uid = device_uid;
    }

    public List<MedicineListEntity> getMedicine_list()
    {
        return medicine_list;
    }

    public void setMedicine_list(List<MedicineListEntity> medicine_list)
    {
        this.medicine_list = medicine_list;
    }

    public static class MedicineListEntity {
        /**
         * total_user_count : 28
         * id : 41973
         * production_unit : 浙江康恩贝制药股份有限公司
         * product_name_zh : 可达灵片
         * name_py : kdlp
         * name_pinyin : kedalingpian
         * dosage_form : 片剂
         * product_name_en : null
         */

        private MedicineEntity medicine;
        private int quantity;

        public MedicineEntity getMedicine()
        {
            return medicine;
        }

        public void setMedicine(MedicineEntity medicine)
        {
            this.medicine = medicine;
        }

        public int getQuantity()
        {
            return quantity;
        }

        public void setQuantity(int quantity)
        {
            this.quantity = quantity;
        }

        public static class MedicineEntity {
            private int total_user_count;
            private int id;
            private String production_unit;
            private String product_name_zh;
            private String name_py;
            private String name_pinyin;
            private String dosage_form;
            private Object product_name_en;

            public int getTotal_user_count()
            {
                return total_user_count;
            }

            public void setTotal_user_count(int total_user_count)
            {
                this.total_user_count = total_user_count;
            }

            public int getId()
            {
                return id;
            }

            public void setId(int id)
            {
                this.id = id;
            }

            public String getProduction_unit()
            {
                return production_unit;
            }

            public void setProduction_unit(String production_unit)
            {
                this.production_unit = production_unit;
            }

            public String getProduct_name_zh()
            {
                return product_name_zh;
            }

            public void setProduct_name_zh(String product_name_zh)
            {
                this.product_name_zh = product_name_zh;
            }

            public String getName_py()
            {
                return name_py;
            }

            public void setName_py(String name_py)
            {
                this.name_py = name_py;
            }

            public String getName_pinyin()
            {
                return name_pinyin;
            }

            public void setName_pinyin(String name_pinyin)
            {
                this.name_pinyin = name_pinyin;
            }

            public String getDosage_form()
            {
                return dosage_form;
            }

            public void setDosage_form(String dosage_form)
            {
                this.dosage_form = dosage_form;
            }

            public Object getProduct_name_en()
            {
                return product_name_en;
            }

            public void setProduct_name_en(Object product_name_en)
            {
                this.product_name_en = product_name_en;
            }
        }
    }
}