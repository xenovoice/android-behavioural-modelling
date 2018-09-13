package com.xv.activityrecognition;

import android.graphics.drawable.Drawable;
import android.view.View;

import java.util.List;

public interface ProgressLayout {

    public void showContent();

    public void showContent(List<Integer> idsOfViewsNotToShow);

    public void showLoading();

    public void showLoading(List<Integer> idsOfViewsNotToHide);

    public void showEmpty(int icon, String title, String description);

    public void showEmpty(Drawable icon, String title, String description);

    public void showEmpty(int icon, String title, String description, List<Integer> idsOfViewsNotToHide);

    public void showEmpty(Drawable icon, String title, String description, List<Integer> idsOfViewsNotToHide);

    public String getCurrentState();

    public boolean isContentCurrentState();

    public boolean isLoadingCurrentState();

    public boolean isEmptyCurrentState();
}
