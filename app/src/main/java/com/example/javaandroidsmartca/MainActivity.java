package com.example.javaandroidsmartca;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.vnpt.smartca.ConfigSDK;
import com.vnpt.smartca.CustomParams;
import com.vnpt.smartca.SmartCAEnvironment;
import com.vnpt.smartca.SmartCALanguage;
import com.vnpt.smartca.SmartCAResultCode;
import com.vnpt.smartca.VNPTSmartCASDK;

import java.io.Serializable;
import java.util.Map;

import kotlin.jvm.JvmOverloads;

public class MainActivity extends AppCompatActivity {
    VNPTSmartCASDK mVNPTSmartCA = new VNPTSmartCASDK();

    EditText editTextTran;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button buttonGetAuth = findViewById(R.id.getAuthID);
        Button buttonMainInfo = findViewById(R.id.btnMainInfo);
        Button buttonSignOut = findViewById(R.id.btnSignOut);
        Button buttonCreate = findViewById(R.id.btnCreateAccount);
        Button btnConfirmTrans = findViewById(R.id.btnConfirmTran);
        editTextTran = findViewById(R.id.plain_text_input);
        CustomParams customParams = new CustomParams();
        customParams.setCustomerId("");
        customParams.setBorderRadiusBtn(999.0);
        customParams.setColorPrimaryBtn("#33CC80");
        customParams.setColorSecondBtn("#DEF7EB");
        customParams.setPackageDefault("PS0");
        customParams.setFeaturesLink("https://www.google.com/?hl=vi");
        ConfigSDK config = new ConfigSDK("4185-637127995547330633.apps.signserviceapi.com",  // clientId
                "NGNhMzdmOGE-OGM2Mi00MTg0",                           // clientSecret
                customParams,                         // env
                SmartCALanguage.INSTANCE.getVI(),                                        // customParams (assuming this is a HashMap or similar)
                SmartCAEnvironment.INSTANCE.getDEMO_ENV(),                                   // lang
                false                                                // isFlutter
        );
        mVNPTSmartCA.initSDK(this, config);
        buttonGetAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAuthentication();
            }
        });
        buttonMainInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getMainInfo();
            }
        });
        buttonSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                singOut();
            }
        });
        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount();
            }
        });
        btnConfirmTrans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmTrans(editTextTran.getText().toString());
            }
        });

    }

    private void confirmTrans(String transId) {
        if (transId.isEmpty()) {
            editTextTran.setError("Vui lòng điền Id giao dịch");
            return;
        }
        mVNPTSmartCA.getWaitingTransaction(transId,
                smartCAResult -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Xác thực thành công");
                    builder.setMessage("status:" + smartCAResult.getStatus() + "statusDesc:" + smartCAResult.getStatusDesc());
                    builder.setPositiveButton("Close", (dialog, which) -> dialog.dismiss());
                    builder.show();
                    if (smartCAResult.getStatus() == SmartCAResultCode.INSTANCE.getSUCCESS_CODE()) {
                        // Xử lý khi confirm thành công
                    } else {
                        // Xử lý khi confirm thất bại
                    }
                    return null;
                });
    }

    private void getAuthentication() {
        try {
            mVNPTSmartCA.getAuthentication(smartCAResult -> {
                if (smartCAResult.getStatus() == SmartCAResultCode.INSTANCE.getNO_EXIST_CERT_VALID()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Thông báo");
                    builder.setMessage(smartCAResult.getData().toString());

                    builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            getMainInfo();
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();

                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            getMainInfo();
                        }
                    }, 5000);
                }
                if (smartCAResult.getStatus() == SmartCAResultCode.INSTANCE.getSUCCESS_CODE()) {
                    try {
                        CallbackResult obj;
                        String jsonString = smartCAResult.getData().toString();
                        Gson gson = new Gson();
                        obj = gson.fromJson(jsonString, CallbackResult.class);

                        String token = obj.getAccessToken();
                        String credentialId = obj.getCredentialId();

                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("Xác thực thành công");
                        builder.setMessage("CredentialId: " + credentialId + ";\nAccessToken: " + token);
                        builder.setPositiveButton("Close", (dialog, which) -> dialog.dismiss());
                        builder.show();
                    } catch (JsonParseException e) {
                        // Handle JSON parsing exception
                        e.printStackTrace();
                        // Show error message to user
                    }
                }
                return null;
            });
        } catch (Exception ex) {
            throw ex;
        }
    }

    private void getMainInfo() {
        try {
            mVNPTSmartCA.getMainInfo(smartCAResult -> {
                if (smartCAResult.getStatus() == SmartCAResultCode.INSTANCE.getSUCCESS_CODE()) {
                    // Xử lý khi confirm thành công
                } else {
                    // Xử lý khi confirm thất bại
                }

                return null;
            });
        } catch (Exception ex) {
            throw ex;
        }
    }

    private void singOut() {
        try {
            mVNPTSmartCA.signOut(smartCAResult -> {
                if (smartCAResult.getStatus() == SmartCAResultCode.INSTANCE.getSUCCESS_CODE()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Thông báo");
                    builder.setMessage("Đăng xuất thành công");
                    builder.setPositiveButton("Close", (dialog, which) -> dialog.dismiss());
                    builder.show();
                } else {
                    // Xử lý khi confirm thất bại
                }
                return null;
            });
        } catch (Exception ex) {
            throw ex;
        }
    }

    private void createAccount() {
        try {
            mVNPTSmartCA.createAccount(smartCAResult -> {
                if (smartCAResult.getStatus() == SmartCAResultCode.INSTANCE.getSUCCESS_CODE()) {
                    // Xử lý khi confirm thành công
                } else {
                    // Xử lý khi confirm thất bại
                }
                return null;
            });
        } catch (Exception ex) {
            throw ex;
        }
    }
}
