package com.gyz.architecture.db;

import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

/**
 * @version V1.0
 * @FileName: com.gyz.architecture.db.BaseDaoFactory.java
 * @author: ZhaoHao
 * @date: 2017-02-08 22:50
 */
public class BaseDaoFactory {

    private String sqliteDatabasePath;
    private SQLiteDatabase sqLiteDatabase;
    private static BaseDaoFactory instance = new BaseDaoFactory();

    public BaseDaoFactory() {
        sqliteDatabasePath = Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/architecture.db";
        openDaoFactory();
    }

    private void openDaoFactory(){
        this.sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(sqliteDatabasePath,null);
    }

    public BaseDaoFactory getInstance(){
        return instance;
    }

    public synchronized <T extends BaseDao<T>,M>T getDataHelper(Class<T> clazz,Class<M> entityClass){
        BaseDao baseDao = null;

        try {
            baseDao = clazz.newInstance();
            baseDao.init(entityClass,sqLiteDatabase);

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return (T) baseDao;
    }


}
