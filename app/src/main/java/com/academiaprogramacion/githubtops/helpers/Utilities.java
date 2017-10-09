package com.academiaprogramacion.githubtops.helpers;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.academiaprogramacion.githubtops.R;
import com.wnafee.vector.compat.ResourcesCompat;

public class Utilities {

    /**
     * Returns a drawable element defined by language provided
     * @param context
     * @param language
     * @return
     */
    public static Drawable getDrawableFromLanguage(Context context, String language) {
        int drawableId;
        switch (language) {
            case "C":
                drawableId = R.drawable.vector_c;
                break;
            case "CoffeeScript":
                drawableId = R.drawable.vector_coffeescript;
                break;
            case "C++":
                drawableId = R.drawable.vector_cplusplus;
                break;
            case "C#":
                drawableId = R.drawable.vector_csharp;
                break;
            case "CSS":
                drawableId = R.drawable.vector_css;
                break;
            case "Erlang":
                drawableId = R.drawable.vector_erlang;
                break;
            case "Go":
                drawableId = R.drawable.vector_go;
                break;
            case "HTML":
                drawableId = R.drawable.vector_html;
                break;
            case "Java":
                drawableId = R.drawable.vector_java;
                break;
            case "JavaScript":
                drawableId = R.drawable.vector_javascript;
                break;
            case "PHP":
                drawableId = R.drawable.vector_php;
                break;
            case "Python":
                drawableId = R.drawable.vector_python;
                break;
            case "Ruby":
                drawableId = R.drawable.vector_ruby;
                break;
            default:
                drawableId = R.drawable.vector_generic;
                break;
        }
        return ResourcesCompat.getDrawable(context, drawableId);
    }
}
