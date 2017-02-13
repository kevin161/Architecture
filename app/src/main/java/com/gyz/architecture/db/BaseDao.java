package com.gyz.architecture.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.gyz.architecture.db.annotion.DbFiled;
import com.gyz.architecture.db.annotion.DbTable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @version V1.0
 * @FileName: com.gyz.architecture.db.BaseDao.java
 * @author: ZhaoHao
 * @date: 2017-02-08 22:47
 */
public abstract class BaseDao<T> implements IBaseDao<T> {
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

        if (!isInit) {
            entityClass = entity;
            database = sqLiteDatabase;
            if (entityClass.getAnnotation(DbTable.class) == null) {
                tableName = entityClass.getClass().getSimpleName();
            } else {
                tableName = entityClass.getAnnotation(DbTable.class).value();
            }

            if (!database.isOpen()) {
                return false;
            }

            if (!TextUtils.isEmpty(createTable())) {
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

        /**
         * 第一条数据 查询0个数据
         */
        String sql = "select * from " + this.tableName + " limit 1,0";
        Cursor cursor = null;

        try {
            cursor = database.rawQuery(sql, null);
            //表的列名数组
            String[] columnNames = cursor.getColumnNames();

            //拿到传入的field数组
            Field[] fields = entityClass.getFields();
            for (Field field : fields) {
                field.setAccessible(true);
            }

            /**
             * 通过遍历找对应关系
             */
            for (String columnName : columnNames) {
//                如果找到对应的field就赋值给他
                Field columnField = null;
                for (Field field : fields) {
                    String fileName = null;
                    //先拿到dbFiled注解对应的值
                    if (field.getAnnotation(DbFiled.class) != null) {
                        fileName = field.getAnnotation(DbFiled.class).value();
                    } else {
                        fileName = field.getName();
                    }

//                   如果表的列名 等于了  成员变量的注解名字
                    if (fileName.equals(columnName)) {
                        columnField = field;
                        break;
                    }

                }
                //找到了对应的关系
                if (columnField != null) {
                    cacheMap.put(columnName, columnField);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Override
    public Long insert(T entity) {

        Map<String,String> map = getValues(entity);
        ContentValues values = getContentValues(map);
        Long result = database.insert(tableName,null,values);
        return result;
    }

    @Override
    public int delete(T where) {
        Map map = getValues(where);
        Condition condition = new Condition(map);

        /**
         * id=1 数据
         * id=?      new String[]{String.value(1)}
         */
        int result = database.delete(tableName, condition.getWhereClause(), condition.getWhereArgs());
        return result;
    }

    @Override
    public int update(T entity, T where) {
        int result = -1;
        Map values = getValues(entity);
        ContentValues contentValues = getContentValues(values);
        //讲条件对象where转换为map
        Map whereClause = getValues(where);

        Condition condition = new Condition(whereClause);
        result = database.update(tableName,contentValues,condition.getWhereClause(),condition.getWhereArgs());
        return result;
    }

    @Override
    public List<T> query(T where) {
        return query(where, null, null, null);
    }

    @Override
    public List<T> query(T where, String orderBy, Integer startIndex, Integer limit) {
        Map map = getValues(where);

        String limitString = null;
        if (startIndex != null && limit != null) {
            limitString = startIndex + " , " + limit;
        }

        Condition condition = new Condition(map);
        Cursor cursor = database.query(tableName, null, condition.getWhereClause(), condition.getWhereArgs(), null, null, orderBy, limitString);
        List<T> result = getResult(cursor, where);
        cursor.close();
        return result;
    }


    /**
     * 将对象拥有的成员变量
     * 转换成  表的列名  ---》成员变量的值     的对应关系
     * 如  tb_name  ----> "张三"
     * 这样的map集合
     * User
     * name  "zhangsn"
     *
     * @return
     */
    private Map getValues(T entity) {
        HashMap<String, String> result = new HashMap<>();
        Iterator<Field> fieldIterator = cacheMap.values().iterator();

        while (fieldIterator.hasNext()) {
            Field colmunField = fieldIterator.next();
            String cacheKey = null;
            String cacheValue = null;
            if (null != colmunField.getAnnotation(DbFiled.class)) {
                cacheKey = colmunField.getAnnotation(DbFiled.class).value();
            } else {
                cacheKey = colmunField.getName();
            }

            try {
                if (null == colmunField.get(entity)) {
                    continue;
                }

                cacheValue = colmunField.get(entity).toString();

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            result.put(cacheKey, cacheValue);

        }
        return result;
    }

    /**
     * 讲 map 转换成ContentValues
     *
     * @param map
     * @return
     */
    private ContentValues getContentValues(Map<String, String> map) {
        ContentValues contentValues = new ContentValues();
        Set keys = map.keySet();
        Iterator<String> iterator = keys.iterator();
        while (iterator.hasNext()){
            String key = iterator.next();
            String value = map.get(key);
            if (value!=null) {
                contentValues.put(key,value);
            }
        }
        return contentValues;
    }


    /**
     * 通过cursor 和 T 转化成list数据，返回给应用层
     *
     * @param cursor
     * @param where
     * @return
     */
    private List<T> getResult(Cursor cursor, T where) {
        ArrayList list = new ArrayList();

        Object item;
        while (cursor.moveToNext()) {
            try {
                item = where.getClass().newInstance();
                /**
                 * 列名  name
                 * 成员变量名  Filed;
                 */
                Iterator iterator = cacheMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry entry = (Map.Entry) iterator.next();
                    /**
                     * 得到列名
                     */
                    String colmun = (String) entry.getKey();
                    // 然后以列名拿到  列名在游标的位子
                    Integer columunIndex = cursor.getColumnIndex(colmun);
                    Field field = (Field) entry.getValue();
                    Class type = field.getType();
                    if (columunIndex != -1) {
                        if (type == String.class) {
                            //反射的方式赋值
                            field.set(item, cursor.getString(columunIndex));
                        } else if (type == Double.class) {
                            field.set(item, cursor.getDouble(columunIndex));
                        } else if (type == Integer.class) {
                            field.set(item, cursor.getInt(columunIndex));
                        } else if (type == Long.class) {
                            field.set(item, cursor.getLong(columunIndex));
                        } else if (type == byte[].class) {
                            field.set(item, cursor.getBlob(columunIndex));
                            /*
                            不支持的类型
                             */
                        } else {
                            continue;
                        }
                    }

                }
                list.add(item);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return list;
    }


    //封装修改语句
    class Condition {

        /**
         * 查询条件
         * name=? && password =?
         */
        private String whereClause;

        private String[] whereArgs;

        public Condition(Map<String, String> whereClause) {
            ArrayList list = new ArrayList();
            StringBuilder sb = new StringBuilder();

            sb.append(" 1=1 ");
            Set keys = whereClause.keySet();
            Iterator iterator = keys.iterator();
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                String value = whereClause.get(key);

                if (value != null) {

                    /*
                    拼接条件查询语句
                    1=1 and name =? and password=?
                     */
                    sb.append(" and " + key + " =?");

                    /**
                     * ？----》value
                     */
                    list.add(value);
                }
            }
            this.whereClause = sb.toString();
            this.whereArgs = (String[]) list.toArray(new String[list.size()]);

        }

        public String getWhereClause() {
            return whereClause;
        }

        public String[] getWhereArgs() {
            return whereArgs;
        }
    }

}
