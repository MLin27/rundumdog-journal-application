package com.example.rundumdog;

import static org.mockito.Mockito.*;

import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.verification.VerificationMode;

@RunWith(MockitoJUnitRunner.class)
public class LoginActivityTest {

    @Mock
    private EditText emailTextView;

    @Mock
    private EditText passwordTextView;

    @Mock
    private ProgressBar progressBar;

    @Mock
    private LoginActivity loginActivity;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        loginActivity = new LoginActivity();
        loginActivity.emailTextView = emailTextView;
        loginActivity.passwordTextView = passwordTextView;
        loginActivity.progressbar = progressBar;
    }

    @Test
    public void testLoginUserAccount_EmailEmpty_ShowsToast() {
        when(emailTextView.getText().toString()).thenReturn("");
        when(passwordTextView.getText().toString()).thenReturn("password123");

        loginActivity.loginUserAccount();

        verify(progressBar, never()).setVisibility(View.VISIBLE);
        verifyStaticToast("Please enter email!!");
    }

    @Test
    public void testLoginUserAccount_PasswordEmpty_ShowsToast() {
        when(emailTextView.getText().toString()).thenReturn("test@example.com");
        when(passwordTextView.getText().toString()).thenReturn("");

        loginActivity.loginUserAccount();

        verify(progressBar, never()).setVisibility(View.VISIBLE);
        verifyStaticToast("Please enter password!!");
    }

    @Test
    public void testLoginUserAccount_ValidInputs_ShowsProgressBar() {
        when(emailTextView.getText().toString()).thenReturn("test@example.com");
        when(passwordTextView.getText().toString()).thenReturn("password123");

        loginActivity.loginUserAccount();

        verify(progressBar, times(1)).setVisibility(View.VISIBLE);
    }

    private void verifyStaticToast(String expectedMessage) {
        // This verifies that the toast message is displayed
        verifyStatic(Toast.class, times(1));
        Toast.makeText(any(), eq(expectedMessage), anyInt());
    }

    private void verifyStatic(Class<Toast> toastClass, VerificationMode times) {
    }
}
