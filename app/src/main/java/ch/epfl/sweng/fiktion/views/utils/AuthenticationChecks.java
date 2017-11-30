package ch.epfl.sweng.fiktion.views.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;

import ch.epfl.sweng.fiktion.providers.AuthProvider;
import ch.epfl.sweng.fiktion.views.HomeActivity;
import ch.epfl.sweng.fiktion.views.SignInActivity;

/** This class has all functions needed to enable authentication checks in views
 * Created by Rodrigo on 30.11.2017.
 */

public class AuthenticationChecks {

    public static void checkAuthState(Activity ctx){
        //check if user is logged in, otherwise prompt sign in
        if (!AuthProvider.getInstance().isConnected()) {
            promptConnection(ctx);
        }
        else {
            // check if user's account is verified
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
        // Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder connectionBuilder = new AlertDialog.Builder(ctx);
        // user cannot proceed if not connected
        connectionBuilder.setCancelable(false);

        // Sets up the dialog builder
        connectionBuilder.setMessage("Only signed in users with valid accounts can contribute to Fiktion!")
                .setTitle("Fiktion")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        goHome(ctx);
                        dialog.cancel();
                    }
                })
                .setPositiveButton("Sign In", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(ctx, SignInActivity.class);
                        ctx.startActivityForResult(i, ActivityCodes.SIGNIN_REQUEST);
                        dialog.cancel();
                    }
                });
        // Get the dialog that confirms if user wants to permanently delete his account
        AlertDialog promptConnection;
        promptConnection = connectionBuilder.create();
        promptConnection.show();
        promptConnection.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
        promptConnection.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
    }

    private static void promptRetry(final Activity ctx) {
        // Instantiate an AlertDialog.Builder with its constructor
        final AlertDialog.Builder refreshBuilder = new AlertDialog.Builder(ctx);
        // user cannot proceed if not email verified
        refreshBuilder.setCancelable(false);

        // Sets up the dialog builder
        refreshBuilder.setMessage("You need to verify your account before contributing to Fiktion!")
                .setTitle("Fiktion")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        goHome(ctx);
                        dialog.cancel();
                    }
                })
                .setPositiveButton("Refresh", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (AuthProvider.getInstance().isEmailVerified()) {
                            dialog.cancel();
                        } else{
                            AlertDialog newDialog = refreshBuilder.create();
                            newDialog.show();
                            newDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                            newDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
                        }

                    }
                });
        // Get the dialog that confirms if user wants to permanently delete his account
        AlertDialog verifyDialog;
        verifyDialog =refreshBuilder.create();
        verifyDialog.show();
        verifyDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
        verifyDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
    }
}
