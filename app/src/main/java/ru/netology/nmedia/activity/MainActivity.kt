package ru.netology.nmedia.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.launch
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.PostViewModel
import androidx.core.net.toUri

class MainActivity : AppCompatActivity() {

    private var isEditing: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel: PostViewModel by viewModels()

        val newPostContract = registerForActivityResult(NewPostActivityContract()) { text ->
            text?.let {
                viewModel.apply {
                    change(text)
                    save()
                }
            }
        }

        val editPostActivityContract =
            registerForActivityResult(EditPostActivityContract()) { text ->
                text?.let {
                    viewModel.apply {
                        change(it)
                        save()
                    }
                }
            }

        val adapter = PostsAdapter(object : OnInteractionListener {
            override fun onEdit(post: Post) {
                viewModel.edit(post)
                editPostActivityContract.launch(post.content)
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
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, post.content)
                    type = "text/plain"
                }

                val shareIntent =
                    Intent.createChooser(intent, getString(R.string.chooser_share_post))
                startActivity(shareIntent)
            }

            override fun onVideo(post: Post) {
                val intentVideo = Intent(Intent.ACTION_VIEW, post.video?.toUri())
                startActivity(intentVideo)
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

        binding.addPost.setOnClickListener {
            newPostContract.launch()
        }
    }
}
