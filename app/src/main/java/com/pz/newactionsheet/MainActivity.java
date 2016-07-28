package com.pz.newactionsheet;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.pz.actionsheetlibrary.NewActionSheet;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, NewActionSheet.MenuItemClickListener {

    /**
     * 菜单的选项
     */
    public static final List<String> ITEMS = Arrays.asList("菜单1", "菜单2", "菜单3", "菜单4");

    private Button tvShowActionSheet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvShowActionSheet = (Button) findViewById(R.id.text_show_actionsheet);
        if (tvShowActionSheet != null) {
            tvShowActionSheet.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_show_actionsheet:
                NewActionSheet as = new NewActionSheet(this);
//                if (!TextUtils.isEmpty(tag)) {
//                    as.setmTag(tag);
//                }
                as.addItems(ITEMS);
                as.setItemClickListener(this);
                as.setCancelableOnTouchMenuOutside(true);
                as.showMenu();
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(String tag, int itemPosition) {
        Toast.makeText(this, "click " + String.valueOf(itemPosition), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCancelClick(String tag) {
        Toast.makeText(this, "click cancel", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClickAfterAnimation(String tag, int itemPosition) {

    }

    @Override
    public void onCancelClickAfterAnimation(String tag) {

    }
}
