package com.academiaprogramacion.githubtops.models;

import android.graphics.drawable.Drawable;

/**
 * Menu item model
 */
public class LanguageGridItem {
	private Drawable mDrawableResource;
	private String mLanguage;

	public LanguageGridItem(String language, Drawable drawableResource) {
		this.mLanguage = language;
		this.mDrawableResource = drawableResource;
	}

	public Drawable getDrawableResource() {
		return mDrawableResource;
	}

	public String getLanguage() {
		return mLanguage;
	}

}
