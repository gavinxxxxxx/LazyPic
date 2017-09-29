package me.gavin.photo.viewer.app

import android.os.Bundle
import me.gavin.photo.viewer.R
import me.yokeyword.fragmentation.SupportActivity

class MainActivity : SupportActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_holder)
        loadRootFragment(R.id.holder, FolderFragment())
    }

}
