package com.zoe.chatdemo;

import java.util.Random;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;

public class LoginActivity extends Activity {
	private EditText account;
	private EditText pwd;
	private Button login;
	private Button register;
	private ProgressDialog progressDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		account = (EditText) findViewById(R.id.et_account);
		pwd = (EditText) findViewById(R.id.et_pwd);
		login = (Button) findViewById(R.id.btn_login);
		register = (Button) findViewById(R.id.btn_register);
		// 登入
		login.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showLoginProgressDialog();
				EMChatManager.getInstance().login(account.getText().toString(),
						pwd.getText().toString(), new EMCallBack() {

							@Override
							public void onError(int arg0, final String errorMsg) {
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										closeLoginProgressDialog();
										Toast.makeText(LoginActivity.this,
												"登录聊天服务器失败：" + errorMsg,
												Toast.LENGTH_SHORT).show();
									}
								});
							}

							@Override
							public void onProgress(int arg0, String arg1) {

							}

							@Override
							public void onSuccess() {
								runOnUiThread(new Runnable() {
									public void run() {
										closeLoginProgressDialog();
										startActivity(new Intent(
												LoginActivity.this,
												MainActivity.class));
										Toast.makeText(LoginActivity.this,
												"登录聊天服务器成功", Toast.LENGTH_SHORT)
												.show();
										finish();
									}
								});
							}
						});
			}
		});
		// 注册
		register.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				account.setText(getAccount());
				pwd.setText("123456");
				CreateAccountTask task = new CreateAccountTask();
				task.execute(account.getText().toString(), "123456");
			}
		});
	}

	private class CreateAccountTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			String userid = params[0];
			String pwd = params[1];
			try {
				EMChatManager.getInstance().createAccountOnServer(userid, pwd);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return userid;
		}
	}

	private void showLoginProgressDialog() {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("正在登陆...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}

	private void closeLoginProgressDialog() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
	}

	private String getAccount() {
		String val = "";
		Random random = new Random();
		for (int i = 0; i < 10; i++) {
			String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
			if ("chat".equalsIgnoreCase(charOrNum)) {
				int choice = random.nextInt(2) % 2 == 0 ? 65 : 97;
				val += (char) (choice + random.nextInt(26));
			} else if ("num".equalsIgnoreCase(charOrNum)) {
				val += String.valueOf(random.nextInt(10));
			}
		}
		return val.toLowerCase();
	}
}
