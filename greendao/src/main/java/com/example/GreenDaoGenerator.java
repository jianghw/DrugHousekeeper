package com.example;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;

public class GreenDaoGenerator {

    private static final String SQL_DB = "com.cjy.flb.db";
    private static final String SQL_DAO = "com.cjy.flb.dao";

    public static void main(String[] args) throws Exception {
        //创建一个用于添加实体的Schema对象，第一个参数表示数据库的版本，
        // 第二个参数表示在java-gen目录下自动生成的实体类和DAO类存放的包名
        Schema schema = new Schema(1, SQL_DB);
        // 假如你不想实体类和DAO类都放在一个包中，你可以重新为DAO类设置一个新的包
        schema.setDefaultJavaPackageDao(SQL_DAO);

        createFirstTable(schema);


        //最后通过DaoGenerator对象的generateAll()方法来生成相应的实体类和DAO类，参数分别为Schema对象和java-gen目录路径
        new DaoGenerator().generateAll(schema, "../flb_cjyun/app/src/main/java-gen");
    }

    /**
     * 创建表
     *
     * @param schema
     */
    private static void createFirstTable(Schema schema) {
        //创建一个实体，一个实体对应一张表，此处表示生成的实体名为Student，同样它默认也是表名
        // 你如果不想实体名和表名相同，也可以重新设置一个表名
        //为Student表添加字段,这里的参数表示实体类Student的字段名，生成的表中的字段会变成大写，如name在表中字段为NAME
        // entity.setTableName("Students");

        Entity box = schema.addEntity("Box");
        box.addLongProperty("boxId").primaryKey();
        box.addStringProperty("day_in_week").notNull();
        box.addStringProperty("point_in_time").notNull();
        box.addIntProperty("number");
        //吃药记录表
        Entity user = schema.addEntity("Eat");
        user.addIdProperty().autoincrement().primaryKey();
        user.addStringProperty("device_uid").notNull();
        user.addStringProperty("eat_medicine_time");
        user.addIntProperty("number");
        user.addBooleanProperty("taken");

        //记录是否通知表
        Entity notif = schema.addEntity("IsNotif");
        notif.addIdProperty().autoincrement().primaryKey();
        notif.addStringProperty("today");
        notif.addStringProperty("medic_id");
        notif.addStringProperty("medic_name");
        notif.addStringProperty("medic_phone");
        notif.addBooleanProperty("isMOut");
        notif.addIntProperty("isM");
        notif.addBooleanProperty("isNOut");
        notif.addIntProperty("isN");
        notif.addBooleanProperty("isAOut");
        notif.addIntProperty("isA");
        notif.addBooleanProperty("isEOut");
        notif.addIntProperty("isE");

        //记录将要服药的时间表
        Entity eatTime = schema.addEntity("EatTime");
        eatTime.addIdProperty().autoincrement().primaryKey();
        eatTime.addStringProperty("medic_id");
        eatTime.addLongProperty("morn");
        eatTime.addLongProperty("non");
        eatTime.addLongProperty("after");
        eatTime.addLongProperty("even");
        eatTime.addStringProperty("mornS");
        eatTime.addStringProperty("nonS");
        eatTime.addStringProperty("afterS");
        eatTime.addStringProperty("evenS");
        eatTime.addStringProperty("name");
        eatTime.addStringProperty("phone");
        //记录重复设置功能中的表
        Entity repeatSet = schema.addEntity("RepeatWeek");
        repeatSet.addIdProperty().autoincrement().primaryKey();
        repeatSet.addStringProperty("medic_id");
        repeatSet.addStringProperty("number");
        repeatSet.addBooleanProperty("isEvery");
        repeatSet.addBooleanProperty("isMorn");
        repeatSet.addBooleanProperty("isTue");
        repeatSet.addBooleanProperty("isWed");
        repeatSet.addBooleanProperty("isThu");
        repeatSet.addBooleanProperty("isFri");
        repeatSet.addBooleanProperty("isSat");
        repeatSet.addBooleanProperty("isSun");


        //建立多表关联
      /*  Entity number = schema.addEntity("Number");
        number.addLongProperty("numberId").primaryKey();
        number.addIntProperty("number").notNull();
        number.addStringProperty("device_uid").notNull();
        number.addStringProperty("unit");
        number.addStringProperty("eat_time");

        Entity medicine = schema.addEntity("Medicine");
        medicine.addLongProperty("medicineId").primaryKey();
        medicine.addStringProperty("medicine_name");
        medicine.addIntProperty("quantity");

        Entity nmAssociated = schema.addEntity("NumMed");
        Property nmnId = nmAssociated.addLongProperty("numberId").getProperty();
        Property nmmId = nmAssociated.addLongProperty("medicineId").getProperty();
        nmAssociated.addToOne(number, nmnId);
        nmAssociated.addToOne(medicine, nmmId);
        number.addToMany(nmAssociated, nmnId);
        medicine.addToMany(nmAssociated, nmmId);*/

        //用于设置时间用表
        Entity setMTime = schema.addEntity("SetMedicTime");
        setMTime.addIdProperty().autoincrement().primaryKey();
        setMTime.addStringProperty("number").notNull();
        setMTime.addStringProperty("device_uid").notNull();
        setMTime.addStringProperty("medicine");
        setMTime.addIntProperty("quantity");
        setMTime.addIntProperty("medicine_id");
        setMTime.addStringProperty("unit");
        setMTime.addStringProperty("eat_time");

        schema.enableKeepSectionsByDefault();
    }
}
