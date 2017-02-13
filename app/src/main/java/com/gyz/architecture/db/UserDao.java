package com.gyz.architecture.db;

import java.util.List;

/**
 * @version V1.0
 * @FileName: com.gyz.architecture.db.UserDao.java
 * @author: ZhaoHao
 * @date: 2017-02-13 22:14
 */
public class UserDao extends BaseDao {
    private static final String TAG = "UserDao";

    @Override
    protected String createTable() {
        return  "create table if not exists tb_user(user_Id int,name varchar(20),password varchar(10))";
    }

    @Override
    public List query(String sql) {
        return null;
    }
}
