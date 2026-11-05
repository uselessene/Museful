package com.example.museful.ui

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.museful.data.SessionViewModel
import com.example.museful.ui.screens.*

object Routes {
    const val CATALOG = "catalog"
    const val HOME = "home"
    const val SERIES = "series"
    const val PROFILE = "profile"
    const val WELCOME = "welcome"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val BOOK = "book"
    const val WRITE_REVIEW = "writeReview"
    const val PROFILE_EDIT = "profileEdit"
    const val LIBRARY = "library"
    const val REVIEWS = "reviews"
    const val QUOTES = "quotes"
    const val WRITE_QUOTE = "writeQuote"
    const val SETTINGS = "settings"
}

@Composable
fun MusefulNavGraph() {
    val navController = rememberNavController()
    val session: SessionViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Routes.CATALOG,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None }
    ) {
        composable(Routes.CATALOG) {
            CatalogScreen(
                onCatalogClick = {},
                onHomeClick = { navController.navigate(Routes.HOME) },
                onSeriesClick = { navController.navigate(Routes.SERIES) },
                onProfileClick = { navController.navigate(Routes.PROFILE) },
                onBookClick = { bookId -> navController.navigate("${Routes.BOOK}?bookId=$bookId") }
            )
        }

        composable(Routes.HOME) {
            if (!session.isLoggedIn) {
                WelcomeScreen(
                    onRegisterClick = { navController.navigate(Routes.REGISTER) },
                    onLoginClick = { navController.navigate(Routes.LOGIN) }
                )
            } else {
                HomeScreen(
                    session = session,
                    onCatalogClick = { navController.navigate(Routes.CATALOG) },
                    onHomeClick = {},
                    onSeriesClick = { navController.navigate(Routes.SERIES) },
                    onProfileClick = { navController.navigate(Routes.PROFILE) },
                    onSeriesDayClick = { navController.navigate(Routes.SERIES) }
                )
            }
        }

        composable(Routes.SERIES) {
            if (!session.isLoggedIn) {
                WelcomeScreen(
                    onRegisterClick = { navController.navigate(Routes.REGISTER) },
                    onLoginClick = { navController.navigate(Routes.LOGIN) }
                )
            } else {
                SeriesScreen(
                    session = session,
                    onCatalogClick = { navController.navigate(Routes.CATALOG) },
                    onHomeClick = { navController.navigate(Routes.HOME) },
                    onSeriesClick = {},
                    onProfileClick = { navController.navigate(Routes.PROFILE) }
                )
            }
        }

        composable(Routes.PROFILE) {
            ProfileScreen(
                session = session,
                onCatalogClick = { navController.navigate(Routes.CATALOG) },
                onHomeClick = { navController.navigate(Routes.HOME) },
                onSeriesClick = { navController.navigate(Routes.SERIES) },
                onProfileClick = {},
                onEditProfileClick = { navController.navigate(Routes.PROFILE_EDIT) },
                onBooksClick = { navController.navigate(Routes.LIBRARY) },
                onReviewsClick = { navController.navigate(Routes.REVIEWS) },
                onQuotesClick = { navController.navigate(Routes.QUOTES) },
                onSettingsClick = { navController.navigate(Routes.SETTINGS) },
                onLoginClick = { navController.navigate(Routes.WELCOME) }
            )
        }

        composable(Routes.PROFILE_EDIT) {
            ProfileEditScreen(session = session, onBackClick = { navController.popBackStack() })
        }

        composable(Routes.LIBRARY) {
            LibraryScreen(
                session = session,
                onBackClick = { navController.popBackStack() },
                onBookClick = { bookId -> navController.navigate("${Routes.BOOK}?bookId=$bookId") }
            )
        }

        composable(Routes.REVIEWS) {
            ReviewsScreen(
                session = session,
                onBackClick = { navController.popBackStack() },
                onEditClick = { bookId -> navController.navigate("${Routes.WRITE_REVIEW}?bookId=$bookId") }
            )
        }

        composable(Routes.QUOTES) {
            QuotesScreen(
                session = session,
                onBackClick = { navController.popBackStack() },
                onWriteQuoteClick = { navController.navigate(Routes.WRITE_QUOTE) }
            )
        }

        composable(Routes.WRITE_QUOTE) {
            WriteQuoteScreen(session = session, onBackClick = { navController.popBackStack() })
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                session = session,
                onBackClick = {
                    navController.navigate(Routes.CATALOG) {
                        popUpTo(Routes.CATALOG) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.WELCOME) {
            WelcomeScreen(
                onRegisterClick = { navController.navigate(Routes.REGISTER) },
                onLoginClick = { navController.navigate(Routes.LOGIN) }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                session = session,
                onLoginClick = { navController.navigate(Routes.LOGIN) },
                onRegisterSuccess = {
                    session.refresh()
                    navController.navigate(Routes.CATALOG) {
                        popUpTo(Routes.CATALOG) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                session = session,
                onRegisterClick = { navController.navigate(Routes.REGISTER) },
                onLoginSuccess = {
                    session.refresh()
                    navController.navigate(Routes.CATALOG) {
                        popUpTo(Routes.CATALOG) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = "${Routes.BOOK}?bookId={bookId}",
            arguments = listOf(navArgument("bookId") { type = NavType.LongType })
        ) { backStackEntry ->
            val bookId = backStackEntry.arguments?.getLong("bookId") ?: return@composable
            BookScreen(
                session = session,
                bookId = bookId,
                onBackClick = { navController.popBackStack() },
                onWriteReviewClick = { navController.navigate("${Routes.WRITE_REVIEW}?bookId=$bookId") },
                onRequireAuth = { navController.navigate(Routes.WELCOME) }
            )
        }

        composable(
            route = "${Routes.WRITE_REVIEW}?bookId={bookId}",
            arguments = listOf(navArgument("bookId") { type = NavType.LongType })
        ) { backStackEntry ->
            val bookId = backStackEntry.arguments?.getLong("bookId") ?: return@composable
            WriteReviewScreen(bookId = bookId, onBackClick = { navController.popBackStack() })
        }
    }
}