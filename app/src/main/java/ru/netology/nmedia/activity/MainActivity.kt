package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.viewmodel.PostViewModel

class MainActivity : AppCompatActivity() {

    private var isEditing: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val viewModel: PostViewModel by viewModels()

        binding.group.visibility = View.GONE

        val adapter = PostsAdapter(object : OnInteractionListener {
            override fun onEdit(post: Post) {
                if (!isEditing) {
                    isEditing = true
                    viewModel.edit(post)
                    binding.group.visibility = View.VISIBLE
                }
            }

            override fun onLike(post: Post) {
                viewModel.likeById(post.id)
            }

            override fun onRemove(post: Post) {
                if (!isEditing) {
                    viewModel.removeById(post.id)
                }
            }

            override fun onShare(post: Post) {
                viewModel.shareById(post.id)
            }
        })

        binding.container.adapter = adapter
        viewModel.data.observe(this) { posts ->
            val newPost = adapter.currentList.size < posts.size
            adapter.submitList(posts) {
               if (newPost) {
                   binding.container.scrollToPosition(0)
               }
            }
        }

        viewModel.edited.observe(this) { post ->
            if (post.id != 0L) {
                with(binding.content) {
                    setText(post.content)
                    AndroidUtils.showKeyboard(this)
                }
            }
        }

        binding.save.setOnClickListener {
            with(binding.content) {
                if (text.isNullOrBlank()) {
                    Toast.makeText(
                        this@MainActivity,
                        context.getString(R.string.error_empty_content),
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                viewModel.change(text.toString())
                viewModel.save()

                setText("")
                clearFocus()
                AndroidUtils.hideKeyboard(this)
                binding.group.visibility = View.GONE
                isEditing = false
            }
        }

        binding.cancel.setOnClickListener {
            with(binding.content) {
                setText("")
                clearFocus()
                AndroidUtils.hideKeyboard(this)
                binding.group.visibility = View.GONE
                viewModel.save()
                isEditing = false
            }
        }
    }
}
