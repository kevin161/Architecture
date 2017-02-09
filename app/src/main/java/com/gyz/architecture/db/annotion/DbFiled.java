package com.gyz.architecture.db.annotion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @version V1.0
 * @FileName: com.gyz.architecture.db.annotion.DbFiled.java
 * @author: ZhaoHao
 * @date: 2017-02-08 22:39
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DbFiled {
   String value();
}
