package com.jingyue.apktools.http;


import com.google.gson.Gson;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

public abstract class HttpCallbackListener<E> extends HttpCallback{
    public void onSuccess(String data){
        if(data.isEmpty()||"null".equals(data)){
            onDataSuccess(null);
            return;
        }
        E object = new Gson().fromJson(data,getTClass());
        onDataSuccess(object);

    }

    public abstract void onDataSuccess(E obiect);
    protected void onError(String err){
    }

    protected Class<E> getTClass() {
        ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
        Type resultType = type.getActualTypeArguments()[0];
        if (resultType instanceof Class) {
            return (Class<E>) resultType;
        } else {
            // 处理集合
            try {
                Field field = resultType.getClass().getDeclaredField("rawTypeName");
                field.setAccessible(true);
                String rawTypeName = (String) field.get(resultType);
                return (Class<E>) Class.forName(rawTypeName);
            } catch (Exception e) {
                return (Class<E>) Collection.class;
            }
        }
    }
}
