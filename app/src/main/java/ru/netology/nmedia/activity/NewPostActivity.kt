package ru.netology.nmedia.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.ActivityNewPostBinding

class NewPostActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding = ActivityNewPostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.content.requestFocus()

        val contents = intent.getStringExtra(Intent.EXTRA_TEXT)
        binding.content.setText(contents)

        binding.save.setOnClickListener {
            if (binding.content.text.isNullOrBlank()) {
                Toast.makeText(
                    this,
                    getString(R.string.empty_post_error),
                    Toast.LENGTH_SHORT
                ).show()
                setResult(RESULT_CANCELED)
            } else {
                val result = Intent().putExtra(Intent.EXTRA_TEXT, binding.content.text.toString())
                setResult(RESULT_OK, result)
            }
            finish()
        }
    }
}