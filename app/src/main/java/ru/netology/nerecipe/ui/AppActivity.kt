package ru.netology.nerecipe.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.forEach
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nerecipe.R
import ru.netology.nerecipe.databinding.ActivityAppBinding
import ru.netology.nerecipe.dto.TabName
import ru.netology.nerecipe.util.StringArg
import ru.netology.nerecipe.viewModel.RecipeViewModel

@AndroidEntryPoint
class AppActivity :
    AppCompatActivity(),
    SearchView.OnQueryTextListener { //, MenuItem.OnActionExpandListener
    private lateinit var binding: ActivityAppBinding
    private lateinit var navController: NavController
    private val viewModel: RecipeViewModel by viewModels()
    private var tabName: TabName = TabName.ALL

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.app_bar, menu)
        val search = menu?.findItem(R.id.search)
        val searchView = search?.actionView as? SearchView
        searchView?.isSubmitButtonEnabled = false
        searchView?.setOnQueryTextListener(this)
        //check to need hide menu
        viewModel.hideOptionMenu.observe(this) {
            menu?.forEach { menuItem ->
                menuItem.isVisible = !it
            }
        }
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.filter -> {
                viewModel.onFilterButtonBarClicked()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
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

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.newRecipeFragment -> binding.navigationMenu.visibility = View.GONE
                R.id.listFilterFragment -> binding.navigationMenu.visibility = View.GONE
                R.id.feedRecipesFragment -> {
                    tabName = TabName.ALL
                    binding.navigationMenu.visibility = View.VISIBLE
                }

                R.id.feedFavoriteRecipesFragment -> {
                    tabName = TabName.FAVORITE
                    binding.navigationMenu.visibility = View.VISIBLE
                }

                R.id.singleRecipeFragment -> {
                    binding.navigationMenu.visibility = View.GONE
                }

                else -> binding.navigationMenu.visibility = View.VISIBLE
            }
        }
        binding.navigationMenu.clearFocus()
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

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        query?.let(::searchDatabase)
        return true
    }

    private fun searchDatabase(query: String) {
        // %" "% because our custom sql query will require that
        val searchQuery = "%$query%"
        viewModel.updateDatabaseByParams(searchQuery, tabName)
    }

    companion object {
        var Bundle.textArg: String? by StringArg
    }
}
