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

    /**
     * This method will check the authentication state of the current user,
     * if he is connected and has a valid account and prompt the appropriate dialog
     *
     * @param ctx            Activity context where this method is called
     * @param cancelListener Listener that will do a precise action if the user cancels the prompted dialog
     */
    public static void checkVerifieddAuth(Activity ctx, DialogInterface.OnCancelListener cancelListener) {
        //check if user is logged in, otherwise prompt sign in
        if (!AuthProvider.getInstance().isConnected()) {
            promptConnection(ctx, cancelListener);
        } else {
            // check if user's account is verified, otherwise prompt verification and/or refresh
            if (!AuthProvider.getInstance().isEmailVerified()) {
                promptRetry(ctx, cancelListener);
            }
        }

    }

    /**
     * This methods checks only if the user is connected
     *
     * @param ctx            Activity context where this check is requested
     * @param cancelListener WHat to do if user clicks return button
     */
    public static void checkLoggedAuth(Activity ctx, DialogInterface.OnCancelListener cancelListener) {
        //check if user is logged in, otherwise prompt sign in
        if (!AuthProvider.getInstance().isConnected()) {
            promptConnection(ctx, cancelListener);
        }
    }

    /**
     * Takes the user to the home activity
     *
     * @param ctx Activity context where the user calls this function
     */
    public static void goHome(Activity ctx) {
        // we only leave the context if we want to contribute in add poi activity (edit also)
        Intent home = new Intent(ctx, HomeActivity.class);
        home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        ctx.startActivity(home);
        ctx.finish();

    }

    /**
     * Prompts the user with a dialog where he can go to a sign in activity
     *
     * @param ctx           Activity context where the dialog will popup
     * @param clickListener Listener that knows what to when user clicks on return button
     */
    private static void promptConnection(final Activity ctx, final DialogInterface.OnCancelListener clickListener) {
        /*
        Intent i = new Intent(ctx, SignInActivity.class);
        ctx.startActivityForResult(i, ActivityCodes.SIGNIN_REQUEST);
   */
        // create dialog for sign in prompt
        // Instantiate an AlertDialog.Builder with its constructor
        final AlertDialog.Builder promptBuilder = new AlertDialog.Builder(ctx);
        // user cannot proceed if not email verified
        promptBuilder.setCancelable(false);


        // Sets up the dialog builder
        promptBuilder.setMessage("You need to be connected to Fiktion if you want to proceed!")
                .setTitle("Fiktion")
                .setNegativeButton("Return", null)
                .setPositiveButton("Sign In", null);
        // Get the dialog that confirms if user wants to permanently delete his account
        final AlertDialog verifyDialog;
        verifyDialog = promptBuilder.create();
        verifyDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clickListener.onCancel(dialog);
                        verifyDialog.cancel();
                    }
                });
                ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(ctx, SignInActivity.class);
                        ctx.startActivityForResult(i, ActivityCodes.SIGNIN_REQUEST);
                    }
                });
            }
        });
        verifyDialog.show();
        verifyDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
        verifyDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(Color.BLUE);

    }


    /**
     * Prompts the user with a dialog where he can send an email verification , refresh or dismiss dialog
     *
     * @param ctx           Activity context where the dialog will popup
     * @param clickListener Listener that knows what to when user clicks on return button
     */
    private static void promptRetry(final Activity ctx, final DialogInterface.OnCancelListener clickListener) {
        // Instantiate an AlertDialog.Builder with its constructor
        final AlertDialog.Builder refreshBuilder = new AlertDialog.Builder(ctx);
        // user cannot proceed if not email verified
        refreshBuilder.setCancelable(false);


        // Sets up the dialog builder
        refreshBuilder.setMessage("You need to verify your Fiktion account!")
                .setTitle("Fiktion")
                .setNegativeButton("Return", null)
                .setPositiveButton("Verify", null)
                .setNeutralButton("Refresh", null);
        // Get the dialog that confirms if user wants to permanently delete his account
        final AlertDialog verifyDialog;
        verifyDialog = refreshBuilder.create();
        verifyDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clickListener.onCancel(dialog);
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
