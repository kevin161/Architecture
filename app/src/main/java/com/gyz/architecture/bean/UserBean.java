package com.gyz.architecture.bean;

import com.gyz.architecture.db.annotion.DbFiled;
import com.gyz.architecture.db.annotion.DbTable;

/**
 * @version V1.0
 * @FileName: com.gyz.architecture.bean.UserBean.java
 * @author: ZhaoHao
 * @date: 2017-02-13 22:12
 */
@DbTable("tb_user")
public class UserBean {

    public int user_Id=0;

    public Integer getUser_Id() {
        return user_Id;
    }

    public void setUser_Id(Integer user_Id) {
        this.user_Id = user_Id;
    }

    public UserBean(Integer id, String name, String password) {
        user_Id= id;
        this.name = name;
        this.password = password;
    }
    public UserBean( ) {
    }

    @DbFiled("name")
    public String name;
    //123456
    @DbFiled("password")
    public String password;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "name  "+name+"  password "+password;
    }

}
