package com.zoe.chatdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.LocationMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.VoiceMessageBody;

public class MainActivity extends ActionBarActivity {
	private EditText tvMsg;
	private TextView tvReceivedMsg;

	private NewMessageBroadcastReceiver msgReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		tvMsg = (EditText) findViewById(R.id.et_msg);
		tvReceivedMsg = (TextView) findViewById(R.id.tv_receive_msg);

		msgReceiver = new NewMessageBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter(EMChatManager
				.getInstance().getNewMessageBroadcastAction());
		registerReceiver(msgReceiver, intentFilter);
		// 初始化完成
		EMChat.getInstance().setAppInited();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void onSendTxtMsg(View view) {
		EMMessage msg = EMMessage.createSendMessage(EMMessage.Type.TXT);
		msg.setReceipt("bot");
		TextMessageBody body = new TextMessageBody(tvMsg.getText().toString());
		msg.addBody(body);
		// 添加扩展属性
		msg.setAttribute("extStringAttr", "String Test Value");

		try {
			EMChatManager.getInstance().sendMessage(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onDestroy() {
		if (msgReceiver != null) {
			try {
				unregisterReceiver(msgReceiver);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		super.onDestroy();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private class NewMessageBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String msgId = intent.getStringExtra("msgid"); // 获取消息id
			EMMessage message = EMChatManager.getInstance().getMessage(msgId);
			switch (message.getType()) {
			case TXT:
				TextMessageBody txtBody = (TextMessageBody) message.getBody();
				tvReceivedMsg.append("从" + message.getFrom() + "获得消息:"
						+ txtBody.getMessage() + "\n\r");
				break;
			case IMAGE:
				ImageMessageBody imgBody = (ImageMessageBody) message.getBody();
				tvReceivedMsg.append("从" + message.getFrom() + "获得图片:"
						+ imgBody.getThumbnailUrl() + "\t"
						+ imgBody.getRemoteUrl() + "\n\r");
				break;
			case VOICE:
				VoiceMessageBody voiceBody = (VoiceMessageBody) message
						.getBody();
				tvReceivedMsg.append("从" + message.getFrom() + "获得声音:"
						+ voiceBody.getLength() + "\t"
						+ voiceBody.getRemoteUrl() + "\n\r");
				break;
			case LOCATION:
				LocationMessageBody locationBody = (LocationMessageBody) message
						.getBody();
				tvReceivedMsg.append("从" + message.getFrom() + "获得:"
						+ locationBody.getAddress() + "\n\r");
				break;

			}
		}
	}
}
