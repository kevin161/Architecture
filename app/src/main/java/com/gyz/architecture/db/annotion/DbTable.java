package com.gyz.architecture.db.annotion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @version V1.0
 * @FileName: com.gyz.architecture.db.annotion.DbTable.java
 * @author: ZhaoHao
 * @date: 2017-02-08 22:41
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DbTable {
    String value();
}
