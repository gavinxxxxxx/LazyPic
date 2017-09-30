package me.gavin.photo.viewer.app;

import android.os.Bundle;
import android.support.annotation.Nullable;

import me.gavin.photo.viewer.R;
import me.yokeyword.fragmentation.SupportActivity;

/**
 * 这里是萌萌哒注释君
 *
 * @author gavin.xiong 2017/9/30
 */
public class MainActivity extends SupportActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_holder);
        loadRootFragment(R.id.holder, new FolderFragment());
    }
}
