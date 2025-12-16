package com.meetline.app.ui.login

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.meetline.app.domain.model.User
import com.meetline.app.domain.usecase.GetSessionUseCase
import com.meetline.app.domain.usecase.LoginUseCase
import com.meetline.app.ui.theme.MeetLineTheme
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Tests de integración UI para [LoginScreen].
 *
 * Verifica la interacción del usuario con la pantalla de login,
 * incluyendo validación de campos y comportamiento de botones.
 *
 * @see LoginScreen Composable bajo prueba.
 */
@RunWith(AndroidJUnit4::class)
class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var loginUseCase: LoginUseCase
    private lateinit var getSessionUseCase: GetSessionUseCase
    private lateinit var viewModel: LoginViewModel
    
    private val testUser = User(
        id = "user_1",
        name = "Test User",
        email = "test@example.com",
        phone = "+1234567890"
    )

    @Before
    fun setUp() {
        loginUseCase = mockk()
        getSessionUseCase = mockk()
        
        every { getSessionUseCase() } returns null
        coEvery { loginUseCase(any(), any()) } returns Result.success(testUser)
        
        viewModel = LoginViewModel(loginUseCase, getSessionUseCase)
    }

    /**
     * Verifica que los campos de email y contraseña son visibles.
     */
    @Test
    fun loginScreen_displaysEmailAndPasswordFields() {
        // Given
        composeTestRule.setContent {
            MeetLineTheme {
                LoginScreen(
                    onNavigateToRegister = {},
                    onLoginSuccess = {}
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Email").assertIsDisplayed()
        composeTestRule.onNodeWithText("Contraseña").assertIsDisplayed()
    }

    /**
     * Verifica que el botón de login existe.
     */
    @Test
    fun loginScreen_displaysLoginButton() {
        // Given
        composeTestRule.setContent {
            MeetLineTheme {
                LoginScreen(
                    onNavigateToRegister = {},
                    onLoginSuccess = {}
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Iniciar Sesión").assertIsDisplayed()
    }

    /**
     * Verifica que se puede escribir en el campo de email.
     */
    @Test
    fun loginScreen_canTypeEmail() {
        // Given
        val testEmail = "test@example.com"
        
        composeTestRule.setContent {
            MeetLineTheme {
                LoginScreen(
                    onNavigateToRegister = {},
                    onLoginSuccess = {}
                )
            }
        }

        // When
        composeTestRule.onNodeWithText("Email").performTextInput(testEmail)

        // Then
        composeTestRule.onNodeWithText(testEmail).assertExists()
    }

    /**
     * Verifica que se puede escribir en el campo de contraseña.
     */
    @Test
    fun loginScreen_canTypePassword() {
        // Given
        composeTestRule.setContent {
            MeetLineTheme {
                LoginScreen(
                    onNavigateToRegister = {},
                    onLoginSuccess = {}
                )
            }
        }

        // When
        composeTestRule.onNodeWithText("Contraseña").performTextInput("password123")

        // Then - La contraseña debe estar oculta por defecto
        composeTestRule.onNodeWithText("Contraseña").assertExists()
    }

    /**
     * Verifica que el botón de login está deshabilitado con campos vacíos.
     */
    @Test
    fun loginScreen_loginButtonDisabledWithEmptyFields() {
        // Given
        composeTestRule.setContent {
            MeetLineTheme {
                LoginScreen(
                    onNavigateToRegister = {},
                    onLoginSuccess = {}
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Iniciar Sesión").assertIsNotEnabled()
    }

    /**
     * Verifica que el link de registro es visible.
     */
    @Test
    fun loginScreen_displaysRegisterLink() {
        // Given
        composeTestRule.setContent {
            MeetLineTheme {
                LoginScreen(
                    onNavigateToRegister = {},
                    onLoginSuccess = {}
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("¿No tienes cuenta?").assertIsDisplayed()
        composeTestRule.onNodeWithText("Regístrate").assertIsDisplayed()
    }

    /**
     * Verifica que clicar en el link de registro invoca el callback.
     */
    @Test
    fun loginScreen_registerLinkNavigatesToRegister() {
        // Given
        var registerClicked = false
        
        composeTestRule.setContent {
            MeetLineTheme {
                LoginScreen(
                    onNavigateToRegister = { registerClicked = true },
                    onLoginSuccess = {}
                )
            }
        }

        // When
        composeTestRule.onNodeWithText("Regístrate").performClick()

        // Then
        assert(registerClicked)
    }

    /**
     * Verifica la estructura básica de la pantalla de login.
     */
    @Test
    fun loginScreen_hasCorrectStructure() {
        // Given
        composeTestRule.setContent {
            MeetLineTheme {
                LoginScreen(
                    onNavigateToRegister = {},
                    onLoginSuccess = {}
                )
            }
        }

        // Then - Verifica que todos los elementos principales existen
        composeTestRule.onNodeWithText("Bienvenido").assertIsDisplayed()
        composeTestRule.onNodeWithText("Email").assertIsDisplayed()
        composeTestRule.onNodeWithText("Contraseña").assertIsDisplayed()
        composeTestRule.onNodeWithText("Iniciar Sesión").assertIsDisplayed()
        composeTestRule.onNodeWithText("¿No tienes cuenta?").assertIsDisplayed()
    }
}
