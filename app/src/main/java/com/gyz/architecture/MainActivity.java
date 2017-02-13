package com.gyz.architecture;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.gyz.architecture.bean.UserBean;
import com.gyz.architecture.db.BaseDao;
import com.gyz.architecture.db.BaseDaoFactory;
import com.gyz.architecture.db.IBaseDao;
import com.gyz.architecture.db.UserDao;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    IBaseDao<UserBean> userDao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userDao = BaseDaoFactory.getInstance().getDataHelper(UserDao.class, UserBean.class);



    }


    public void save(View v){

        for (int i=0;i<20;i++)
        {
            UserBean user=new UserBean(i,"teacher","123456");
            userDao.insert(user);
        }
    }

    public void deleteUser(){
        UserBean userBean = new UserBean();
        userBean.setName("David");
        userDao.delete(userBean);
    }


    public void update(View v){

        UserBean where=new UserBean();
        where.setName("teacher");

        UserBean user=new UserBean(1,"David","123456789");
        userDao.update(user,where);

    }

    public void queryList(View f){
        UserBean userBean = new UserBean();
        userBean.setName("teacher");
        userBean.setUser_Id(5);

        List<UserBean> list=userDao.query(userBean);
        Toast.makeText(getBaseContext(),"查询到  "+list.size()+"  条数据",Toast.LENGTH_LONG).show();
        Log.i("zhaohao","查询到  "+list.size()+"  条数据");
    }
}
