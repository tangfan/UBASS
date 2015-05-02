package com.unionpay.upa.excel.export;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.unionpay.upa.excel.export.model.ErrorstatModel;
import com.unionpay.upa.excel.export.model.ExportConfig;
import com.unionpay.upa.excel.export.model.InvalidorderstatModel;
import com.unionpay.upa.excel.export.utils.FileUtils;

/**
 * 读取CVS文件
 */
public class ReadCSV<T> {

	private List<T> UPAModels = new ArrayList<T>();

	public List<T> getUPAModel(InputStream cvsInput, String file) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(cvsInput, "UTF-8"));
			// 读取直到最后一行
			String line = "";
			if (file.contains(ExportConfig.CLASS_NAME_INPUTCLICKSTAT)) {
				ErrorstatModel model = null;
				while ((line = br.readLine()) != null) {
					model = new ErrorstatModel();
					// 把一行数据分割成多个字段
					StringTokenizer st = new StringTokenizer(line, "\t");
					while (st.hasMoreTokens()) {
						model.setDate(st.nextToken());
						model.setMerabbr(st.nextToken());
						model.setMerchantcd(st.nextToken());
						model.setInputCreditCard(Integer.parseInt(st
								.nextToken()));
						model.setInputDebitCard(Integer.parseInt(st.nextToken()));
						model.setInputOrtherCard(Integer.parseInt(st
								.nextToken()));model.setCardNoNotSupported(Integer.parseInt(st
								.nextToken()));
						
						model.setLoginNo(Integer.parseInt(st.nextToken()));
						model.setLoginFailedNo(Integer.parseInt(st.nextToken()));
						model.setRegisterNo(Integer.parseInt(st.nextToken()));
						model.setRegisterFailedNo(Integer.parseInt(st
								.nextToken()));
						model.setSendSMSNo(Integer.parseInt(st.nextToken()));
						model.setSMSValidateErrorNo(Integer.parseInt(st
								.nextToken()));
						model.setCaptchaErrorNo(Integer.parseInt(st.nextToken()));
					}
					UPAModels.add((T) model);
				}
			} else if (file.contains(ExportConfig.CLASS_NAME_INVALIDORDERSTAT)) {
				InvalidorderstatModel model = null;
				while ((line = br.readLine()) != null) {
					model = new InvalidorderstatModel();
					// 把一行数据分割成多个字段
					StringTokenizer st = new StringTokenizer(line, "\t");
					while (st.hasMoreTokens()) {
						model.setMerabbr(st.nextToken());
						model.setMerchantcd(st.nextToken());
						model.setNoopration(Integer.parseInt(st.nextToken()));
						model.setNooprationper(Double.parseDouble(st.nextToken()));
						model.setNotfoundcardbin(Integer.parseInt(st.nextToken()));
						model.setNotfoundcardbinper(Double.parseDouble(st.nextToken()));
						model.setBorrowingrejected(Integer.parseInt(st.nextToken()));
						model.setBorrowingrejectedper(Double.parseDouble(st.nextToken()));
						model.setNotfoundbankconfig(Integer.parseInt(st.nextToken()));
						model.setNotfoundbankconfigper(Double.parseDouble(st.nextToken()));
						model.setNotsupportbankchannel(Integer.parseInt(st.nextToken()));
						model.setNotsupportbankchannelper(Double.parseDouble(st.nextToken()));
						model.setCardvalidationother(Integer.parseInt(st.nextToken()));
						model.setCardvalidationotherper(Double.parseDouble(st.nextToken()));
						
						model.setCardvalidationdebitsucc(Integer.parseInt(st.nextToken()));
						model.setCardvalidationdebitsuccper(Double.parseDouble(st.nextToken()));
						model.setCardvalidationcreditsucc(Integer.parseInt(st.nextToken()));
						model.setCardvalidationcreditsuccper(Double.parseDouble(st.nextToken()));
						model.setCardvalidationquasisucc(Integer.parseInt(st.nextToken()));
						model.setCardvalidationquasisuccper(Double.parseDouble(st.nextToken()));
						model.setCardvalidationothersucc(Integer.parseInt(st.nextToken()));
						model.setCardvalidationothersuccper(Double.parseDouble(st.nextToken()));
						
						model.setUserlogin(Integer.parseInt(st.nextToken()));
						model.setUserloginper(Double.parseDouble(st.nextToken()));
						model.setUserloginsucc(Integer.parseInt(st.nextToken()));
						model.setUserloginsuccper(Double.parseDouble(st.nextToken()));
						model.setUserregistration(Integer.parseInt(st.nextToken()));
						model.setUserregistrationper(Double.parseDouble(st.nextToken()));
						
						model.setUserregistrationsucc(Integer.parseInt(st.nextToken()));
						model.setUserregistrationsuccper(Double.parseDouble(st.nextToken()));
						model.setBanktips(Integer.parseInt(st.nextToken()));
						model.setBanktipsper(Double.parseDouble(st.nextToken()));
						
						model.setSmssendfreq(Integer.parseInt(st.nextToken()));
						model.setSmssendfreqper(Double.parseDouble(st.nextToken()));
						model.setSmssendsucc(Integer.parseInt(st.nextToken()));
						model.setSmssendsuccper(Double.parseDouble(st.nextToken()));
						
						model.setSmsvalidation(Integer.parseInt(st.nextToken()));
						model.setSmsvalidationper(Double.parseDouble(st.nextToken()));
						model.setCaptchavalidation(Integer.parseInt(st.nextToken()));
						model.setCaptchavalidationper(Double.parseDouble(st.nextToken()));
						
						model.setOpencard(Integer.parseInt(st.nextToken()));
						model.setOpencardper(Double.parseDouble(st.nextToken()));
						model.setAuthentication(Integer.parseInt(st.nextToken()));
						model.setAuthenticationper(Double.parseDouble(st.nextToken()));
						
						model.setPasserror(Integer.parseInt(st.nextToken()));
						model.setPasserrorper(Double.parseDouble(st.nextToken()));
						model.setNotinservice(Integer.parseInt(st.nextToken()));
						model.setNotinserviceper(Double.parseDouble(st.nextToken()));
						
						model.setNotleaveno(Integer.parseInt(st.nextToken()));
						model.setNotleavenoper(Double.parseDouble(st.nextToken()));
						model.setOpencardother(Integer.parseInt(st.nextToken()));
						model.setOpencardotherper(Double.parseDouble(st.nextToken()));
						
						model.setOther(Integer.parseInt(st.nextToken()));
						model.setOtherper(Double.parseDouble(st.nextToken()));
						model.setInvalidorders(Integer.parseInt(st.nextToken()));
						model.setInvalidOrdersPer(Double.parseDouble(st.nextToken()));
                        model.setSacqcode(st.nextToken());
                        model.setPcPer(Double.parseDouble(st.nextToken()));
                        model.setMobilePer(Double.parseDouble(st.nextToken()));
                        model.setPadPer(Double.parseDouble(st.nextToken()));
                        model.setOrdersubPer(Double.parseDouble(st.nextToken()));
						//st.nextToken();//最后一个日期，不做处理,用于结束本次循环
					}
					UPAModels.add((T) model);
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			FileUtils.close(br);
		}
		return UPAModels;
	}
}