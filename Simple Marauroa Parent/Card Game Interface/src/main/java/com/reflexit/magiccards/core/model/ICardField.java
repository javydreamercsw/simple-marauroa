package com.reflexit.magiccards.core.model;

import java.lang.reflect.Field;

public interface ICardField {

    public Class getType();

    public boolean isTransient();

    public String name();
    
    public Field getJavaField();
}
