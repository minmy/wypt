package cn.sini.cgb.common.wxpay;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import cn.sini.cgb.common.util.Environment;

public class PayConfig extends WXPayConfig{


			
	@Override
	public String getAppID() {
		return Environment.getProperty("appId");
	}

	@Override
	public String getMchID() {
		return Environment.getProperty("mchId");
	}

	@Override
	public String getKey() {
		return Environment.getProperty("apiKey");
	}

	@Override
	public InputStream getCertStream() {
		FileInputStream instream = null;
		try {
			instream = new FileInputStream(Environment.getProperty("certPath")+"/8kjnojse8ISAKMDA2FS278.p12");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
        return instream;
	}
	@Override
    public int getHttpConnectTimeoutMs() {
        return 8000;
    }
 
    @Override
    public int getHttpReadTimeoutMs() {
        return 10000;
    }

	@Override
	public IWXPayDomain getWXPayDomain() {
        return new IWXPayDomain() {
            @Override
            public void report(String domain, long elapsedTimeMillis, Exception ex) {
            }
 
            @Override
            public DomainInfo getDomain(WXPayConfig config) {
                return new DomainInfo("api.mch.weixin.qq.com", false);
            }
        };
    }

}
