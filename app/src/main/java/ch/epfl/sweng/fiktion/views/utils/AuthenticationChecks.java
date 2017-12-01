package ch.epfl.sweng.fiktion.views.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.Toast;

import ch.epfl.sweng.fiktion.providers.AuthProvider;
import ch.epfl.sweng.fiktion.views.HomeActivity;
import ch.epfl.sweng.fiktion.views.SignInActivity;

/**
 * This class has all functions needed to enable authentication checks in views
 * Created by Rodrigo on 30.11.2017.
 */

public class AuthenticationChecks {

    public static void checkAuthState(Activity ctx) {
        //check if user is logged in, otherwise prompt sign in
        if (!AuthProvider.getInstance().isConnected()) {
            promptConnection(ctx);
        } else {
            // check if user's account is verified, otherwise prompt verification and/or refresh
            if (!AuthProvider.getInstance().isEmailVerified()) {
                promptRetry(ctx);
            }
        }
    }

    private static void goHome(Activity ctx) {
        Intent home = new Intent(ctx, HomeActivity.class);
        home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        ctx.startActivity(home);
        ctx.finish();
    }

    /**
     * Takes the user to a sign in activity where he can sign in or register a new account
     */
    private static void promptConnection(final Activity ctx) {
        Intent i = new Intent(ctx, SignInActivity.class);
        ctx.startActivityForResult(i, ActivityCodes.SIGNIN_REQUEST);
    }

    private static void promptRetry(final Activity ctx) {
        // Instantiate an AlertDialog.Builder with its constructor
        final AlertDialog.Builder refreshBuilder = new AlertDialog.Builder(ctx);
        // user cannot proceed if not email verified
        refreshBuilder.setCancelable(false);


        // Sets up the dialog builder
        refreshBuilder.setMessage("You need to verify your Fiktion account!")
                .setTitle("Fiktion")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Verify", null)
                .setNeutralButton("Refresh", null);
        // Get the dialog that confirms if user wants to permanently delete his account
        final AlertDialog verifyDialog;
        verifyDialog = refreshBuilder.create();
        verifyDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        verifyDialog.cancel();
                    }
                });
                ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AuthProvider.getInstance().sendEmailVerification(new AuthProvider.AuthListener() {
                            @Override
                            public void onSuccess() {
                                Toast.makeText(ctx,
                                        "Verification email sent",
                                        Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure() {
                                Toast.makeText(ctx,
                                        "Failed to send verification email.",
                                        Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                });
                ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (AuthProvider.getInstance().isEmailVerified()) {
                            verifyDialog.cancel();
                        }
                    }
                });
            }
        });
        verifyDialog.show();
        verifyDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
        verifyDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
    }
}
