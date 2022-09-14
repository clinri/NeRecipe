package ru.netology.nerecipe.ui

import android.content.Intent
import android.inputmethodservice.Keyboard
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.snackbar.Snackbar
import ru.netology.nerecipe.R
import ru.netology.nerecipe.databinding.ActivityAppBinding
import ru.netology.nerecipe.util.StringArg
import ru.netology.nerecipe.util.hideKeyboard

class AppActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAppBinding
    private lateinit var navController: NavController

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.app_bar,menu)
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppBinding.inflate(layoutInflater)

        setContentView(binding.root)
        handleIntent(intent)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        // Setup the bottom navigation view with navController
        navController = navHostFragment.navController
        binding.navigationMenu.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, distanation, _ ->
            if (distanation.id == R.id.newRecipeFragment) {
                binding.navigationMenu.visibility = View.GONE
            } else {
                binding.navigationMenu.visibility = View.VISIBLE
            }
        }

        binding.navigationMenu.requestFocus()
        binding.navigationMenu.hideKeyboard()

//        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        binding.navigationMenu.clearFocus()
    }

    companion object {
        var Bundle.textArg: String? by StringArg
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        intent?.let {
            if (it.action != Intent.ACTION_SEND) return@let
            val text = it.getStringExtra(Intent.EXTRA_TEXT)
            if (text.isNullOrBlank()) {
                Snackbar.make(binding.root, "Content can't be empty", Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok) { finish() }
                    .show()
                return@let
            }

            navController.navigateUp()
            navController.navigate(
                R.id.action_feedRecipesFragment_to_newRecipeFragment,
                Bundle().apply {
                    textArg = text
                })
        }
    }
}