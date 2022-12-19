package com.rchugunov.autofilltestapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rchugunov.autofilltestapp.databinding.ActivityMainBinding
import io.cobrowse.CobrowseIO

class MainActivity : AppCompatActivity() {

    val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        CobrowseIO.instance().license("Av3013hRuDUy2g");
        CobrowseIO.instance().start(this);

        binding.webView.loadUrl("http://192.168.1.101:8080/")
    }
}