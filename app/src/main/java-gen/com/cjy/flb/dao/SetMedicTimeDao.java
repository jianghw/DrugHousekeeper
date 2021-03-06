package com.cjy.flb.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.cjy.flb.db.SetMedicTime;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "SET_MEDIC_TIME".
*/
public class SetMedicTimeDao extends AbstractDao<SetMedicTime, Long> {

    public static final String TABLENAME = "SET_MEDIC_TIME";

    /**
     * Properties of entity SetMedicTime.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Number = new Property(1, String.class, "number", false, "NUMBER");
        public final static Property Device_uid = new Property(2, String.class, "device_uid", false, "DEVICE_UID");
        public final static Property Medicine = new Property(3, String.class, "medicine", false, "MEDICINE");
        public final static Property Quantity = new Property(4, Integer.class, "quantity", false, "QUANTITY");
        public final static Property Medicine_id = new Property(5, Integer.class, "medicine_id", false, "MEDICINE_ID");
        public final static Property Unit = new Property(6, String.class, "unit", false, "UNIT");
        public final static Property Eat_time = new Property(7, String.class, "eat_time", false, "EAT_TIME");
    };


    public SetMedicTimeDao(DaoConfig config) {
        super(config);
    }
    
    public SetMedicTimeDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"SET_MEDIC_TIME\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"NUMBER\" TEXT NOT NULL ," + // 1: number
                "\"DEVICE_UID\" TEXT NOT NULL ," + // 2: device_uid
                "\"MEDICINE\" TEXT," + // 3: medicine
                "\"QUANTITY\" INTEGER," + // 4: quantity
                "\"MEDICINE_ID\" INTEGER," + // 5: medicine_id
                "\"UNIT\" TEXT," + // 6: unit
                "\"EAT_TIME\" TEXT);"); // 7: eat_time
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"SET_MEDIC_TIME\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, SetMedicTime entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getNumber());
        stmt.bindString(3, entity.getDevice_uid());
 
        String medicine = entity.getMedicine();
        if (medicine != null) {
            stmt.bindString(4, medicine);
        }
 
        Integer quantity = entity.getQuantity();
        if (quantity != null) {
            stmt.bindLong(5, quantity);
        }
 
        Integer medicine_id = entity.getMedicine_id();
        if (medicine_id != null) {
            stmt.bindLong(6, medicine_id);
        }
 
        String unit = entity.getUnit();
        if (unit != null) {
            stmt.bindString(7, unit);
        }
 
        String eat_time = entity.getEat_time();
        if (eat_time != null) {
            stmt.bindString(8, eat_time);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public SetMedicTime readEntity(Cursor cursor, int offset) {
        SetMedicTime entity = new SetMedicTime( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getString(offset + 1), // number
            cursor.getString(offset + 2), // device_uid
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // medicine
            cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4), // quantity
            cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5), // medicine_id
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // unit
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7) // eat_time
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, SetMedicTime entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setNumber(cursor.getString(offset + 1));
        entity.setDevice_uid(cursor.getString(offset + 2));
        entity.setMedicine(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setQuantity(cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4));
        entity.setMedicine_id(cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5));
        entity.setUnit(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setEat_time(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(SetMedicTime entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(SetMedicTime entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
