package com.example.bluetooth.fragment;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bluetooth.R;
import com.smailnet.eamil.Email;

public class GetInTouch extends AppCompatActivity {

    private EditText editTextFirstName;
    private EditText editTextLastName;
    private EditText editTextEmailAddress;
    private EditText editTextMessage;
    private Button gitpopupButton;
    private View gitpopupView;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_in_touch);

        gitpopupView = getLayoutInflater().inflate(R.layout.get_in_touch_popup, null, false);
        gitpopupButton = gitpopupView.findViewById(R.id.popup03);

        editTextFirstName = findViewById(R.id.editTextFirstName);
        editTextLastName = findViewById(R.id.editTextLastName);
        editTextEmailAddress = findViewById(R.id.editTextEmailAddress);
        editTextMessage = findViewById(R.id.editTextMessage);
        submitButton = findViewById(R.id.submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail();
            }
        });
    }

    private void sendEmail() {
        Email.initialize(this);
        String firstName = editTextFirstName.getText().toString();
        String lastName = editTextLastName.getText().toString();
        String emailAddress = editTextEmailAddress.getText().toString();
        String message = editTextMessage.getText().toString();

        Email.Config config = new Email.Config()
                .setMailType(Email.MailType.QQ)
                .setAccount("1216533303@qq.com")
                .setPassword("ycxwnrffpspdbadi");

        Email.getSendService(config)
                .setTo("zceemc1@ucl.ac.uk")
                .setNickname(firstName + " " + lastName)
                .setSubject("Get in Touch - Message from " + firstName + " " + lastName)
                .setText("Name: " + firstName + " " + lastName + "\nEmail: " + emailAddress + "\n\nMessage:\n" + message)
                .send(new Email.GetSendCallback() {
                    @Override
                    public void onSuccess() {
                        showGitPopupWindow();
                    }

                    @Override
                    public void onFailure(String msg) {
                        Log.i(TAG, "Error：" + msg);
                    }
                });
    }

    private void showGitPopupWindow() {
        int popupWidth = ViewGroup.LayoutParams.MATCH_PARENT;
        int popupHeight = ViewGroup.LayoutParams.MATCH_PARENT;

        // Create the popup window
        PopupWindow pW = new PopupWindow(
                gitpopupView,
                popupWidth,
                popupHeight,
                true);

        // Check if the popupWindow is not null before attempting to show
        if (pW != null) {
            // Show the popup window
            View parentView = findViewById(android.R.id.content);
            pW.showAtLocation(parentView, Gravity.CENTER, 0, 0);
            // Set a listener to dismiss the popup window when the "Cancel" button is clicked
            gitpopupButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pW.dismiss(); // Dismiss the popup window
                }
            });
        }
    }
}





