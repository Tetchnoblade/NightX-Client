package net.aspw.client.visual.font;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface FontDetails {

    String fontName();

    int fontSize() default -1;
}