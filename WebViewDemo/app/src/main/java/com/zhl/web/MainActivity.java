package com.zhl.web;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.zhl.web.dao.HistoryDao;
import com.zhl.web.entry.SurfaceHistroy;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class MainActivity extends Activity implements OnClickListener {
	WebView mWebView;
	ImageView net_start, btn_home;
	AutoCompleteTextView net_Address;
	ProgressBar mprogressBar;
	ArrayAdapter<String> histroy_adapter;

	/**
	 * 浏览地址历史
	 */
	ArrayList<String> histroy_list = new ArrayList<String>();
	/**
	 * 用户输入的地址
	 */
	String address;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_PROGRESS);
		setContentView(R.layout.activity_main);
		initView();
		updateHistroy();
	}

	private void initView() {
		// TODO Auto-generated method stub

		mprogressBar = (ProgressBar) this.findViewById(R.id.mProgress);
		btn_home = (ImageView) this.findViewById(R.id.home);
		btn_home.setOnClickListener(this);
		net_Address = (AutoCompleteTextView) this.findViewById(R.id.net_address);
		
		mWebView = (WebView) this.findViewById(R.id.web);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setBuiltInZoomControls(true);
		mWebView.getSettings().setUseWideViewPort(true);
		mWebView.getSettings().setRenderPriority(RenderPriority.HIGH);
		mWebView.setInitialScale(25);
		// mWebView.getSettings().setBlockNetworkImage(true);
		mWebView.addJavascriptInterface(new WebAppInterface(this), "Android");
		mWebView.loadData("", "text/html", null);
		mWebView.loadUrl("javascript:alert(injectedObject.toString())");
		// mWebView.setInitialScale(39);

		mWebView.setWebViewClient(new MyWebViewClient(mprogressBar));
		mWebView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				// TODO Auto-generated method stub
				// MainActivity.this.setProgress(newProgress * 100);
				mprogressBar.setProgress(newProgress);
			}
		});
		net_start = (ImageView) this.findViewById(R.id.start);
		net_Address.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				// TODO Auto-generated method stub
				if (actionId == EditorInfo.IME_ACTION_GO) {
					startNet();
					InputMethodManager inputManager = (InputMethodManager) MainActivity.this
							.getSystemService(INPUT_METHOD_SERVICE);
					inputManager.hideSoftInputFromWindow(
							net_Address.getWindowToken(), 0);
					return true;
				}
				return false;
			}
		});
		net_start.setOnClickListener(this);
//		mWebView.loadUrl("http://www.baidu.com");
		mWebView.loadUrl("http://dc15858.com/");
	}

	/**
	 * 得到浏览历史
	 * 
	 * @return
	 */
	private void updateHistroy() {
		histroy_list = new HistoryDao(this).getHistroy();
		histroy_adapter=new ArrayAdapter<String>(this,R.layout.auto_compleate_item, histroy_list);
		net_Address.setAdapter(histroy_adapter);
		net_Address.setThreshold(1);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.home:
			
			mWebView.loadUrl("http://dc15858.com/");
			break;
		case R.id.start:
			startNet();
			break;

		default:
			break;
		}
	}

	private void startNet() {
		// TODO Auto-generated method stub
		address = net_Address.getText().toString();
		if (null != address && !"".equals(address)) {
			if (!address.startsWith("http://")) {
				if (address.startsWith("www.")) {
					address = "http://" + address;
					mWebView.loadUrl(address);
				} else {
					try {
						// address = URLEncoder.encode(address, "gb2312");
						mWebView.loadUrl("http://www.baidu.com.cn/s?wd="
								+ URLEncoder.encode(address, "gb2312"));
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} else {
				mWebView.loadUrl(address);
			}
			// 保存浏览历史
			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					SurfaceHistroy histroy = new SurfaceHistroy();
					histroy.setDzmc(address);
					histroy.setRealAddress(address);
					new HistoryDao(MainActivity.this)
							.saveSurfaceHistroy(histroy);
				}
			}).start();
			updateHistroy();
		} else {
			Toast.makeText(this, "请先输入网址", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
			mWebView.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
