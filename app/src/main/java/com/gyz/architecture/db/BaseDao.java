package com.gyz.architecture.db;

import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.gyz.architecture.db.annotion.DbTable;

import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * @version V1.0
 * @FileName: com.gyz.architecture.db.BaseDao.java
 * @author: ZhaoHao
 * @date: 2017-02-08 22:47
 */
public abstract class BaseDao<T>  implements IBaseDao<T>{
    /**
     * ]
     * 持有数据库操作类的引用
     */
    private SQLiteDatabase database;
    /**
     * 保证实例化一次
     */
    private boolean isInit = false;
    /**
     * 持有操作数据库表所对应的java类型
     * User
     */
    private Class<T> entityClass;
    /**
     * 维护这表名与成员变量名的映射关系
     * key---》表名
     * value --》Field
     * class  methoFiled
     * {
     * Method  setMthod
     * Filed  fild
     * }
     */
    private HashMap<String, Field> cacheMap;

    private String tableName;

    /**
     * 创建表
     *
     * @return
     */
    protected abstract String createTable();

    /**
     * @param entity
     * @param sqLiteDatabase
     * @return 实例化一次
     */
    protected synchronized boolean init(Class<T> entity, SQLiteDatabase sqLiteDatabase) {

        if (!isInit){
            entityClass = entity;
            database = sqLiteDatabase;
            if (entityClass.getAnnotation(DbTable.class)==null){
                tableName = entityClass.getClass().getSimpleName();
            }else {
                tableName = entityClass.getAnnotation(DbTable.class).value();
            }

            if (database.isOpen()){
                return  false;
            }

            if (!TextUtils.isEmpty(createTable())){
                database.execSQL(createTable());
            }
            cacheMap = new HashMap<>();
            initCacheMap();
            isInit = true;

        }
        return isInit;
    }

    /**
     * 维护映射关系
     */
    private void initCacheMap() {




    }
}
